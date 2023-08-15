package com.github.yufiriamazenta.customadvancement.manager.impl;

import com.github.yufiriamazenta.customadvancement.manager.IAdvancementManager;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;

public class V1_19_R1AdvancementManager implements IAdvancementManager {

    @Override
    public void loadAdvancements(Map<ResourceLocation, Advancement.Builder> advancements) {
        MinecraftServer.getServer().getAdvancements().advancements.add(advancements);
    }

    @Override
    public void removeAdvancements(Set<ResourceLocation> keySet, boolean reload) {
        MinecraftServer.getServer().getAdvancements().advancements.remove(keySet);
        if (reload) {
            reloadAdvancements();
        }
    }

    @Override
    public void reloadAdvancements() {
        for (ServerPlayer player : MinecraftServer.getServer().getPlayerList().players) {
            player.getAdvancements().save();
            player.getAdvancements().reload(MinecraftServer.getServer().getAdvancements());
        }
    }

    @Override
    public boolean grantAdvancement(Player player, ResourceLocation key) {
        Advancement advancement = MinecraftServer.getServer().getAdvancements().getAdvancement(key);
        if (advancement == null) {
            return false;
        }
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        AdvancementProgress advancementProgress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
        if (!advancementProgress.isDone()) {
            for (String string : advancementProgress.getRemainingCriteria()) {
                serverPlayer.getAdvancements().award(advancement, string);
            }
        }
        return true;
    }

    @Override
    public boolean revokeAdvancement(Player player, ResourceLocation key) {
        Advancement advancement = MinecraftServer.getServer().getAdvancements().getAdvancement(key);
        if (advancement == null) {
            return false;
        }
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        AdvancementProgress advancementProgress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
        if (advancementProgress.hasProgress()) {
            for (String string : advancementProgress.getCompletedCriteria()) {
                serverPlayer.getAdvancements().revoke(advancement, string);
            }

        }
        return true;
    }

    @Override
    public Advancement.Builder json2Advancement(JsonObject advancementJson) {
        return Advancement.Builder.fromJson(advancementJson, null);
    }

}
