package com.github.yufiriamazenta.customadvancement.manager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import crypticlib.util.MsgUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;

import java.util.*;

public interface IAdvancementManager {

    void loadAdvancements(Map<ResourceLocation, Advancement.Builder> advancements);

    default void loadAdvancementsJson(Map<ResourceLocation, JsonObject> advancementsJsonMap) {
        Map<ResourceLocation, Advancement.Builder> advancements = new HashMap<>();
        for (ResourceLocation key : advancementsJsonMap.keySet()) {
            advancements.put(key, json2Advancement(advancementsJsonMap.get(key)));
        }
        loadAdvancements(advancements);
    }

    default void loadAdvancement(ResourceLocation key, Advancement.Builder advancement) {
        loadAdvancements(Map.of(key, advancement));
    }

    default void loadAdvancement(String namespace, String key, Advancement.Builder advancement) {
        loadAdvancement(new ResourceLocation(namespace, key.toLowerCase()), advancement);
    }

    default void loadAdvancement(String key, Advancement.Builder advancement) {
        loadAdvancement("custom_advancement", key, advancement);
    }

    default void loadAdvancementJson(String key, JsonObject advancementJson) {
        loadAdvancement(key, json2Advancement(advancementJson));
    }

    default void loadAdvancement(String key, ConfigurationSection config) {
        JsonObject advancementJson = config2Json(config);
        loadAdvancementJson(key, advancementJson);
    }

    void removeAdvancements(Set<ResourceLocation> keySet, boolean reload);

    default void removeAdvancement(ResourceLocation key, boolean reload) {
        removeAdvancements(Set.of(key), reload);
    }

    default void removeAdvancement(String namespace, String key, boolean reload) {
        removeAdvancement(new ResourceLocation(namespace, key), reload);
    }

    default void removeAdvancement(String key, boolean reload) {
        removeAdvancement("custom_advancement", key, reload);
    }

    void reloadAdvancements();

    boolean grantAdvancement(Player player, ResourceLocation key);

    default boolean grantAdvancement(Player player, String key) {
        return grantAdvancement(player, new ResourceLocation("custom_advancement", key));
    }

    boolean revokeAdvancement(Player player, ResourceLocation key);

    default boolean revokeAdvancement(Player player, String key) {
        return revokeAdvancement(player, new ResourceLocation("custom_advancement", key));
    }

    Advancement.Builder json2Advancement(JsonObject advancementJson);

    default JsonObject config2Json(ConfigurationSection config) {
        JsonObject rootJson = new JsonObject();
        if (config.getString("parent") != null) {
            rootJson.addProperty("parent", config.getString("parent"));
        }

        //显示数据json
        JsonObject displayJson = new JsonObject();
        JsonObject iconJson = new JsonObject();
        iconJson.addProperty("item", config.getString("display.icon"));
        displayJson.add("icon", iconJson);
        displayJson.addProperty("title", MsgUtil.color(config.getString("display.title", "Unset title")));
        displayJson.addProperty("description", MsgUtil.color(config.getString("display.description", "Unset description")));
        displayJson.addProperty("hidden", config.getBoolean("display.hidden", false));
        displayJson.addProperty("frame", config.getString("display.frame", "task"));
        if (config.getString("display.background") != null)
            displayJson.addProperty("background", config.getString("display.background", "minecraft:textures/gui/advancements/backgrounds/stone.png"));
        displayJson.addProperty("show_toast", config.getBoolean("display.show_toast", true));
        displayJson.addProperty("announce_to_chat", config.getBoolean("display.announce_to_chat", true));
        rootJson.add("display", displayJson);

        Gson gson = new Gson();
        //准则列表json
        ConfigurationSection criteria = config.getConfigurationSection("criteria");
        if (criteria != null) {
            JsonObject criteriaJson = gson.fromJson(gson.toJson(configSection2Map(criteria)), JsonObject.class);
            rootJson.add("criteria", criteriaJson);
        }

        //需要完成的准则列表
        List<?> requirements = config.getList("requirements");
        if (requirements != null && requirements.size() >= 1) {
            JsonArray requirementsJsonArr = gson.fromJson(gson.toJson(requirements), JsonArray.class);
            rootJson.add("requirements", requirementsJsonArr);
        }

        //需要完成的准则列表
        ConfigurationSection rewards = config.getConfigurationSection("rewards");
        if (rewards != null) {
            JsonObject rewardsJson = new JsonObject();
            if (rewards.getDouble("exp", 0) != 0) {
                rewardsJson.addProperty("experience", rewards.getDouble("exp"));
            }
            if (rewards.getStringList("loot").size() >= 1) {
                JsonArray loot = new JsonArray();
                for (String lootName : rewards.getStringList("loot")) {
                    loot.add(lootName);
                }
                rewardsJson.add("loot", loot);
            }
            if (rewards.getStringList("recipes").size() >= 1) {
                JsonArray recipes = new JsonArray();
                for (String recipeName : rewards.getStringList("recipes")) {
                    recipes.add(recipeName);
                }
                rewardsJson.add("recipes", recipes);
            }
            rootJson.add("rewards", rewardsJson);
        }

        return rootJson;
    }

    default Map<String, Object> configSection2Map(ConfigurationSection configSection) {
        Map<String, Object> map = new HashMap<>();
        for (String key : configSection.getKeys(false)) {
            if (configSection.isConfigurationSection(key)) {
                map.put(key, configSection2Map(configSection.getConfigurationSection(key)));
            } else {
                map.put(key, configSection.get(key));
            }
        }
        return map;
    }

}