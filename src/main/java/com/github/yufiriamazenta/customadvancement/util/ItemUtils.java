package com.github.yufiriamazenta.customadvancement.util;

import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.ItemExecutor;
import io.lumine.mythic.core.items.MythicItem;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pers.neige.neigeitems.NeigeItems;
import pers.neige.neigeitems.manager.ItemManager;

import java.util.Objects;
import java.util.Optional;

public class ItemUtils {

    public static ItemStack matchItem(String itemStr) {
        ItemStack item;
        if (itemStr.contains(":")) {
            String namespace = itemStr.substring(0, itemStr.indexOf(":")), key = itemStr.substring(itemStr.indexOf(":") + 1);
            item = switch (namespace) {
                case "items_adder" -> getItemsAdderItem(key);
                case "oraxen" -> getOraxenItem(key);
                case "mythic_mobs" -> getMythicMobsItem(key);
                case "ni" -> getNIItem(key);
                case "minecraft" -> new ItemStack(Objects.requireNonNull(Material.matchMaterial(key)));
                default -> throw new IllegalArgumentException(namespace + " is not a valid item namespace");
            };
        } else {
            Material material = Material.matchMaterial(itemStr);
            if (material == null) {
                throw new IllegalArgumentException(itemStr + " is a not exist item type");
            }
            item = new ItemStack(material);
        }
        return item;
    }

    public static ItemStack getItemsAdderItem(String itemStr) {
        CustomStack customStack = CustomStack.getInstance(itemStr);
        if (customStack == null) {
            throw new IllegalArgumentException(itemStr + " is a not exist ItemsAdder item");
        }
        return customStack.getItemStack();
    }

    public static ItemStack getOraxenItem(String itemStr) {
        if (!OraxenItems.exists(itemStr)) {
            throw new IllegalArgumentException(itemStr + " is a not exist Oraxen item");
        }
        return OraxenItems.getItemById(itemStr).build();
    }

    public static ItemStack getMythicMobsItem(String itemStr) {
        ItemExecutor executor = MythicBukkit.inst().getItemManager();
        Optional<MythicItem> itemOptional = executor.getItem(itemStr);
        if (!itemOptional.isPresent()) {
            throw new IllegalArgumentException(itemStr + " is not a valid MythicMobs item");
        }
        MythicItem mythicItem = itemOptional.get();
        int amount = mythicItem.getAmount();
        return BukkitAdapter.adapt(itemOptional.get().generateItemStack(amount));
    }

    public static ItemStack getNIItem(String itemStr) {
        if (!ItemManager.INSTANCE.hasItem(itemStr))
            throw new IllegalArgumentException(itemStr + " is not a valid NeigeItems item");
        return ItemManager.INSTANCE.getItemStack(itemStr);
    }

}
