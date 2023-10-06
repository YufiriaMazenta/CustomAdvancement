package com.github.yufiriamazenta.customadv.adv;


import com.google.gson.JsonObject;
import org.bukkit.entity.Player;

public abstract class AbstractAdvancementWrapper {

    private JsonObject advancementJson;
    private String key;

    public AbstractAdvancementWrapper(String key, JsonObject json) {
        this.key = key;
        this.advancementJson = json;
    }

    public AbstractAdvancementWrapper(String key) {
        this.key = key;
        this.advancementJson = null;
    }

    abstract public void register();

    abstract public void unregister();

    abstract public boolean grant(Player player);

    abstract public boolean revoke(Player player);

    public JsonObject getAdvancementJson() {
        return advancementJson;
    }

    public void setAdvancementJson(JsonObject advancementJson) {
        this.advancementJson = advancementJson;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
