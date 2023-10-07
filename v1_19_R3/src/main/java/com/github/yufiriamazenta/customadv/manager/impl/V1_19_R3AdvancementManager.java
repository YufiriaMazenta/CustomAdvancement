package com.github.yufiriamazenta.customadv.manager.impl;

import com.github.yufiriamazenta.customadv.adv.AbstractAdvancementWrapper;
import com.github.yufiriamazenta.customadv.adv.V1_19_R3AdvancementWrapper;
import com.github.yufiriamazenta.customadv.manager.IAdvancementManager;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.advancements.Advancements;
import net.minecraft.server.AdvancementDataPlayer;
import net.minecraft.server.AdvancementDataWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.players.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;

public enum V1_19_R3AdvancementManager implements IAdvancementManager {

    INSTANCE;

    @Override
    public AbstractAdvancementWrapper advancementWrapper(String key, JsonObject jsonObject) {
        return new V1_19_R3AdvancementWrapper(key, jsonObject);
    }

    @Override
    public AbstractAdvancementWrapper advancementWrapper(String key) {
        return new V1_19_R3AdvancementWrapper(key);
    }

    @Override
    public void reloadAdvancementTree() {
        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        AdvancementDataWorld advancementDataWorld = minecraftServer.az();
        Advancements advancements = advancementDataWorld.c;
        for (Advancement advancement : advancements.b()) {
            if (advancement.d() != null)
                AdvancementTree.a(advancement);
        }
    }

    @Override
    public void reloadPlayerAdvancements() {
        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        PlayerList playerList = minecraftServer.ac();
        AdvancementDataWorld advancementDataWorld = minecraftServer.az();
        for (EntityPlayer player : playerList.k) {
            AdvancementDataPlayer advancementDataPlayer = player.M();
            advancementDataPlayer.b();
            advancementDataPlayer.a(advancementDataWorld);
        }
    }

}
