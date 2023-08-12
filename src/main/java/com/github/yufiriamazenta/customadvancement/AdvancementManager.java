
package com.github.yufiriamazenta.customadvancement;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import crypticlib.config.impl.YamlConfigWrapper;
import crypticlib.util.FileUtil;
import crypticlib.util.MsgUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class AdvancementManager {

    private static File advancementsFolder;
    private static final Map<String, YamlConfigWrapper> advancementsConfigMap = new ConcurrentHashMap<>();
    private static final List<NamespacedKey> advancementList = new ArrayList<>();

    @SuppressWarnings("deprecation")
    public static Advancement loadFromConfig(String id, ConfigurationSection config) {
        NamespacedKey namespacedKey;
        try {
            namespacedKey = new NamespacedKey(CustomAdvancement.getInstance(), id);
        } catch (IllegalArgumentException e) {
            Map<String, String> map = new HashMap<>();
            map.put("%prefix%", CustomAdvancement.getInstance().getPrefix());
            MsgUtil.info(
                    CustomAdvancement.getInstance().getLangFile().getConfig().getString("advancement.load_failed.invalid_key", "advancement.load_failed.invalid_key"),
                    map);
            throw new RuntimeException(e);
        }
        advancementList.add(namespacedKey);
        JsonObject rootJson = new JsonObject();
        if (config.getString("parent") != null) {
            rootJson.addProperty("parent", config.getString("parent"));
        }

        //显示数据json
        JsonObject displayJson = new JsonObject();
        JsonObject iconJson = new JsonObject();
        iconJson.addProperty("item", config.getString("display.icon"));
        displayJson.add("icon", iconJson);
        displayJson.addProperty("title", MsgUtil.color(config.getString("display.title", id)));
        displayJson.addProperty("description", MsgUtil.color(config.getString("display.description", id)));
        displayJson.addProperty("hidden", config.getBoolean("display.hidden", false));
        displayJson.addProperty("frame", config.getString("display.frame", "task"));
        if (config.getString("display.background") != null)
            displayJson.addProperty("background", config.getString("display.background", "minecraft:textures/gui/advancements/backgrounds/stone.png"));
        displayJson.addProperty("show_toast", config.getBoolean("display.show_toast", true));
        displayJson.addProperty("announce_to_chat", config.getBoolean("display.announce_to_chat", true));
        rootJson.add("display", displayJson);

        //准则列表json
        JsonObject criteriaJson = new JsonObject();
        JsonObject impJson = new JsonObject();
        impJson.addProperty("trigger", "minecraft:impossible");
        criteriaJson.add("imp", impJson);
        rootJson.add("criteria", criteriaJson);

        //需要完成的准则列表
        JsonArray requirementsJsonArr = new JsonArray();
        JsonArray impJsonArr = new JsonArray();
        impJsonArr.add("imp");
        requirementsJsonArr.add(impJsonArr);
        rootJson.add("requirements", requirementsJsonArr);

        String advancementJsonStr = rootJson.toString();
        return Bukkit.getUnsafe().loadAdvancement(namespacedKey, advancementJsonStr);
    }

    public static void loadAdvancements() {
        advancementsFolder = new File(CustomAdvancement.getInstance().getDataFolder(), "advancements");
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
                advancementsConfigMap.put(key, advancementConfig);
                loadFromConfig(key, advancementConfig.getConfig());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void disableAdvancements() {
        for (NamespacedKey advancement : AdvancementManager.getAdvancementList()) {
            Bukkit.getUnsafe().removeAdvancement(advancement);
        }
    }

    public static void reloadAdvancements() {
        disableAdvancements();
        advancementsConfigMap.clear();
        advancementList.clear();
        Bukkit.reloadData();
        loadAdvancements();
    }

    public static Map<String, YamlConfigWrapper> getAdvancementsConfigMap() {
        return advancementsConfigMap;
    }

    public static File getAdvancementsFolder() {
        return advancementsFolder;
    }

    public static List<NamespacedKey> getAdvancementList() {
        return advancementList;
    }
}
