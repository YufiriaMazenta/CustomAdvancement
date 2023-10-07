package com.github.yufiriamazenta.customadv.adv;

import com.github.yufiriamazenta.customadv.AdvancementsCache;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Advancements;
import net.minecraft.advancements.critereon.LootDeserializationContext;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.AdvancementDataPlayer;
import net.minecraft.server.AdvancementDataWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.level.storage.loot.LootPredicateManager;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class V1_19_R3AdvancementWrapper extends AbstractAdvancementWrapper {

    public V1_19_R3AdvancementWrapper(String key, JsonObject json) {
        super(key, json);
    }

    public V1_19_R3AdvancementWrapper(String key) {
        super(key);
    }

    @Override
    public void register() {
        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        AdvancementDataWorld advancementDataWorld = minecraftServer.az();
        Advancements advancements = advancementDataWorld.c;
        LootPredicateManager lootPredicateManager = minecraftServer.aI();
        MinecraftKey minecraftKey = new MinecraftKey(super.getKey());
        Advancement.SerializedAdvancement advancement = Advancement.SerializedAdvancement.a(super.getAdvancementJson(), new LootDeserializationContext(minecraftKey, lootPredicateManager));
        Map<MinecraftKey, Advancement.SerializedAdvancement> advancementMap = new ConcurrentHashMap<>();
        advancementMap.put(minecraftKey, advancement);
        advancements.a(advancementMap);
        AdvancementsCache.getAdvancementWrapperMap().put(getKey(), this);
    }

    @Override
    public void unregister() {
        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        AdvancementDataWorld advancementDataWorld = minecraftServer.az();
        Advancements advancements = advancementDataWorld.c;
        MinecraftKey minecraftKey = new MinecraftKey(super.getKey());
        advancements.a(Set.of(minecraftKey));
        AdvancementsCache.getAdvancementWrapperMap().remove(getKey());
    }

    @Override
    public boolean grant(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        AdvancementDataPlayer advancementDataPlayer = entityPlayer.M();
        Advancement advancement = ((CraftServer) Bukkit.getServer()).getServer().az().a(new MinecraftKey(super.getKey()));
        if (advancement == null) {
            throw new NullPointerException("Advancement " + super.getKey() + " is null");
        }
        AdvancementProgress advancementProgress = advancementDataPlayer.b(advancement);
        if (!advancementProgress.a()) {
            for (String criterion : advancementProgress.e()) {
                advancementDataPlayer.a(advancement, criterion);
            }
        }
        return true;
    }

    @Override
    public boolean revoke(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        AdvancementDataPlayer advancementDataPlayer = entityPlayer.M();
        Advancement advancement = ((CraftServer) Bukkit.getServer()).getServer().az().a(new MinecraftKey(super.getKey()));
        if (advancement == null) {
            throw new NullPointerException("Advancement " + super.getKey() + " is null");
        }
        AdvancementProgress advancementProgress = advancementDataPlayer.b(advancement);
        if (!advancementProgress.b()) {
            for (String criterion : advancementProgress.f()) {
                advancementDataPlayer.b(advancement, criterion);
            }
        }
        return true;
    }

}
