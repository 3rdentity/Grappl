package io.grappl.client.impl.plugin;

import io.grappl.client.impl.Application;
import io.grappl.client.impl.GrapplDataFile;

import java.io.DataInputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

public class PluginLoader {

    private String directory;
    private int pluginsLoaded = 0;
    private String extension = "jar";
    private Set<String> loadedPlugins;

    public PluginLoader(String directory) {
        this.directory = GrapplDataFile.getOSSpecificLocation() + directory;
        loadedPlugins = new HashSet<String>();
    }

    public PluginLoader(String directory, String fileExtension) {
        this.directory = directory;
        this.extension = fileExtension;
        loadedPlugins = new HashSet<String>();
    }

    /**
     * @return the directory that plugins will load from
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * Returns the file extension of the type of plugin this object loads.
     * Defaults to .jar.
     *
     * @return the file extension of plugins loaded
     */
    public String getExtension() {
        return extension;
    }

    /**
     * @return the number of plugins loaded.
     */
    public int getPluginsLoaded() {
        return pluginsLoaded;
    }

    /**
     * Load the plugins
     */
    public void load() {
        directory = directory.replaceAll(" Grappl", "Grappl");
        File[] files = new File(directory).listFiles();


//        System.out.println(new File(directory).getAbsoluteFile());
        try {
            if(files != null) {
//                System.out.println(".jars found: " + files.length);
                for (File file : files) {
                    try {
//                        System.out.println("Found plugin: " + file.getName());
                        String[] pluginName = file.getName().split("\\.");

                        if (pluginName[1].equalsIgnoreCase(extension)) {
                            try {
                                ClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()});
                                String mainClassLocation = "";

                                DataInputStream plgInfoStream = new DataInputStream(classLoader.getResourceAsStream("plginfo.dat"));
                                //noinspection deprecation
                                mainClassLocation = plgInfoStream.readLine();

                                if (!loadedPlugins.contains(mainClassLocation)) {
                                    Class theClass = classLoader.loadClass(mainClassLocation);

                                    try {
                                        Object the = theClass.newInstance();
                                        Method m = theClass.newInstance().getClass().getMethod("main");
                                        m.invoke(the);
                                        pluginsLoaded++;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    loadedPlugins.add(mainClassLocation);
                                }

                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Application.getLog().log(pluginsLoaded + " plugin(s) loaded");
    }
}
