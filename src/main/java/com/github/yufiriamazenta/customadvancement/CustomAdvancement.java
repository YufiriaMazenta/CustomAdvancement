package com.github.yufiriamazenta.customadvancement;

import crypticlib.BukkitPlugin;
import crypticlib.config.impl.YamlConfigWrapper;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementFrameType;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.critereon.CriterionTriggerKilled;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public final class CustomAdvancement extends BukkitPlugin implements Listener {

    private static CustomAdvancement INSTANCE;
    private YamlConfigWrapper langFile;
    private AdvancementManager advancementManager;

    @Override
    public void enable() {
        INSTANCE = this;
        saveDefaultConfig();
        loadLangFile();
//        AdvancementManager.loadAdvancements();
        addTest();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void disable() {
//        AdvancementManager.disableAdvancements();
    }

    public void addTest() {
        Advancement.SerializedAdvancement advancement = Advancement.SerializedAdvancement.a().a(
                        Items.A,
                        IChatBaseComponent.a("test"),
                        IChatBaseComponent.a("test"),
                        new MinecraftKey("textures/gui/advancements/backgrounds/adventure.png"),
                        AdvancementFrameType.a,
                        true,
                        true,
                        false
                ).a(AdvancementRequirements.a)
                .a("killed_something", CriterionTriggerKilled.a.c())
                .a("killed_by_something", CriterionTriggerKilled.a.e());
        Map<MinecraftKey, Advancement.SerializedAdvancement> map = new HashMap<>();
        map.put(new MinecraftKey("test", "test"), advancement);
        ((CraftServer) Bukkit.getServer()).getServer().az().c.a(map);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (event.isSneaking())
            return;
        //删除进度
        HashSet<MinecraftKey> minecraftKeys = new HashSet<>();
        minecraftKeys.add(new MinecraftKey("test", "test"));
        ((CraftServer) Bukkit.getServer()).getServer().az().c.a(minecraftKeys);

        //添加进度
        Advancement.SerializedAdvancement advancement = Advancement.SerializedAdvancement.a().a(
                        Items.A,
                        IChatBaseComponent.a("test2"),
                        IChatBaseComponent.a("test2"),
                        new MinecraftKey("textures/gui/advancements/backgrounds/adventure.png"),
                        AdvancementFrameType.a,
                        true,
                        true,
                        false
                ).a(AdvancementRequirements.a)
                .a("killed_something", CriterionTriggerKilled.a.c())
                .a("killed_by_something", CriterionTriggerKilled.a.e());
        Map<MinecraftKey, Advancement.SerializedAdvancement> map = new HashMap<>();
        map.put(new MinecraftKey("test", "test"), advancement);
        ((CraftServer) Bukkit.getServer()).getServer().az().c.a(map);
    }

    private void loadLangFile() {
        langFile = new YamlConfigWrapper(this, "lang.yml");
    }

    public static CustomAdvancement getInstance() {
        return INSTANCE;
    }

    public YamlConfigWrapper getLangFile() {
        return langFile;
    }

    public String getPrefix() {
        return langFile.getConfig().getString("prefix", "&8[&3Custom&bAdvancement&8]&r");
    }

}
