package com.github.yufiriamazenta.customadvancement;

import com.github.yufiriamazenta.customadvancement.cmd.CustomAdvancementCmd;
import crypticlib.BukkitPlugin;
import crypticlib.config.impl.YamlConfigWrapper;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;

public final class CustomAdvancement extends BukkitPlugin {

    private static CustomAdvancement INSTANCE;
    private YamlConfigWrapper langFile;

    @Override
    public void onEnable() {
        INSTANCE = this;
        saveDefaultConfig();
        loadLangFile();
        AdvancementManager.loadAdvancements();
        Bukkit.getPluginCommand("customadvancement").setExecutor(CustomAdvancementCmd.INSTANCE);
        Bukkit.getPluginCommand("customadvancement").setTabCompleter(CustomAdvancementCmd.INSTANCE);
    }

    @Override
    public void onDisable() {
        for (NamespacedKey advancement : AdvancementManager.getAdvancementList()) {
            Bukkit.getUnsafe().removeAdvancement(advancement);
        }
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
