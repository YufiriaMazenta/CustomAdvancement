package com.github.yufiriamazenta.customadvancement;

import com.github.yufiriamazenta.customadvancement.manager.impl.*;
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
        AdvancementLoader.INSTANCE.loadAdvancements();
    }

    @Override
    public void disable() {
    }

    private void loadAdvancementImpl() {
        advancementManager = switch (getNmsVersion()) {
            case "v1_20_R1" -> new V1_20_R1AdvancementManager();
            case "v1_19_R3" -> new V1_19_R3AdvancementManager();
            case "v1_19_R2" -> new V1_19_R2AdvancementManager();
            case "v1_19_R1" -> new V1_19_R1AdvancementManager();
            case "v1_18_R2" -> new V1_18_R2AdvancementManager();
            case "v1_18_R1" -> new V1_18_R1AdvancementManager();
            case "v1_17_R1" -> new V1_17_R1AdvancementManager();
            default -> throw new RuntimeException("Run on an unsupported version");
        };
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