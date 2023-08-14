
package com.github.yufiriamazenta.customadvancement.manager.impl;


import com.github.yufiriamazenta.customadvancement.manager.IAdvancementManager;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

import java.util.Map;
import java.util.Set;

public class V1_20_R1AdvancementManager implements IAdvancementManager {

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

}
