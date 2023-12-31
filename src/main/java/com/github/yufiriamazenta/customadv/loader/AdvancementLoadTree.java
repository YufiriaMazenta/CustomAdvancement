package com.github.yufiriamazenta.customadv.loader;

import com.github.yufiriamazenta.customadv.CustomAdvancement;
import com.google.gson.JsonObject;
import crypticlib.config.impl.YamlConfigWrapper;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AdvancementLoadTree {
    private final Map<String, AdvancementLoadTreeNode> loadNodes;

    public AdvancementLoadTree(Map<String, Object> advancementFiles) {
        loadNodes = new HashMap<>();

        while (advancementFiles.size() >= 1) {
            List<String> removeList = new ArrayList<>();
            advancementFiles.forEach((key, advancementFile) -> {
                AdvancementLoadTreeNode node;
                if (advancementFile instanceof YamlConfigWrapper)
                    node = new AdvancementLoadTreeNode(key, (YamlConfigWrapper) advancementFile);
                else if (advancementFile instanceof JsonObject)
                    node = new AdvancementLoadTreeNode(key, (JsonObject) advancementFile);
                else
                    throw new IllegalArgumentException("Unsupported advancement file type");
                String parentKey = node.getParentKey();
                if (parentKey == null || Bukkit.getAdvancement(NamespacedKey.fromString(parentKey)) != null) {
                    loadNodes.put(key, node);
                    removeList.add(key);
                    return;
                }
                for (String nodeKey : loadNodes.keySet()) {
                    AdvancementLoadTreeNode parentNode = loadNodes.get(nodeKey).matchNode(parentKey);
                    if (parentNode != null) {
                        parentNode.getChildNodes().put(key, node);
                        removeList.add(key);
                        return;
                    }
                }
                if (!advancementFiles.containsKey(parentKey)) {
                    loadNodes.put(key, node);
                    removeList.add(key);
                }
            });
            removeList.forEach(advancementFiles::remove);
        }
    }

    public void load() {
        loadNodes.forEach((nodeKey, node) -> {
            node.load();
        });
    }

    void unload() {
        loadNodes.forEach((nodeKey, node) -> {
            CustomAdvancement.getInstance().getAdvancementManager().advancementWrapper(nodeKey).unregister();
        });
    }

    public Map<String, AdvancementLoadTreeNode> getLoadNodes() {
        return loadNodes;
    }
}
