/*
 *  java-sandbox
 *  Copyright (c) 2012 datenwerke Jan Albrecht
 *  http://www.datenwerke.net
 *
 *  This file is part of the java-sandbox: https://sourceforge.net/p/dw-sandbox/
 *
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package net.datenwerke.sandbox;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import net.datenwerke.sandbox.securitypermissions.SandboxRuntimePermission;

import org.apache.commons.io.IOUtils;

import sun.misc.Resource;
import sun.misc.URLClassPath;


/**
 * The {@link ClassLoader} used by the java-sandbox library.
 * This classloader takes care of loading classes with the correct class loaders
 * and much more. This can be seen to some extend as the heart of the
 * java-sandbox library.
 *
 * @author Arno Mittelbach
 */
final public class SandboxClassLoader extends ClassLoader {

    private static final Logger logger = Logger.getLogger(SandboxClassLoader.class.getName());

    public static final String DEFAULT_CODESOURCE_PREFIX = "/java-sandbox-default-codesource";

    /**
     * 绕行的类
     */
    private static final HashSet<String> BYPASS_CLASSES = new HashSet<>();

    static {
        BYPASS_CLASSES.add("net.datenwerke.sandbox.SandboxClassLoader");
        BYPASS_CLASSES.add("net.datenwerke.sandbox.SandboxServiceImpl");

        BYPASS_CLASSES.add("net.datenwerke.sandbox.SandboxedCallResult");
        BYPASS_CLASSES.add("net.datenwerke.sandbox.SandboxedCallResultImpl");
        BYPASS_CLASSES.add("net.datenwerke.sandbox.SandboxService");
        BYPASS_CLASSES.add("net.datenwerke.sandbox.SandboxContext");

        BYPASS_CLASSES.add("net.datenwerke.sandbox.handlers.SandboxHandler");
        BYPASS_CLASSES.add("net.datenwerke.sandbox.handlers.BadThreadKillHandler");
        BYPASS_CLASSES.add("net.datenwerke.sandbox.handlers.ContextRegisteredHandler");

        BYPASS_CLASSES.add("net.datenwerke.sandbox.permissions.FileEqualsPermission");
        BYPASS_CLASSES.add("net.datenwerke.sandbox.permissions.FilePermission");
        BYPASS_CLASSES.add("net.datenwerke.sandbox.permissions.FilePrefixPermission");
        BYPASS_CLASSES.add("net.datenwerke.sandbox.permissions.FileRegexPermission");
        BYPASS_CLASSES.add("net.datenwerke.sandbox.permissions.FileSuffixPermission");
        BYPASS_CLASSES.add("net.datenwerke.sandbox.permissions.StackEntry");
        BYPASS_CLASSES.add("net.datenwerke.sandbox.permissions.ClassPermission");
        BYPASS_CLASSES.add("net.datenwerke.sandbox.permissions.PackagePermission");
        BYPASS_CLASSES.add("net.datenwerke.sandbox.permissions.SecurityPermission");

        BYPASS_CLASSES.add("net.datenwerke.sandbox.util.VariableAssignment");
    }

    private final SandboxSecurityManager securityManager;

    private Map<String, SandboxClassLoader> subLoaderCache = new HashMap<>();
    private Map<String, SandboxClassLoader> subLoaderPrefixCache = new HashMap<>();
    private Map<URLClassPath, SandboxClassLoader> subLoaderByJar = new HashMap<>();

    private boolean debug = false;

    private SandboxContext context = new SandboxContext();

    private String name = "";

    private URLClassPath whitelistedUcp;
    private URLClassPath bypassUcp;

    private Collection<String> classesToLoadWithParent = new HashSet<>();
    private Collection<String> classesByPrefixToLoadWithParent = new HashSet<>();

    private Collection<String> classesToLoadDirectly = new HashSet<>();
    private Collection<String> classesByPrefixToLoadDirectly = new HashSet<>();

    private boolean hasSubLoaders;

    private String codesource;

    private boolean removeFinalizers;

    private SandboxClassLoaderEnhancer enhancer;

    private ClassLoader parent;

    /**
     * Instantiates a new SandboxClassLoader with the current ClassLoader as parent.
     */
    public SandboxClassLoader() {
        this(SandboxService.class.getClassLoader());
    }

    /**
     * Instantiates a new SandboxClassLoader with the given ClassLoader as parent.
     *
     * @param parent
     */
    public SandboxClassLoader(ClassLoader parent) {
        this(parent, System.getSecurityManager());
    }

    /**
     * Instantiates a new SandboxClassLoader with the given ClassLoader as parent and using the given security manager.
     *
     * @param parent
     * @param securityManager
     */
    public SandboxClassLoader(ClassLoader parent, SecurityManager securityManager) {
        super(parent);
        this.parent = parent;
        this.securityManager = (SandboxSecurityManager) securityManager;
    }

    /**
     * Initializes this classloader with the config provided by the SandboxContext
     *
     * @param context
     */
    public void init(SandboxContext context) {
        securityManager.checkPermission(new SandboxRuntimePermission("initSandboxLoader"));

        /* name */
        this.name = context.getName();

        /* jars */
        if (null != context.getWhitelistedJars() && !context.getWhitelistedJars().isEmpty()) {
            whitelistedUcp = new URLClassPath(context.getWhitelistedJars().toArray(new URL[]{}));
        } else {
            whitelistedUcp = null;
        }
        if (null != context.getJarsForApplicationLoader() && !context.getJarsForApplicationLoader().isEmpty()) {
            bypassUcp = new URLClassPath(context.getJarsForApplicationLoader().toArray(new URL[]{}));
        } else {
            bypassUcp = null;
        }
        /* load configuration */
        classesToLoadWithParent = new HashSet<>(context.getClassesForApplicationLoader());
        classesToLoadWithParent.addAll(BYPASS_CLASSES);
        classesByPrefixToLoadWithParent = new HashSet<>(context.getClassPrefixesForApplicationLoader());

        classesToLoadDirectly = new HashSet<>(context.getClassesForSandboxLoader());
        classesByPrefixToLoadDirectly = new HashSet<>(context.getClassPrefixesForSandboxLoader());

        /* subLoaders */
        this.hasSubLoaders = !context.getSubLoaderContextByClassMap().isEmpty() ||
                !context.getSubLoaderContextByClassPrefixMap().isEmpty() ||
                !context.getSubLoaderContextByJar().isEmpty();
        if (hasSubLoaders) {
            IdentityHashMap<SandboxContext, SandboxClassLoader> loaderMap = new IdentityHashMap<>();

            for (Entry<String, SandboxContext> e : context.getSubLoaderContextByClassMap().entrySet()) {
                subLoaderCache.put(e.getKey(), initSubLoader(loaderMap, e.getValue()));
            }
            for (Entry<String, SandboxContext> e : context.getSubLoaderContextByClassPrefixMap().entrySet()) {
                subLoaderPrefixCache.put(e.getKey(), initSubLoader(loaderMap, e.getValue()));
            }
            for (Entry<URL, SandboxContext> e : context.getSubLoaderContextByJar().entrySet()) {
                subLoaderByJar.put(new URLClassPath(new URL[]{e.getKey()}), initSubLoader(loaderMap, e.getValue()));
            }
        }

        /* debug */
        this.debug = context.isDebug();

        this.codesource = context.getCodesource();
        if (null == this.codesource) {
            this.codesource = DEFAULT_CODESOURCE_PREFIX.concat("/").concat(null == name || "".equals(name) ? "default" : name).concat("/");
        }
        this.removeFinalizers = context.isRemoveFinalizers();

        this.enhancer = context.getLoaderEnhancer();

        /* store context */
        this.context = context;
    }

    private SandboxClassLoader initSubLoader(IdentityHashMap<SandboxContext, SandboxClassLoader> loaderMap, SandboxContext context) {
        if (loaderMap.containsKey(context)) {
            return loaderMap.get(context);
        }
        SandboxClassLoader subLoader = new SandboxClassLoader(this, securityManager);
        subLoader.init(context);

        loaderMap.put(context, subLoader);

        return subLoader;
    }

    /**
     * Returns the context used to initialize this {@link SandboxClassLoader}
     *
     * @return
     * @see #init(SandboxContext)
     */
    public SandboxContext getContext() {
        getSecurityManager().checkPermission(new SandboxRuntimePermission("getSandboxLoaderContext"));

        return context;
    }

    @Override
    protected Class<?> loadClass(final String name, boolean resolve) throws ClassNotFoundException {
        Class clazz = null;

        if (debug) {
            logger.log(Level.INFO, getName() + "(" + System.identityHashCode(this) + ")" + " about to load class: " + name);
        }
        if (null != enhancer) {
            enhancer.classToBeLoaded(this, name, resolve);
        }
        boolean trustedSource = false;

        if (name.startsWith("java.") || bypassClazz(name)) {
            clazz = super.loadClass(name, resolve);

            /* check if it comes from an available jar */
            if (!name.startsWith("java.") && null != whitelistedUcp) {
                String path = name.replace('.', '/').concat(".class");

                Resource res = whitelistedUcp.getResource(path, false);
                if (res != null) {
                    trustedSource = true;
                }
            }

        } else {
            /* check subcontext */
            if (hasSubLoaders) {
                SandboxClassLoader subLoader = doGetSubLoaderByClassContext(name);
                if (null != subLoader) {
                    return subLoader.loadClass(name, resolve);
                }
            }

            /* check if we have already handeled this class */
            clazz = findLoadedClass(name);
            if (clazz != null) {
                if (null != whitelistedUcp) {
                    String path = name.replace('.', '/').concat(".class");
                    Resource res = whitelistedUcp.getResource(path, false);
                    if (res != null) {
                        trustedSource = true;
                    }
                }
            } else {
                try {
                    String basePath = name.replace('.', '/');
                    String path = basePath.concat(".class");

                    ProtectionDomain domain = null;
                    try {
                        CodeSource codeSource = new CodeSource(new URL("file", "", codesource.concat(basePath)), (java.security.cert.Certificate[]) null);
                        domain = new ProtectionDomain(codeSource, new Permissions(), this, null);
                    } catch (MalformedURLException e) {
                        throw new RuntimeException("Could not create protection domain.");
                    }

                    /* define package */
                    int i = name.lastIndexOf('.');
                    if (i != -1) {
                        String pkgName = name.substring(0, i);
                        java.lang.Package pkg = getPackage(pkgName);
                        if (pkg == null) {
                            definePackage(pkgName, null, null, null, null, null, null, null);
                        }
                    }


                    /* first strategy .. check jars */
                    if (null != whitelistedUcp) {
                        Resource res = whitelistedUcp.getResource(path, false);
                        if (res != null) {
                            byte[] cBytes = enhance(name, res.getBytes());
                            clazz = defineClass(name, cBytes, 0, cBytes.length, domain);
                            trustedSource = true;
                        }
                    }

                    /* load class */
                    if (clazz == null) {
                        InputStream in = null;
                        try {
                            /* we only load from local sources */
                            in = parent.getResourceAsStream(path);
                            byte[] cBytes = null;
                            if (in != null) {
                                cBytes = IOUtils.toByteArray(in);
                            }

                            if (null == cBytes && null != enhancer) {
                                cBytes = enhancer.loadClass(this, name);
                            }

                            if (null == cBytes) {
                                throw new ClassNotFoundException("Could not find " + name);
                            }
                            /* load and define class */
                            cBytes = enhance(name, cBytes);
                            clazz = defineClass(name, cBytes, 0, cBytes.length, domain);
                        } finally {
                            if (null != in) {
                                try {
                                    in.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    /* do we need to resolve */
                    if (resolve) {
                        resolveClass(clazz);
                    }
                } catch (IOException e) {
                    throw new ClassNotFoundException("Could not load " + name, e);
                } catch (Exception e) {
                    throw new ClassNotFoundException("Could not load " + name, e);
                }
            }
        }

        if (!trustedSource && null != clazz && null != securityManager)
            securityManager.checkClassAccess(name);

        if (null != enhancer)
            enhancer.classLoaded(this, name, clazz);

        return clazz;
    }


    private byte[] enhance(String name, byte[] cBytes) throws IOException, RuntimeException, CannotCompileException, NotFoundException {
        if (removeFinalizers) {
            CtClass clazz = new ClassPool().makeClass(new ByteArrayInputStream(cBytes));
            if (!clazz.isInterface()) {
                try {
                    CtMethod method = clazz.getMethod("finalize", "()V");
                    if (null != method && !method.isEmpty()) {
                        clazz.removeMethod(method);
                        cBytes = clazz.toBytecode();
                    }
                } catch (NotFoundException ignore) {
                }
            }
        }
        if (null != enhancer) {
            cBytes = enhancer.enhance(this, name, cBytes);
        }
        return cBytes;

    }

    public SandboxClassLoader getSubLoaderByClassContext(String clazz) {
        getSecurityManager().checkPermission(new SandboxRuntimePermission("getSubLoader"));
        return doGetSubLoaderByClassContext(clazz);
    }

    private SandboxClassLoader doGetSubLoaderByClassContext(String clazz) {
        String path = clazz.replace('.', '/').concat(".class");
        for (Entry<URLClassPath, SandboxClassLoader> e : subLoaderByJar.entrySet()) {
            Resource res = e.getKey().getResource(path, false);
            if (res != null) {
                return e.getValue();
            }
        }

        SandboxClassLoader subLoader = subLoaderCache.get(clazz);
        if (null != subLoader) {
            return subLoader;
        }

        for (String prefix : subLoaderPrefixCache.keySet()) {
            if (clazz.startsWith(prefix)) {
                return subLoaderPrefixCache.get(prefix);
            }
        }

        return null;
    }

    private boolean bypassClazz(String name) {
        if (null != enhancer && enhancer.isLoadClassWithApplicationLoader(name)) {
            return true;
        }

        if (classesToLoadWithParent.contains(name)) {
            return !prohibitBypass(name);
        }

        for (String bp : classesByPrefixToLoadWithParent) {
            if (name.startsWith(bp)) {
                return !prohibitBypass(name);
            }
        }

        /* check if it comes from an available jar */
        if (!name.startsWith("java.") && null != bypassUcp) {
            String path = name.replace('.', '/').concat(".class");

            Resource res = bypassUcp.getResource(path, false);
            if (res != null) {
                return !prohibitBypass(name);
            }
        }

        return false;
    }

    private boolean prohibitBypass(String name) {
        if (classesToLoadDirectly.contains(name)) {
            return true;
        }

        for (String bp : classesByPrefixToLoadDirectly) {
            if (name.startsWith(bp)) {
                return true;
            }
        }
        return false;
    }


    public SandboxClassLoader getSubloaderByName(String name) {
        getSecurityManager().checkPermission(new SandboxRuntimePermission("getSubLoader"));

        return doGetSubloaderByName(name);
    }

    private SandboxClassLoader doGetSubloaderByName(String name) {
        if (name.equals(getName())) {
            return this;
        }

        for (SandboxClassLoader loader : subLoaderByJar.values()) {
            if (name.equals(loader.getName())) {
                return loader;
            }
        }

        for (SandboxClassLoader loader : subLoaderCache.values()) {
            if (name.equals(loader.getName())) {
                return loader;
            }
        }

        for (SandboxClassLoader loader : subLoaderPrefixCache.values()) {
            if (name.equals(loader.getName())) {
                return loader;
            }
        }

        for (SandboxClassLoader loader : subLoaderByJar.values()) {
            SandboxClassLoader subLoader = loader.getSubloaderByName(name);
            if (null != subLoader) {
                return subLoader;
            }
        }

        for (SandboxClassLoader loader : subLoaderCache.values()) {
            SandboxClassLoader subLoader = loader.getSubloaderByName(name);
            if (null != subLoader) {
                return subLoader;
            }
        }

        for (SandboxClassLoader loader : subLoaderPrefixCache.values()) {
            SandboxClassLoader subLoader = loader.getSubloaderByName(name);
            if (null != subLoader) {
                return subLoader;
            }
        }

        return null;
    }

    /**
     * Returns the name of the ClassLoader.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    public SandboxSecurityManager getSecurityManager() {
        securityManager.checkPermission(new SandboxRuntimePermission("getSecurityManager"));

        return securityManager;
    }

    public Class<?> defineClass(String name, byte[] classBytes) {
        return defineClass(name, classBytes, true);
    }

    public Class<?> defineClass(String name, byte[] classBytes, boolean enhanceClass) {
        securityManager.checkPermission(new SandboxRuntimePermission("defineClass"));

        Class<?> clazz = findLoadedClass(name);
        if (null != clazz) {
            return clazz;
        }

        if (enhanceClass) {
            try {
                classBytes = enhance(name, classBytes);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        ProtectionDomain domain = null;
        try {
            CodeSource codeSource = new CodeSource(new URL("file", "", codesource), (java.security.cert.Certificate[]) null);
            domain = new ProtectionDomain(codeSource, new Permissions(), this, null);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not create protection domain.");
        }

        return defineClass(name, classBytes, 0, classBytes.length, domain);
    }

}