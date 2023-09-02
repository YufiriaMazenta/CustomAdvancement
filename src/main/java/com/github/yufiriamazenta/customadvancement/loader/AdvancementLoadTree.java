package com.github.yufiriamazenta.customadvancement.loader;

import com.github.yufiriamazenta.customadvancement.manager.impl.AdvancementManager;
import crypticlib.config.impl.YamlConfigWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AdvancementLoadTree {
    private final Map<String, AdvancementLoadTreeNode> loadNodes;

    public AdvancementLoadTree(Map<String, YamlConfigWrapper> advancementConfigs) {
        loadNodes = new HashMap<>();

        while (advancementConfigs.size() >= 1) {
            List<String> removeList = new ArrayList<>();
            advancementConfigs.forEach((key, config) -> {
                AdvancementLoadTreeNode node = new AdvancementLoadTreeNode(key, config);
                String parentKey = node.getParentKey();
                if (parentKey == null || !parentKey.startsWith("custom_advancement:")) {
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
                if (!advancementConfigs.containsKey(parentKey)) {
                    loadNodes.put(key, node);
                    removeList.add(key);
                }
            });
            removeList.forEach(advancementConfigs::remove);
        }
    }

    void load() {
        loadNodes.forEach((nodeKey, node) -> {
            try {
                node.load();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }

    void unload() {
        loadNodes.forEach((nodeKey, node) -> {
            AdvancementManager.INSTANCE.removeAdvancement(nodeKey);
        });
    }

    public Map<String, AdvancementLoadTreeNode> getLoadNodes() {
        return loadNodes;
    }
}
