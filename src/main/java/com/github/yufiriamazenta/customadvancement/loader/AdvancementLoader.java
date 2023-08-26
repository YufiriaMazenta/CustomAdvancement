package com.github.yufiriamazenta.customadvancement.loader;

import com.github.yufiriamazenta.customadvancement.CustomAdvancement;
import crypticlib.config.impl.YamlConfigWrapper;
import crypticlib.util.FileUtil;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public enum AdvancementLoader {

    INSTANCE;

    private final File advancementsFolder;
    private final Map<String, YamlConfigWrapper> advancementConfigs;
    private AdvancementLoadTree loadTree;

    AdvancementLoader() {
        advancementConfigs = new ConcurrentHashMap<>();
        advancementsFolder = new File(CustomAdvancement.getInstance().getDataFolder(), "advancements");
    }

    public void loadAdvancements() {
        if (!advancementsFolder.exists()) {
            boolean mkdirResult = advancementsFolder.mkdir();
            if (!mkdirResult)
                return;
        }
        List<File> allFiles = FileUtil.getAllFiles(advancementsFolder, Pattern.compile("(.yml|.yaml)$"));
        if (allFiles.size() < 1) {
            CustomAdvancement.getInstance().saveResource("advancements/example.yml", false);
            allFiles.add(new File(advancementsFolder, "example.yml"));
        }
        for (File file : allFiles) {
            String key = file.getPath().substring(advancementsFolder.getPath().length() + 1);
            key = key.replace("\\", "/");
            int lastDotIndex = key.lastIndexOf(".");
            key = key.substring(0, lastDotIndex);
            advancementConfigs.put("custom_advancement:" + key, new YamlConfigWrapper(file));
        }
        loadTree = new AdvancementLoadTree(advancementConfigs);
        loadTree.load();
    }

    public void unloadAdvancements() {
        loadTree.unload();
    }

    public void reloadAdvancements() {
        unloadAdvancements();
        advancementConfigs.clear();
        CustomAdvancement.getInstance().getAdvancementManager().getEditableAdvancements().clear();
        loadAdvancements();
        CustomAdvancement.getInstance().getAdvancementManager().reloadAdvancementTree();
        CustomAdvancement.getInstance().getAdvancementManager().reloadPlayerAdvancements();
    }

    public Map<String, YamlConfigWrapper> getAdvancementConfigs() {
        return advancementConfigs;
    }

    public AdvancementLoadTree getLoadTree() {
        return loadTree;
    }

}
