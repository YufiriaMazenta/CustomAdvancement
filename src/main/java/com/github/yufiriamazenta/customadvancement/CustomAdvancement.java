package com.github.yufiriamazenta.customadvancement;

import com.github.yufiriamazenta.customadvancement.loader.AdvancementLoader;
import com.github.yufiriamazenta.customadvancement.manager.impl.AdvancementManager;
import com.github.yufiriamazenta.customadvancement.manager.IAdvancementManager;
import crypticlib.BukkitPlugin;
import crypticlib.config.impl.YamlConfigWrapper;
import org.bukkit.event.Listener;

public final class CustomAdvancement extends BukkitPlugin implements Listener {

    private static CustomAdvancement INSTANCE;
    private YamlConfigWrapper langFile;
    private IAdvancementManager advancementManager;

    @Override
    public void enable() {
        INSTANCE = this;
        saveDefaultConfig();
        loadLangFile();
        loadAdvancementImpl();
        try {
            loadAdvancements();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disable() {
    }

    private void loadAdvancements() throws Exception {
        AdvancementLoader.INSTANCE.loadAdvancements();
        advancementManager.reloadAdvancementTree();
    }

    private void loadAdvancementImpl() {
        advancementManager = AdvancementManager.INSTANCE;
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

    public IAdvancementManager getAdvancementManager() {
        return advancementManager;
    }

    public void setAdvancementManager(IAdvancementManager advancementManager) {
        this.advancementManager = advancementManager;
    }

}