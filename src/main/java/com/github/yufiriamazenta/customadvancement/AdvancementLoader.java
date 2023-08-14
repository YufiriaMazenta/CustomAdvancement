package com.github.yufiriamazenta.customadvancement;

import crypticlib.config.impl.YamlConfigWrapper;
import crypticlib.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public enum AdvancementLoader {

    INSTANCE;

    private final File advancementsFolder;
    private final List<String> advancements;

    AdvancementLoader() {
        advancementsFolder = new File(CustomAdvancement.getInstance().getDataFolder(), "advancements");
        advancements = new ArrayList<>();
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
        for (File advancementFile : allFiles) {
            String key = advancementFile.getPath().substring(advancementsFolder.getPath().length() + 1);
            key = key.replace("\\", "/");
            int lastDotIndex = key.lastIndexOf(".");
            key = key.substring(0, lastDotIndex);
            try {
                YamlConfigWrapper advancementConfig = new YamlConfigWrapper(advancementFile);
                advancements.add(key);
                CustomAdvancement.getInstance().getAdvancementManager().loadAdvancement(key, advancementConfig.getConfig());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void unloadAdvancements() {
        for (String advancement : advancements) {
            CustomAdvancement.getInstance().getAdvancementManager().removeAdvancement(advancement, false);
        }
    }

    public void reloadAdvancements() {
        unloadAdvancements();
        advancements.clear();
        loadAdvancements();
        CustomAdvancement.getInstance().getAdvancementManager().reloadAdvancements();
    }

    public List<String> getAdvancements() {
        return advancements;
    }

}
