package com.github.yufiriamazenta.customadvancement.loader;

import com.github.yufiriamazenta.customadvancement.CustomAdvancement;
import crypticlib.config.impl.YamlConfigWrapper;

import java.util.HashMap;
import java.util.Map;

public final class AdvancementLoadTreeNode {

    private final String nodeKey;

    private final YamlConfigWrapper advancementConfig;
    private final Map<String, AdvancementLoadTreeNode> childNodes;

    public AdvancementLoadTreeNode(String nodeKey, YamlConfigWrapper advancementConfig, Map<String, AdvancementLoadTreeNode> childNodes) {
        this.nodeKey = nodeKey;
        this.advancementConfig = advancementConfig;
        this.childNodes = childNodes;
    }

    public AdvancementLoadTreeNode(String nodeKey, YamlConfigWrapper advancementConfig) {
        this(nodeKey, advancementConfig, new HashMap<>());
    }

    public void load() {
        CustomAdvancement.getInstance().getAdvancementManager().loadAdvancement(nodeKey, advancementConfig.getConfig(), false);
        for (AdvancementLoadTreeNode node : childNodes.values()) {
            node.load();
        }
    }

    public String getNodeKey() {
        return nodeKey;
    }

    public YamlConfigWrapper getAdvancementConfig() {
        return advancementConfig;
    }

    public Map<String, AdvancementLoadTreeNode> getChildNodes() {
        return childNodes;
    }

    public String getParentKey() {
        return advancementConfig.getConfig().getString("parent");
    }

    public AdvancementLoadTreeNode matchNode(String key) {
        if (nodeKey.equals(key))
            return this;
        for (String childKey : childNodes.keySet()) {
            AdvancementLoadTreeNode node = childNodes.get(childKey).matchNode(key);
            if (node != null) {
                return node;
            }
        }
        return null;
    }

}
