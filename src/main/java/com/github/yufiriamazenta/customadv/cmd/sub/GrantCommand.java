package com.github.yufiriamazenta.customadv.cmd.sub;

import com.github.yufiriamazenta.customadv.AdvancementsCache;
import com.github.yufiriamazenta.customadv.CustomAdvancement;
import crypticlib.command.impl.SubcmdExecutor;
import crypticlib.util.MsgUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GrantCommand extends SubcmdExecutor {

    public final static GrantCommand INSTANCE = new GrantCommand();

    public GrantCommand() {
        super("grant", "custom_advancement.command.grant");
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        YamlConfiguration langConfig = CustomAdvancement.getInstance().getLangFile().config();
        if (args.size() < 2) {
            MsgUtil.sendMsg(sender, langConfig.getString("command.missing_parameters", "command.missing_parameters") , Map.of("%prefix%", CustomAdvancement.getInstance().getPrefix()));
            return true;
        }
        Player player = Bukkit.getPlayer(args.get(0));
        if (player == null || !player.isOnline()) {
            MsgUtil.sendMsg(sender, langConfig.getString("command.grant_failed_offline_player", "command.grant_failed_offline_player"), Map.of("%prefix%", CustomAdvancement.getInstance().getPrefix()));
            return true;
        }

        if (CustomAdvancement.getInstance().getAdvancementManager().advancementWrapper(args.get(1)).grant(player)) {
            MsgUtil.sendMsg(sender, langConfig.getString("command.grant_success", "command.grant_success"), Map.of("%prefix%", CustomAdvancement.getInstance().getPrefix(), "%player%", args.get(0)));
        } else {
            MsgUtil.sendMsg(sender, langConfig.getString("command.grant_failed_not_exist_advancement", "command.grant_failed_not_exist_advancement"), Map.of("%prefix%", CustomAdvancement.getInstance().getPrefix()));
        }
        return true;
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
        } else if (args.size() == 2) {
            List<String> advancements = new ArrayList<>(AdvancementsCache.getAdvancements());
            advancements.removeIf(str -> !str.startsWith(args.get(1)));
            return advancements;
        } else {
            return Collections.singletonList("");
        }
    }

}
