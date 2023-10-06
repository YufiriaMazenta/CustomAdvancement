package com.github.yufiriamazenta.customadv.loader;

import com.github.yufiriamazenta.customadv.CustomAdvancement;
import com.google.gson.JsonObject;
import crypticlib.config.impl.YamlConfigWrapper;
import crypticlib.util.MsgUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AdvancementLoadTreeNode {

    private final String nodeKey;

    private final YamlConfigWrapper advancementConfig;
    private final Map<String, AdvancementLoadTreeNode> childNodes;
    private final JsonObject advancementJson;

    public AdvancementLoadTreeNode(String nodeKey, YamlConfigWrapper advancementConfig, Map<String, AdvancementLoadTreeNode> childNodes) {
        this.nodeKey = nodeKey;
        this.advancementConfig = advancementConfig;
        this.childNodes = childNodes;
        this.advancementJson = null;
    }

    public AdvancementLoadTreeNode(String nodeKey, JsonObject advancementJson, Map<String, AdvancementLoadTreeNode> childNodes) {
        this.nodeKey = nodeKey;
        this.advancementConfig = null;
        this.childNodes = childNodes;
        this.advancementJson = advancementJson;
    }

    public AdvancementLoadTreeNode(String nodeKey, YamlConfigWrapper advancementConfig) {
        this(nodeKey, advancementConfig, new ConcurrentHashMap<>());
    }

    public AdvancementLoadTreeNode(String nodeKey, JsonObject jsonObject) {
        this(nodeKey, jsonObject, new ConcurrentHashMap<>());
    }

    public void load() {
        try {
            if (advancementConfig != null && advancementJson == null) {
                CustomAdvancement.getInstance().getAdvancementManager().advancementWrapper(nodeKey, advancementConfig.getConfig()).register();
            }
            else if (advancementJson != null && advancementConfig == null) {
                CustomAdvancement.getInstance().getAdvancementManager().advancementWrapper(nodeKey, advancementJson).register();
            }
        } catch (Throwable e) {
            MsgUtil.info("&cAn error occurred while loading advancement " + nodeKey);
            e.printStackTrace();
        }
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
        if (advancementConfig != null && advancementJson == null)
            return advancementConfig.getConfig().getString("parent");
        else if (advancementJson != null && advancementConfig == null)
            if (advancementJson.has("parent"))
                return advancementJson.get("parent").getAsString();
            else
                return null;
        else
            return null;
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
