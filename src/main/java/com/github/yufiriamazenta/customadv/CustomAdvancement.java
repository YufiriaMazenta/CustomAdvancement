package com.github.yufiriamazenta.customadv;

import com.github.yufiriamazenta.customadv.cmd.sub.GrantCommand;
import com.github.yufiriamazenta.customadv.cmd.sub.ReloadCommand;
import com.github.yufiriamazenta.customadv.cmd.sub.RevokeCommand;
import com.github.yufiriamazenta.customadv.loader.AdvancementLoader;
import com.github.yufiriamazenta.customadv.manager.IAdvancementManager;
import com.github.yufiriamazenta.customadv.manager.impl.*;
import crypticlib.BukkitPlugin;
import crypticlib.CrypticLib;
import crypticlib.command.api.CommandInfo;
import crypticlib.command.impl.RootCmdExecutor;
import crypticlib.config.impl.YamlConfigWrapper;
import org.bukkit.event.Listener;

import static crypticlib.command.CommandManager.subcommand;

public final class CustomAdvancement extends BukkitPlugin implements Listener {

    private static CustomAdvancement INSTANCE;
    private YamlConfigWrapper langFile;
    private IAdvancementManager advancementManager;
    private String ADVANCEMENT_NAMESPACE_YAML = getConfig().getString("advancement_namespace_yaml", "custom_advancement");
    private String ADVANCEMENT_NAMESPACE_JSON = getConfig().getString("advancement_namespace_json", "minecraft");

    @Override
    public void enable() {
        INSTANCE = this;
        saveDefaultConfig();
        loadLangFile();
        loadAdvancementManager();
        loadAdvancements();
        loadCommands();
    }

    private void loadCommands() {
        new RootCmdExecutor()
            .regSub(GrantCommand.INSTANCE)
            .regSub(ReloadCommand.INSTANCE)
            .regSub(RevokeCommand.INSTANCE)
            .register(this, new CommandInfo("customadvancement","custom_advancement.command", new String[]{"ca", "cadv"}));
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
        return langFile.config().getString("prefix", "&8[&3Custom&bAdvancement&8]&r");
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
        advancementManager = switch (CrypticLib.nmsVersion()) {
            case "v1_18_R1" -> V1_17_R1AdvancementManager.INSTANCE;
            case "v1_18_R2" -> V1_18_R2AdvancementManager.INSTANCE;
            case "v1_19_R1" -> V1_19_R1AdvancementManager.INSTANCE;
            case "v1_19_R2" -> V1_19_R2AdvancementManager.INSTANCE;
            case "v1_19_R3" -> V1_19_R3AdvancementManager.INSTANCE;
            case "v1_20_R1" -> V1_20_R1AdvancementManager.INSTANCE;
            case "v1_20_R2" -> V1_20_R2AdvancementManager.INSTANCE;
            default -> throw new UnsupportedOperationException("Unknown version");
        };
    }

    public void reloadNamespace() {
        ADVANCEMENT_NAMESPACE_YAML = getConfig().getString("advancement_namespace_yaml", "custom_advancement");
        ADVANCEMENT_NAMESPACE_JSON = getConfig().getString("advancement_namespace_json", "minecraft");
    }

    public String getAdvancementNamespaceYaml() {
        return ADVANCEMENT_NAMESPACE_YAML;
    }

    public String getAdvancementNamespaceJson() {
        return ADVANCEMENT_NAMESPACE_JSON;
    }

}