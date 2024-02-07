package com.github.yufiriamazenta.customadv.manager.impl;

import com.github.yufiriamazenta.customadv.adv.AbstractAdvancementWrapper;
import com.github.yufiriamazenta.customadv.adv.V1_20_R3AdvancementWrapper;
import com.github.yufiriamazenta.customadv.manager.IAdvancementManager;
import com.google.gson.JsonObject;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.advancements.TreeNodePosition;
import net.minecraft.server.AdvancementDataPlayer;
import net.minecraft.server.AdvancementDataWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.players.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;

public enum V1_20_R3AdvancementManager implements IAdvancementManager {

    INSTANCE;

    @Override
    public AbstractAdvancementWrapper advancementWrapper(String key, JsonObject jsonObject) {
        return new V1_20_R3AdvancementWrapper(key, jsonObject);
    }

    @Override
    public AbstractAdvancementWrapper advancementWrapper(String key) {
        return new V1_20_R3AdvancementWrapper(key);
    }

    @Override
    public void reloadAdvancementTree() {
        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        AdvancementDataWorld advancementDataWorld = minecraftServer.aB();
        AdvancementTree advancementTree = advancementDataWorld.a();
        for (AdvancementNode advancementNode : advancementTree.b()) {
            if (advancementNode.a().c().isPresent())
                TreeNodePosition.a(advancementNode);
        }
    }

    @Override
    public void reloadPlayerAdvancements() {
        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        PlayerList playerList = minecraftServer.ae();
        AdvancementDataWorld advancementDataWorld = minecraftServer.aB();
        for (EntityPlayer player : playerList.l) {
            AdvancementDataPlayer advancementDataPlayer = player.Q();
            advancementDataPlayer.b();
            advancementDataPlayer.a(advancementDataWorld);
        }
    }

}
