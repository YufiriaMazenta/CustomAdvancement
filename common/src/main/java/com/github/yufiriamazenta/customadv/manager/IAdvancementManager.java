package com.github.yufiriamazenta.customadv.manager;


import com.github.yufiriamazenta.customadv.adv.AbstractAdvancementWrapper;
import com.github.yufiriamazenta.customadv.util.ItemUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import crypticlib.nms.item.ItemManager;
import crypticlib.util.JsonUtil;
import crypticlib.util.MsgUtil;
import crypticlib.util.YamlConfigUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IAdvancementManager {

    AbstractAdvancementWrapper advancementWrapper(String key, JsonObject jsonObject);

    AbstractAdvancementWrapper advancementWrapper(String key);

    default AbstractAdvancementWrapper advancementWrapper(String key, ConfigurationSection config) {
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
            iconJson.addProperty("nbt", ItemManager.item(itemStack).nbtCompound().toJson().toString());
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
        ConfigurationSection criteria = config.getConfigurationSection("criteria");
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
        List<?> requirements = config.getList("requirements");
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
        return advancementWrapper(key, rootJson);
    }

    void reloadAdvancementTree();

    void reloadPlayerAdvancements();

}