package com.github.yufiriamazenta.customadvancement.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import crypticlib.util.MsgUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface IAdvancementManager {

    void loadAdvancements(Map<ResourceLocation, Advancement.Builder> advancements);

    default void loadAdvancement(ResourceLocation key, Advancement.Builder advancement) {
        loadAdvancements(Map.of(key, advancement));
    }

    default void loadAdvancement(String namespace, String key, Advancement.Builder advancement) {
        loadAdvancement(new ResourceLocation(namespace, key.toLowerCase()), advancement);
    }

    default void loadAdvancement(String key, Advancement.Builder advancement) {
        loadAdvancement("custom_advancement", key, advancement);
    }

    default void loadAdvancement(String key, ConfigurationSection config) {
        JsonObject rootJson = new JsonObject();
        if (config.getString("parent") != null) {
            rootJson.addProperty("parent", config.getString("parent"));
        }

        //显示数据json
        JsonObject displayJson = new JsonObject();
        JsonObject iconJson = new JsonObject();
        iconJson.addProperty("item", config.getString("display.icon"));
        displayJson.add("icon", iconJson);
        displayJson.addProperty("title", MsgUtil.color(config.getString("display.title", key)));
        displayJson.addProperty("description", MsgUtil.color(config.getString("display.description", key)));
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
        Advancement.Builder advancement = Advancement.Builder.fromJson(rootJson, null);
        loadAdvancement(key, advancement);
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

    default void reloadAdvancements() {
        for (ServerPlayer player : MinecraftServer.getServer().getPlayerList().players) {
            player.getAdvancements().save();
            player.getAdvancements().reload(MinecraftServer.getServer().getAdvancements());
        }
    }

}