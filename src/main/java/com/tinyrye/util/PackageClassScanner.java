package com.tinyrye.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class PackageClassScanner
{
    /**
     * Pulled from <a href="https://dzone.com/articles/get-all-classes-within-package"></a>
     * Thanks, Victor Tatai!
     */
    public List<Class> findClasses(String packageName)
        throws ClassNotFoundException, IOException
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) throw new RuntimeException("No class loader found!");
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> packageDirectories = new ArrayList<File>();
        // there could be multiple directories housing classes with this package
        // for example src/main/java/... and src/test/java/...
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            packageDirectories.add(new File(resource.getFile()));
        }
        List<Class> classes = new ArrayList<Class>();
        for (File directory : packageDirectories) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }
    
    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private List<Class> findClasses(File directory, String packageName)
        throws ClassNotFoundException
    {
        List<Class> classes = new ArrayList<Class>();
        if (! directory.exists()) {
            return classes;
        }
        for (File classpathFile: directory.listFiles())
        {
            if (classpathFile.isDirectory()) {
                if (classpathFile.getName().contains(".")) throw new RuntimeException("Classpath directory cannot have period in its name.");
                classes.addAll(findClasses(classpathFile, packageName + "." + classpathFile.getName()));
            }
            else if (classpathFile.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + classpathFile.getName().substring(0, classpathFile.getName().length() - 6)));
            }
        }
        return classes;
    }
}