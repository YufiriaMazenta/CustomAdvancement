package com.github.yufiriamazenta.customadv.adv;

import com.github.yufiriamazenta.customadv.AdvancementsCache;
import com.google.gson.JsonObject;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.LootDeserializationContext;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.AdvancementDataPlayer;
import net.minecraft.server.AdvancementDataWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.level.storage.loot.LootDataManager;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R2.CraftServer;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class V1_20_R2AdvancementWrapper extends AbstractAdvancementWrapper {

    public V1_20_R2AdvancementWrapper(String key, JsonObject json) {
        super(key, json);
    }

    public V1_20_R2AdvancementWrapper(String key) {
        super(key);
    }

    @Override
    public void register() {
        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        AdvancementDataWorld advancementDataWorld = minecraftServer.az();
        AdvancementTree advancementTree = advancementDataWorld.a();
        LootDataManager lootDataManager = minecraftServer.aH();
        MinecraftKey minecraftKey = new MinecraftKey(super.getKey());
        Advancement advancement = Advancement.a(super.getAdvancementJson(), new LootDeserializationContext(minecraftKey, lootDataManager));
        List<AdvancementHolder> advancementHolders = new ArrayList<>();
        advancementHolders.add(new AdvancementHolder(minecraftKey, advancement));
        advancementTree.a(advancementHolders);

        Map<MinecraftKey, AdvancementHolder> advancementHolderMap = new ConcurrentHashMap<>();
        for (AdvancementNode advancementNode : advancementTree.c()) {
            advancementHolderMap.put(advancementNode.b().a(), advancementNode.b());
        }
        advancementDataWorld.c = advancementHolderMap;

        AdvancementsCache.getAdvancementWrapperMap().put(getKey(), this);
    }

    @Override
    public void unregister() {
        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        AdvancementDataWorld advancementDataWorld = minecraftServer.az();
        AdvancementTree advancementTree = advancementDataWorld.a();
        MinecraftKey minecraftKey = new MinecraftKey(super.getKey());
        advancementTree.a(Set.of(minecraftKey));
        AdvancementsCache.getAdvancementWrapperMap().remove(getKey());
    }

    @Override
    public boolean grant(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        AdvancementDataPlayer advancementDataPlayer = entityPlayer.O();
        AdvancementHolder advancementHolder = ((CraftServer) Bukkit.getServer()).getServer().az().a(new MinecraftKey(super.getKey()));
        if (advancementHolder == null) {
            throw new NullPointerException("Advancement " + super.getKey() + " is null");
        }
        AdvancementProgress advancementProgress = advancementDataPlayer.b(advancementHolder);
        if (!advancementProgress.a()) {
            for (String criterion : advancementProgress.e()) {
                advancementDataPlayer.a(advancementHolder, criterion);
            }
        }
        return true;
    }

    @Override
    public boolean revoke(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        AdvancementDataPlayer advancementDataPlayer = entityPlayer.O();
        AdvancementHolder advancementHolder = ((CraftServer) Bukkit.getServer()).getServer().az().a(new MinecraftKey(super.getKey()));
        if (advancementHolder == null) {
            throw new NullPointerException("Advancement " + super.getKey() + " is null");
        }
        AdvancementProgress advancementProgress = advancementDataPlayer.b(advancementHolder);
        if (!advancementProgress.b()) {
            for (String criterion : advancementProgress.f()) {
                advancementDataPlayer.b(advancementHolder, criterion);
            }
        }
        return true;
    }

}
