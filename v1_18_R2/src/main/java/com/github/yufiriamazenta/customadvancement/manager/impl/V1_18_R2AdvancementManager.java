
package com.github.yufiriamazenta.customadvancement.manager.impl;


import com.github.yufiriamazenta.customadvancement.manager.IAdvancementManager;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.Set;

public class V1_18_R2AdvancementManager implements IAdvancementManager {

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
    public Advancement.Builder json2Advancement(JsonObject advancementJson) {
        return Advancement.Builder.fromJson(advancementJson, null);
    }

}
