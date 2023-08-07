package com.github.yufiriamazenta.customadvancement;

import com.github.yufiriamazenta.lib.config.impl.YamlConfigWrapper;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomAdvancement extends JavaPlugin {

    private static CustomAdvancement INSTANCE;
    private YamlConfigWrapper langFile;

    @Override
    public void onEnable() {
        INSTANCE = this;
        loadLangFile();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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
