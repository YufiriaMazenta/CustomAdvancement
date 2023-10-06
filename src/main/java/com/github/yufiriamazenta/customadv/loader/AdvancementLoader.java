package com.github.yufiriamazenta.customadv.loader;

import com.github.yufiriamazenta.customadv.AdvancementsCache;
import com.github.yufiriamazenta.customadv.CustomAdvancement;
import com.google.gson.JsonObject;
import crypticlib.config.impl.YamlConfigWrapper;
import crypticlib.util.FileUtil;
import crypticlib.util.JsonUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public enum AdvancementLoader {

    INSTANCE;

    private final File advancementsFolder;
    private final Map<String, Object> advancementFiles;
    private AdvancementLoadTree advancementLoadTree;

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
                        advancementFiles.put(CustomAdvancement.getInstance().getAdvancementNamespaceJson() + ":" + key, JsonUtil.getGson().fromJson(FileUtils.readFileToString(file, "UTF-8"), JsonObject.class));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                case "yml", "yaml" -> {
                    advancementFiles.put(CustomAdvancement.getInstance().getAdvancementNamespaceYaml() + ":" + key, new YamlConfigWrapper(file));
                }
            }
        }
        advancementLoadTree = new AdvancementLoadTree(advancementFiles);
        advancementLoadTree.load();
    }

    public void unloadAdvancements() {
        advancementLoadTree.unload();
    }

    public void reloadAdvancements() {
        unloadAdvancements();
        advancementFiles.clear();
        AdvancementsCache.getAdvancementWrapperMap().clear();
        loadAdvancements();
        CustomAdvancement.getInstance().getAdvancementManager().reloadAdvancementTree();
        CustomAdvancement.getInstance().getAdvancementManager().reloadPlayerAdvancements();
    }

    public Map<String, Object> getAdvancementFiles() {
        return advancementFiles;
    }

    public AdvancementLoadTree getAdvancementLoadTree() {
        return advancementLoadTree;
    }

    public String getAdvancementKeyFromFile(File file) {
        String key = file.getPath().substring(advancementsFolder.getPath().length() + 1);
        key = key.replace("\\", "/");
        int lastDotIndex = key.lastIndexOf(".");
        key = key.substring(0, lastDotIndex);
        return key;
    }

}
