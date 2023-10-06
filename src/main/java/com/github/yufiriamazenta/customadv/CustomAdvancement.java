package com.github.yufiriamazenta.customadv;

import com.github.yufiriamazenta.customadv.loader.AdvancementLoader;
import com.github.yufiriamazenta.customadv.manager.IAdvancementManager;
import com.github.yufiriamazenta.customadv.manager.impl.V1_20_R2AdvancementManager;
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
        loadAdvancementManager();
        loadAdvancements();
    }

    @Override
    public void disable() {
    }

    private void loadAdvancements() {
        AdvancementLoader.INSTANCE.loadAdvancements();
        advancementManager.reloadAdvancementTree();
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

    public void setLangFile(YamlConfigWrapper langFile) {
        this.langFile = langFile;
    }

    private void loadAdvancementManager() {
        advancementManager = switch (CustomAdvancement.getInstance().getNmsVersion()) {
            case "v1_20_R2" -> new V1_20_R2AdvancementManager();
            default -> throw new UnsupportedOperationException("Unknown version");
        };
    }

}