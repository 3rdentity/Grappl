package io.grappl.client.impl.plugin;

import io.grappl.client.api.ApplicationMode;
import io.grappl.client.impl.Application;

import java.io.File;

public class PluginManager {

    public static void main(String[] args) {

        Application.create(null, ApplicationMode.NORMAL);
        setupAndLoad();
    }

    public static void setupAndLoad() {
        createDirectory();
        PluginLoader pluginLoader = new PluginLoader("plugins/");
        pluginLoader.load();
    }

    public static void createDirectory() {
        File file = new File("plugins/");
        file.mkdirs();
    }
}
