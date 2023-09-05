package com.github.yufiriamazenta.customadvancement.loader;

import com.github.yufiriamazenta.customadvancement.CustomAdvancement;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import crypticlib.config.impl.YamlConfigWrapper;
import crypticlib.util.FileUtil;
import crypticlib.util.JsonUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public enum AdvancementLoader {

    INSTANCE;

    private final File advancementsFolder;
    private final Map<String, Object> advancementFiles;
    private AdvancementLoadTree yamlAdvancementLoadTree;

    AdvancementLoader() {
        advancementFiles = new ConcurrentHashMap<>();
        advancementsFolder = new File(CustomAdvancement.getInstance().getDataFolder(), "advancements");
    }

    public void loadAdvancements() {
        if (!advancementsFolder.exists()) {
            boolean mkdirResult = advancementsFolder.mkdir();
            if (!mkdirResult)
                return;
        }
        List<File> files = FileUtil.getAllFiles(advancementsFolder, Pattern.compile("(.yml|.yaml|.json)$"));
        if (files.size() < 1) {
            CustomAdvancement.getInstance().saveResource("advancements/example.yml", false);
            files.add(new File(advancementsFolder, "example.yml"));
        }
        for (File file : files) {
            String key = getAdvancementKeyFromFile(file);
            switch (file.getPath().substring(file.getPath().lastIndexOf(".") + 1)) {
                case "json" -> {
                    try {
                        advancementFiles.put("custom_advancement:" + key, JsonUtil.getGson().fromJson(FileUtils.readFileToString(file, "UTF-8"), JsonObject.class));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                case "yml", "yaml" -> {
                    advancementFiles.put("custom_advancement:" + key, new YamlConfigWrapper(file));
                }
            }
        }
        yamlAdvancementLoadTree = new AdvancementLoadTree(advancementFiles);
        yamlAdvancementLoadTree.load();
    }

    public void unloadAdvancements() {
        yamlAdvancementLoadTree.unload();
    }

    public void reloadAdvancements() {
        unloadAdvancements();
        advancementFiles.clear();
        CustomAdvancement.getInstance().getAdvancementManager().getEditableAdvancements().clear();
        loadAdvancements();
        CustomAdvancement.getInstance().getAdvancementManager().reloadAdvancementTree();
        CustomAdvancement.getInstance().getAdvancementManager().reloadPlayerAdvancements();
    }

    public Map<String, Object> getAdvancementFiles() {
        return advancementFiles;
    }

    public AdvancementLoadTree getYamlAdvancementLoadTree() {
        return yamlAdvancementLoadTree;
    }

    public String getAdvancementKeyFromFile(File file) {
        String key = file.getPath().substring(advancementsFolder.getPath().length() + 1);
        key = key.replace("\\", "/");
        int lastDotIndex = key.lastIndexOf(".");
        key = key.substring(0, lastDotIndex);
        return key;
    }

}
