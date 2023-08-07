package com.github.yufiriamazenta.customadvancement;

import com.github.yufiriamazenta.lib.util.MsgUtil;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public class AdvancementLoader {

    public static Advancement loadFromConfig(String id, ConfigurationSection config) {
        NamespacedKey namespacedKey;
        try {
            namespacedKey = new NamespacedKey(CustomAdvancement.getInstance(), id);
        } catch (IllegalArgumentException e) {
            MsgUtil.info(
                    CustomAdvancement.getInstance().getLangFile().getConfig().getString("advancement.load_failed.invalid_key", "advancement.load_failed.invalid_key"),
                    Map.of("%prefix%", CustomAdvancement.getInstance().getPrefix()));
            throw new RuntimeException(e);
        }
        JsonObject rootJson = new JsonObject();
        return Bukkit.getUnsafe().loadAdvancement(namespacedKey, rootJson.getAsString());
    }

}
