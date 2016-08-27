package com.softwhistle.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PackageClassScanner
{
    private static final Logger LOG = LoggerFactory.getLogger(PackageClassScanner.class);

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
        List<Class> classes = new ArrayList<Class>();
        while (resources.hasMoreElements())
        {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("jar")) {
                LOG.info("Looking in JAR: {}", resource.toString());
                findClasses((JarURLConnection) resource.openConnection(), packageName, classes);
            }
            else if (resource.getProtocol().equals("file")) {
                findClasses(new File(resource.getFile()), packageName, classes);
            }
        }
        for (File directory : packageDirectories) {
            ;
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
    private void findClasses(File directory, String packageName, List<Class> toAppendTo)
        throws ClassNotFoundException
    {
        if (! directory.exists()) {
            return;
        }
        for (File classpathFile: directory.listFiles())
        {
            if (classpathFile.isDirectory()) {
                if (classpathFile.getName().contains(".")) throw new RuntimeException("Classpath directory cannot have period in its name.");
                findClasses(classpathFile, packageName + "." + classpathFile.getName(), toAppendTo);
            }
            else if (classpathFile.getName().endsWith(".class")) {
                toAppendTo.add(Class.forName(packageName + '.' + classpathFile.getName().substring(0, classpathFile.getName().length() - 6)));
            }
        }
    }

    /**
     * 
     */
    private void findClasses(JarURLConnection jar, String packageName, List<Class> toAppendTo)
        throws IOException, ClassNotFoundException
    {
        Enumeration<JarEntry> entries = jar.getJarFile().entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (entry.isDirectory()) continue;
            JarEntryClassParse entryParse = new JarEntryClassParse(entry);
            if (! entryParse.isClassEntry) continue;
            if (entryParse.packageName.startsWith(packageName)) {
                LOG.debug("Class entry matching requested package: {}", entryParse.fullClassName());
                toAppendTo.add(entryParse.classOfEntry());
            }
        }
    }

    private static class JarEntryClassParse
    {
        public final String packageName;
        public final String className;
        public final boolean isClassEntry;

        public JarEntryClassParse(JarEntry entry) {
            List<String> slashTokens = Arrays.asList(entry.getName().split("/"));
            List<String> packageTokens = slashTokens.subList(0, slashTokens.size() - 1);
            packageName = packageTokens.stream().collect(Collectors.joining("."));
            String classNameToken = slashTokens.get(slashTokens.size() - 1);
            isClassEntry = classNameToken.endsWith(".class");
            if (isClassEntry) className = classNameToken.substring(0, classNameToken.length() - 6);
            else className = null;
        }

        public Class classOfEntry() throws ClassNotFoundException {
            return Class.forName(fullClassName());
        }

        public String fullClassName() {
            return String.format("%s.%s", packageName, className);
        }
    }
}
