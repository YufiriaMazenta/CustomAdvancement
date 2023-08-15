package com.github.yufiriamazenta.customadvancement.cmd.sub;

import com.github.yufiriamazenta.customadvancement.AdvancementLoader;
import com.github.yufiriamazenta.customadvancement.CustomAdvancement;
import crypticlib.command.ISubCommand;
import crypticlib.util.MsgUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum RevokeCommand implements ISubCommand {

    INSTANCE;
    private final Map<String, ISubCommand> subCommandMap = new ConcurrentHashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        YamlConfiguration langConfig = CustomAdvancement.getInstance().getLangFile().getConfig();
        if (args.size() < 2) {
            MsgUtil.sendLang(sender, langConfig, "command.missing_parameters", Map.of("%prefix%", CustomAdvancement.getInstance().getPrefix()));
            return true;
        }
        Player player = Bukkit.getPlayer(args.get(0));
        if (player == null || !player.isOnline()) {
            MsgUtil.sendLang(sender, langConfig, "command.revoke_failed_offline_player", Map.of("%prefix%", CustomAdvancement.getInstance().getPrefix()));
            return true;
        }

        if (CustomAdvancement.getInstance().getAdvancementManager().revokeAdvancement(player, args.get(1))) {
            MsgUtil.sendLang(sender, langConfig, "command.revoke_success", Map.of("%prefix%", CustomAdvancement.getInstance().getPrefix(), "%player%", args.get(0)));
        } else {
            MsgUtil.sendLang(sender, langConfig, "command.revoke_failed_not_exist_advancement", Map.of("%prefix%", CustomAdvancement.getInstance().getPrefix()));
        }
        return true;
    }

    @Override
    public String getSubCommandName() {
        return "revoke";
    }

    @Override
    public String getPerm() {
        return "custom_advancement.command.revoke";
    }

    @Override
    public void setPerm(String perm) {}

    @Override
    public @NotNull Map<String, ISubCommand> getSubCommands() {
        return subCommandMap;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() <= 1) {
            List<String> playerNameList = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerNameList.add(player.getName());
            }
            playerNameList.removeIf(str -> !str.startsWith(args.get(0)));
            return playerNameList;
        } else if (args.size() <= 2) {
            List<String> advancements = AdvancementLoader.INSTANCE.getAdvancements();
            advancements.removeIf(str -> !str.startsWith(args.get(1)));
            return advancements;
        } else {
            return Collections.singletonList("");
        }
    }

}
