package com.github.yufiriamazenta.customadvancement.manager;

import com.github.yufiriamazenta.customadvancement.util.ItemUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import crypticlib.item.Item;
import crypticlib.util.ItemUtil;
import crypticlib.util.JsonUtil;
import crypticlib.util.MsgUtil;
import crypticlib.util.YamlConfigUtil;
import io.netty.util.SuppressForbidden;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public interface IAdvancementManager {

    void loadAdvancements(Map<ResourceLocation, Advancement.Builder> advancements);

    default void loadAdvancementsJson(Map<ResourceLocation, JsonObject> advancementsJsonMap) {
        Map<ResourceLocation, Advancement.Builder> advancements = new HashMap<>();
        for (ResourceLocation key : advancementsJsonMap.keySet()) {
            advancements.put(key, json2Advancement(key, advancementsJsonMap.get(key)));
        }
        loadAdvancements(advancements);
    }

    default void loadAdvancement(ResourceLocation key, Advancement.Builder advancement) {
        loadAdvancements(Map.of(key, advancement));
        getEditableAdvancements().add(key.toString());
    }


    default void loadAdvancement(String key, Advancement.Builder advancement) {
        loadAdvancement(new ResourceLocation(key), advancement);
    }

    default void loadAdvancementJson(String key, JsonObject advancementJson) {
        loadAdvancement(key, json2Advancement(new ResourceLocation(key), advancementJson));
    }

    default void loadAdvancement(String key, ConfigurationSection config) {
        JsonObject advancementJson = config2Json(config);
        loadAdvancementJson(key, advancementJson);

    }

    void removeAdvancements(Set<ResourceLocation> keySet);

    default void removeAdvancement(ResourceLocation key) {
        removeAdvancements(Set.of(key));
        getEditableAdvancements().remove(key.toString());
    }

    default void removeAdvancement(String key) {
        removeAdvancement(new ResourceLocation(key));
    }

    void reloadPlayerAdvancements();

    void reloadAdvancementTree();

    boolean grantAdvancement(Player player, ResourceLocation key);

    default boolean grantAdvancement(Player player, String key) {
        return grantAdvancement(player, new ResourceLocation(key));
    }

    boolean revokeAdvancement(Player player, ResourceLocation key);

    default boolean revokeAdvancement(Player player, String key) {
        return revokeAdvancement(player, new ResourceLocation(key));
    }

    Advancement.Builder json2Advancement(ResourceLocation key, JsonObject advancementJson);

    default JsonObject config2Json(ConfigurationSection config) {
        JsonObject rootJson = new JsonObject();
        if (config.getString("parent") != null) {
            rootJson.addProperty("parent", config.getString("parent"));
        }

        //显示数据json
        JsonObject displayJson = new JsonObject();
        JsonObject iconJson = new JsonObject();
        String item = config.getString("display.icon", "stone");
        ItemStack itemStack = ItemUtils.matchItem(item);
        if (itemStack.hasItemMeta()) {
            iconJson.addProperty("nbt", Item.fromBukkitItem(itemStack).getNbtTag().toJsonStr());
        }
        iconJson.addProperty("item", itemStack.getType().getKey().toString());
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

        Gson gson = JsonUtil.getGson();
        //准则列表json
        ConfigurationSection criteria = config.getConfigurationSection("criteria.vanilla");
        JsonObject criteriaJson;
        if (criteria != null) {
            criteriaJson = JsonUtil.configSection2Json(criteria);
        } else {
            criteriaJson = new JsonObject();
            JsonObject impJson = new JsonObject();
            impJson.addProperty("trigger", "minecraft:impossible");
            criteriaJson.add("imp", impJson);
        }
        rootJson.add("criteria", criteriaJson);

        //需要完成的准则列表
        List<?> requirements = config.getList("requirements.vanilla");
        if (requirements != null && requirements.size() >= 1) {
            JsonArray requirementsJsonArr = gson.fromJson(gson.toJson(YamlConfigUtil.configList2List(requirements)), JsonArray.class);
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

    List<String> getAdvancements();

    List<String> getEditableAdvancements();
}