package com.github.yufiriamazenta.customadvancement.manager.impl;

import com.github.yufiriamazenta.customadvancement.CustomAdvancement;
import com.github.yufiriamazenta.customadvancement.loader.AdvancementLoader;
import com.github.yufiriamazenta.customadvancement.manager.IAdvancementManager;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.TreeNodePosition;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public enum AdvancementManager implements IAdvancementManager {

    INSTANCE;
    private Map<String, Map<String, String>> methodNameMap;
    private Map<String, Map<String, String>> fieldNameMap;
    private Method getServerMethod, getLootDataMethod, getServerAdvancementsMethod, addAdvancementMethod, removeAdvancementMethod, getAdvancementMethod, getServerPlayersMethod;
    private Method getPlayerAdvancementsMethod, savePlayerAdvancementsMethod, reloadPlayerAdvancementsMethod, getOrStartProgressMethod, awardPlayerAdvancementMethod, revokePlayerAdvancementMethod;
    private Method advancementProgressIsDoneMethod, getAdvancementProgressRemainingCriteriaMethod, advancementProgressHasProgressMethod, getAdvancementProgressCompletedCriteriaMethod;
    private Method advancementFromJsonMethod;
    private Method getPlayerHandleMethod;
    private Method treeNodeRunMethod;
    private Field serverAdvancementsField, serverPlayerListField;
    private Constructor<?> deserializationContextConstructor;
    private final List<String> advancements;

    AdvancementManager() {
        advancements = new ArrayList<>();
        initReflectionMap();
        loadMethodsAndFields();
    }

    @Override
    public void loadAdvancements(Map<ResourceLocation, Advancement.Builder> advancements, boolean reload) {
        try {
            MinecraftServer server = (MinecraftServer) getServerMethod.invoke(null);
            ServerAdvancementManager advancementManager = (ServerAdvancementManager) getServerAdvancementsMethod.invoke(server);
            AdvancementList advancementList = (AdvancementList) serverAdvancementsField.get(advancementManager);
            addAdvancementMethod.invoke(advancementList, advancements);
            if (reload) {
                AdvancementLoader.INSTANCE.reloadAdvancements();
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeAdvancements(Set<ResourceLocation> keySet, boolean reload) {
        try {
            MinecraftServer server = (MinecraftServer) getServerMethod.invoke(null);
            ServerAdvancementManager advancementManager = (ServerAdvancementManager) getServerAdvancementsMethod.invoke(server);
            AdvancementList advancementList = (AdvancementList) serverAdvancementsField.get(advancementManager);
            removeAdvancementMethod.invoke(advancementList, keySet);
            if (reload) {
                AdvancementLoader.INSTANCE.reloadAdvancements();
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reloadPlayerAdvancements() {
        try {
            MinecraftServer server = (MinecraftServer) getServerMethod.invoke(null);
            PlayerList playerList = (PlayerList) getServerPlayersMethod.invoke(server);
            List<ServerPlayer> players = (List<ServerPlayer>) serverPlayerListField.get(playerList);
            ServerAdvancementManager serverAdvancementManager = (ServerAdvancementManager) getServerAdvancementsMethod.invoke(server);
            for (ServerPlayer player : players) {
                PlayerAdvancements playerAdvancements = (PlayerAdvancements) getPlayerAdvancementsMethod.invoke(player);
                savePlayerAdvancementsMethod.invoke(playerAdvancements);
                reloadPlayerAdvancementsMethod.invoke(playerAdvancements, serverAdvancementManager);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reloadAdvancementTree() {
        try {
            MinecraftServer server = (MinecraftServer) getServerMethod.invoke(null);
            ServerAdvancementManager serverAdvancementManager = (ServerAdvancementManager) getServerAdvancementsMethod.invoke(server);
            AdvancementLoader.INSTANCE.getLoadTree().getLoadNodes().forEach((nodeKey, nodeValue) -> {
                if (nodeValue.getParentKey() == null) {
                    try {
                        Advancement advancement = (Advancement) getAdvancementMethod.invoke(serverAdvancementManager, new ResourceLocation(nodeKey));
                        treeNodeRunMethod.invoke(null, advancement);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean grantAdvancement(Player player, ResourceLocation key) {
        try {
            MinecraftServer server = (MinecraftServer) getServerMethod.invoke(null);
            ServerAdvancementManager serverAdvancementManager = (ServerAdvancementManager) getServerAdvancementsMethod.invoke(server);
            Object advancementObj = getAdvancementMethod.invoke(serverAdvancementManager, key);
            if (advancementObj == null) {
                return false;
            }
            Advancement advancement = (Advancement) advancementObj;
            if (getPlayerHandleMethod == null) {
                getPlayerHandleMethod = player.getClass().getMethod("getHandle");
            }
            ServerPlayer serverPlayer = (ServerPlayer) getPlayerHandleMethod.invoke(player);
            PlayerAdvancements playerAdvancements = (PlayerAdvancements) getPlayerAdvancementsMethod.invoke(serverPlayer);
            AdvancementProgress advancementProgress = (AdvancementProgress) getOrStartProgressMethod.invoke(playerAdvancements, advancement);
            Boolean isDone = (Boolean) advancementProgressIsDoneMethod.invoke(advancementProgress);
            if (!isDone) {
                Iterable<String> remainingCriteria = (Iterable<String>) getAdvancementProgressRemainingCriteriaMethod.invoke(advancementProgress);
                for (String criterion : remainingCriteria) {
                    awardPlayerAdvancementMethod.invoke(playerAdvancements, advancement, criterion);
                }
            }
            return true;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean revokeAdvancement(Player player, ResourceLocation key) {
        try {
            MinecraftServer server = (MinecraftServer) getServerMethod.invoke(null);
            ServerAdvancementManager serverAdvancementManager = (ServerAdvancementManager) getServerAdvancementsMethod.invoke(server);
            Object advancementObj = getAdvancementMethod.invoke(serverAdvancementManager, key);
            if (advancementObj == null) {
                return false;
            }
            Advancement advancement = (Advancement) advancementObj;
            if (getPlayerHandleMethod == null) {
                getPlayerHandleMethod = player.getClass().getMethod("getHandle");
            }
            ServerPlayer serverPlayer = (ServerPlayer) getPlayerHandleMethod.invoke(player);
            PlayerAdvancements playerAdvancements = (PlayerAdvancements) getPlayerAdvancementsMethod.invoke(serverPlayer);
            AdvancementProgress advancementProgress = (AdvancementProgress) getOrStartProgressMethod.invoke(playerAdvancements, advancement);
            Boolean hasProgress = (Boolean) advancementProgressHasProgressMethod.invoke(advancementProgress);
            if (hasProgress) {
                Iterable<String> completedCriteria = (Iterable<String>) getAdvancementProgressCompletedCriteriaMethod.invoke(advancementProgress);
                for (String criterion : completedCriteria) {
                    revokePlayerAdvancementMethod.invoke(playerAdvancements, advancement, criterion);
                }
            }
            return true;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Advancement.Builder json2Advancement(ResourceLocation key, JsonObject advancementJson) {
        try {
            MinecraftServer server = (MinecraftServer) getServerMethod.invoke(null);
            Object lootData = getLootDataMethod.invoke(server);
            Object deserializationContext = deserializationContextConstructor.newInstance(key, lootData);
            return (Advancement.Builder) advancementFromJsonMethod.invoke(null, advancementJson, deserializationContext);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<String> getAdvancements() {
        return new ArrayList<>(advancements);
    }

    @Override
    public List<String> getEditableAdvancements() {
        return advancements;
    }

    //以下为内部实现专用方法

    private void loadMethodsAndFields() {
        try {
            String nmsVer = CustomAdvancement.getInstance().getNmsVersion();
            getServerMethod = MinecraftServer.class.getMethod(methodNameMap.get("getServer").get(nmsVer));
            getLootDataMethod = MinecraftServer.class.getMethod(methodNameMap.get("getLootData").get(nmsVer));
            getServerAdvancementsMethod = MinecraftServer.class.getMethod(methodNameMap.get("getAdvancements").get(nmsVer));
            addAdvancementMethod = AdvancementList.class.getMethod(methodNameMap.get("addAdvancement").get(nmsVer), Map.class);
            removeAdvancementMethod = AdvancementList.class.getMethod(methodNameMap.get("removeAdvancement").get(nmsVer), Set.class);
            getAdvancementMethod = ServerAdvancementManager.class.getMethod(methodNameMap.get("getAdvancement").get(nmsVer), ResourceLocation.class);
            getServerPlayersMethod = MinecraftServer.class.getMethod(methodNameMap.get("getPlayers").get(nmsVer));
            getPlayerAdvancementsMethod = ServerPlayer.class.getMethod(methodNameMap.get("getPlayerAdvancements").get(nmsVer));
            savePlayerAdvancementsMethod = PlayerAdvancements.class.getMethod(methodNameMap.get("savePlayerAdvancements").get(nmsVer));
            reloadPlayerAdvancementsMethod = PlayerAdvancements.class.getMethod(methodNameMap.get("reloadPlayerAdvancements").get(nmsVer), ServerAdvancementManager.class);
            getOrStartProgressMethod = PlayerAdvancements.class.getMethod(methodNameMap.get("getOrStartProgress").get(nmsVer), Advancement.class);
            awardPlayerAdvancementMethod = PlayerAdvancements.class.getMethod(methodNameMap.get("awardPlayerAdvancement").get(nmsVer), Advancement.class, String.class);
            revokePlayerAdvancementMethod = PlayerAdvancements.class.getMethod(methodNameMap.get("revokePlayerAdvancement").get(nmsVer), Advancement.class, String.class);
            advancementProgressIsDoneMethod = AdvancementProgress.class.getMethod(methodNameMap.get("advancementProgressIsDone").get(nmsVer));
            getAdvancementProgressRemainingCriteriaMethod = AdvancementProgress.class.getMethod(methodNameMap.get("getAdvancementProgressRemainingCriteria").get(nmsVer));
            advancementProgressHasProgressMethod = AdvancementProgress.class.getMethod(methodNameMap.get("advancementProgressHasProgress").get(nmsVer));
            getAdvancementProgressCompletedCriteriaMethod = AdvancementProgress.class.getMethod(methodNameMap.get("getAdvancementProgressCompletedCriteria").get(nmsVer));
            advancementFromJsonMethod = Advancement.Builder.class.getMethod(methodNameMap.get("advancementFromJson").get(nmsVer), JsonObject.class, DeserializationContext.class);
            treeNodeRunMethod = TreeNodePosition.class.getMethod(methodNameMap.get("treeNodeRun").get(nmsVer), Advancement.class);
            serverAdvancementsField = ServerAdvancementManager.class.getField(fieldNameMap.get("serverAdvancements").get(nmsVer));
            serverPlayerListField = PlayerList.class.getField(fieldNameMap.get("serverPlayerList").get(nmsVer));
            deserializationContextConstructor = DeserializationContext.class.getConstructors()[0];
        } catch (NoSuchMethodException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private void initReflectionMap() {
        //以下加载方法
        methodNameMap = new ConcurrentHashMap<>();

        Map<String, String> getServerMethodNameMap = new ConcurrentHashMap<>();
        getServerMethodNameMap.put("v1_17_R1", "getServer");
        getServerMethodNameMap.put("v1_18_R1", "getServer");
        getServerMethodNameMap.put("v1_18_R2", "getServer");
        getServerMethodNameMap.put("v1_19_R1", "getServer");
        getServerMethodNameMap.put("v1_19_R2", "getServer");
        getServerMethodNameMap.put("v1_19_R3", "getServer");
        getServerMethodNameMap.put("v1_20_R1", "getServer");
        methodNameMap.put("getServer", getServerMethodNameMap);

        Map<String, String> getLootDataMethodNameMap = new ConcurrentHashMap<>();
        getLootDataMethodNameMap.put("v1_17_R1", "getLootTableRegistry");
        getLootDataMethodNameMap.put("v1_18_R1", "aG");
        getLootDataMethodNameMap.put("v1_18_R2", "aF");
        getLootDataMethodNameMap.put("v1_19_R1", "aH");
        getLootDataMethodNameMap.put("v1_19_R2", "aH");
        getLootDataMethodNameMap.put("v1_19_R3", "aH");
        getLootDataMethodNameMap.put("v1_20_R1", "aH");
        methodNameMap.put("getLootData", getLootDataMethodNameMap);

        Map<String, String> getServerAdvancementsMethodNameMap = new ConcurrentHashMap<>();
        getServerAdvancementsMethodNameMap.put("v1_17_R1", "getAdvancementData()");
        getServerAdvancementsMethodNameMap.put("v1_18_R1", "ax");
        getServerAdvancementsMethodNameMap.put("v1_18_R2", "ax");
        getServerAdvancementsMethodNameMap.put("v1_19_R1", "az");
        getServerAdvancementsMethodNameMap.put("v1_19_R2", "ay");
        getServerAdvancementsMethodNameMap.put("v1_19_R3", "az");
        getServerAdvancementsMethodNameMap.put("v1_20_R1", "az");
        methodNameMap.put("getAdvancements", getServerAdvancementsMethodNameMap);

        Map<String, String> addAdvancementMethodNameMap = new ConcurrentHashMap<>();
        addAdvancementMethodNameMap.put("v1_17_R1", "a");
        addAdvancementMethodNameMap.put("v1_18_R1", "a");
        addAdvancementMethodNameMap.put("v1_18_R2", "a");
        addAdvancementMethodNameMap.put("v1_19_R1", "a");
        addAdvancementMethodNameMap.put("v1_19_R2", "a");
        addAdvancementMethodNameMap.put("v1_19_R3", "a");
        addAdvancementMethodNameMap.put("v1_20_R1", "a");
        methodNameMap.put("addAdvancement", addAdvancementMethodNameMap);

        Map<String, String> removeAdvancementMethodNameMap = new ConcurrentHashMap<>();
        removeAdvancementMethodNameMap.put("v1_17_R1", "a");
        removeAdvancementMethodNameMap.put("v1_18_R1", "a");
        removeAdvancementMethodNameMap.put("v1_18_R2", "a");
        removeAdvancementMethodNameMap.put("v1_19_R1", "a");
        removeAdvancementMethodNameMap.put("v1_19_R2", "a");
        removeAdvancementMethodNameMap.put("v1_19_R3", "a");
        removeAdvancementMethodNameMap.put("v1_20_R1", "a");
        methodNameMap.put("removeAdvancement", removeAdvancementMethodNameMap);

        Map<String, String> getAdvancementMethodNameMap = new ConcurrentHashMap<>();
        getAdvancementMethodNameMap.put("v1_17_R1", "a");
        getAdvancementMethodNameMap.put("v1_18_R1", "a");
        getAdvancementMethodNameMap.put("v1_18_R2", "a");
        getAdvancementMethodNameMap.put("v1_19_R1", "a");
        getAdvancementMethodNameMap.put("v1_19_R2", "a");
        getAdvancementMethodNameMap.put("v1_19_R3", "a");
        getAdvancementMethodNameMap.put("v1_20_R1", "a");
        methodNameMap.put("getAdvancement", getAdvancementMethodNameMap);

        Map<String, String> getPlayersMethodNameMap = new ConcurrentHashMap<>();
        getPlayersMethodNameMap.put("v1_17_R1", "getPlayerList");
        getPlayersMethodNameMap.put("v1_18_R1", "ac");
        getPlayersMethodNameMap.put("v1_18_R2", "ac");
        getPlayersMethodNameMap.put("v1_19_R1", "ac");
        getPlayersMethodNameMap.put("v1_19_R2", "ab");
        getPlayersMethodNameMap.put("v1_19_R3", "ac");
        getPlayersMethodNameMap.put("v1_20_R1", "ac");
        methodNameMap.put("getPlayers", getPlayersMethodNameMap);

        Map<String, String> getPlayerAdvancementsMethodNameMap = new ConcurrentHashMap<>();
        getPlayerAdvancementsMethodNameMap.put("v1_17_R1", "getAdvancementData");
        getPlayerAdvancementsMethodNameMap.put("v1_18_R1", "M");
        getPlayerAdvancementsMethodNameMap.put("v1_18_R2", "M");
        getPlayerAdvancementsMethodNameMap.put("v1_19_R1", "M");
        getPlayerAdvancementsMethodNameMap.put("v1_19_R2", "N");
        getPlayerAdvancementsMethodNameMap.put("v1_19_R3", "M");
        getPlayerAdvancementsMethodNameMap.put("v1_20_R1", "M");
        methodNameMap.put("getPlayerAdvancements", getPlayerAdvancementsMethodNameMap);

        Map<String, String> savePlayerAdvancementsMethodNameMap = new ConcurrentHashMap<>();
        savePlayerAdvancementsMethodNameMap.put("v1_17_R1", "b");
        savePlayerAdvancementsMethodNameMap.put("v1_18_R1", "b");
        savePlayerAdvancementsMethodNameMap.put("v1_18_R2", "b");
        savePlayerAdvancementsMethodNameMap.put("v1_19_R1", "b");
        savePlayerAdvancementsMethodNameMap.put("v1_19_R2", "b");
        savePlayerAdvancementsMethodNameMap.put("v1_19_R3", "b");
        savePlayerAdvancementsMethodNameMap.put("v1_20_R1", "b");
        methodNameMap.put("savePlayerAdvancements", savePlayerAdvancementsMethodNameMap);

        Map<String, String> reloadPlayerAdvancementsMethodNameMap = new ConcurrentHashMap<>();
        reloadPlayerAdvancementsMethodNameMap.put("v1_17_R1", "a");
        reloadPlayerAdvancementsMethodNameMap.put("v1_18_R1", "a");
        reloadPlayerAdvancementsMethodNameMap.put("v1_18_R2", "a");
        reloadPlayerAdvancementsMethodNameMap.put("v1_19_R1", "a");
        reloadPlayerAdvancementsMethodNameMap.put("v1_19_R2", "a");
        reloadPlayerAdvancementsMethodNameMap.put("v1_19_R3", "a");
        reloadPlayerAdvancementsMethodNameMap.put("v1_20_R1", "a");
        methodNameMap.put("reloadPlayerAdvancements", reloadPlayerAdvancementsMethodNameMap);

        Map<String, String> getOrStartProgressMethodNameMap = new ConcurrentHashMap<>();
        getOrStartProgressMethodNameMap.put("v1_17_R1", "getProgress");
        getOrStartProgressMethodNameMap.put("v1_18_R1", "b");
        getOrStartProgressMethodNameMap.put("v1_18_R2", "b");
        getOrStartProgressMethodNameMap.put("v1_19_R1", "b");
        getOrStartProgressMethodNameMap.put("v1_19_R2", "b");
        getOrStartProgressMethodNameMap.put("v1_19_R3", "b");
        getOrStartProgressMethodNameMap.put("v1_20_R1", "b");
        methodNameMap.put("getOrStartProgress", getOrStartProgressMethodNameMap);

        Map<String, String> awardPlayerAdvancementMethodNameMap = new ConcurrentHashMap<>();
        awardPlayerAdvancementMethodNameMap.put("v1_17_R1", "grantCriteria");
        awardPlayerAdvancementMethodNameMap.put("v1_18_R1", "a");
        awardPlayerAdvancementMethodNameMap.put("v1_18_R2", "a");
        awardPlayerAdvancementMethodNameMap.put("v1_19_R1", "a");
        awardPlayerAdvancementMethodNameMap.put("v1_19_R2", "a");
        awardPlayerAdvancementMethodNameMap.put("v1_19_R3", "a");
        awardPlayerAdvancementMethodNameMap.put("v1_20_R1", "a");
        methodNameMap.put("awardPlayerAdvancement", awardPlayerAdvancementMethodNameMap);

        Map<String, String> revokePlayerAdvancementMethodNameMap = new ConcurrentHashMap<>();
        revokePlayerAdvancementMethodNameMap.put("v1_17_R1", "revokeCritera");
        revokePlayerAdvancementMethodNameMap.put("v1_18_R1", "b");
        revokePlayerAdvancementMethodNameMap.put("v1_18_R2", "b");
        revokePlayerAdvancementMethodNameMap.put("v1_19_R1", "b");
        revokePlayerAdvancementMethodNameMap.put("v1_19_R2", "b");
        revokePlayerAdvancementMethodNameMap.put("v1_19_R3", "b");
        revokePlayerAdvancementMethodNameMap.put("v1_20_R1", "b");
        methodNameMap.put("revokePlayerAdvancement", revokePlayerAdvancementMethodNameMap);

        Map<String, String> advancementProgressIsDoneMethodNameMap = new ConcurrentHashMap<>();
        advancementProgressIsDoneMethodNameMap.put("v1_17_R1", "isDone");
        advancementProgressIsDoneMethodNameMap.put("v1_18_R1", "a");
        advancementProgressIsDoneMethodNameMap.put("v1_18_R2", "a");
        advancementProgressIsDoneMethodNameMap.put("v1_19_R1", "a");
        advancementProgressIsDoneMethodNameMap.put("v1_19_R2", "a");
        advancementProgressIsDoneMethodNameMap.put("v1_19_R3", "a");
        advancementProgressIsDoneMethodNameMap.put("v1_20_R1", "a");
        methodNameMap.put("advancementProgressIsDone", advancementProgressIsDoneMethodNameMap);

        Map<String, String> getAdvancementProgressRemainingCriteriaMethodNameMap = new ConcurrentHashMap<>();
        getAdvancementProgressRemainingCriteriaMethodNameMap.put("v1_17_R1", "getRemainingCriteria");
        getAdvancementProgressRemainingCriteriaMethodNameMap.put("v1_18_R1", "e");
        getAdvancementProgressRemainingCriteriaMethodNameMap.put("v1_18_R2", "e");
        getAdvancementProgressRemainingCriteriaMethodNameMap.put("v1_19_R1", "e");
        getAdvancementProgressRemainingCriteriaMethodNameMap.put("v1_19_R2", "e");
        getAdvancementProgressRemainingCriteriaMethodNameMap.put("v1_19_R3", "e");
        getAdvancementProgressRemainingCriteriaMethodNameMap.put("v1_20_R1", "e");
        methodNameMap.put("getAdvancementProgressRemainingCriteria", getAdvancementProgressRemainingCriteriaMethodNameMap);

        Map<String, String> advancementProgressHasProgressMethodNameMap = new ConcurrentHashMap<>();
        advancementProgressHasProgressMethodNameMap.put("v1_17_R1", "b");
        advancementProgressHasProgressMethodNameMap.put("v1_18_R1", "b");
        advancementProgressHasProgressMethodNameMap.put("v1_18_R2", "b");
        advancementProgressHasProgressMethodNameMap.put("v1_19_R1", "b");
        advancementProgressHasProgressMethodNameMap.put("v1_19_R2", "b");
        advancementProgressHasProgressMethodNameMap.put("v1_19_R3", "b");
        advancementProgressHasProgressMethodNameMap.put("v1_20_R1", "b");
        methodNameMap.put("advancementProgressHasProgress", advancementProgressHasProgressMethodNameMap);

        Map<String, String> getAdvancementProgressCompletedCriteriaMethodNameMap = new ConcurrentHashMap<>();
        getAdvancementProgressCompletedCriteriaMethodNameMap.put("v1_17_R1", "getAwardedCriteria");
        getAdvancementProgressCompletedCriteriaMethodNameMap.put("v1_18_R1", "f");
        getAdvancementProgressCompletedCriteriaMethodNameMap.put("v1_18_R2", "f");
        getAdvancementProgressCompletedCriteriaMethodNameMap.put("v1_19_R1", "f");
        getAdvancementProgressCompletedCriteriaMethodNameMap.put("v1_19_R2", "f");
        getAdvancementProgressCompletedCriteriaMethodNameMap.put("v1_19_R3", "f");
        getAdvancementProgressCompletedCriteriaMethodNameMap.put("v1_20_R1", "f");
        methodNameMap.put("getAdvancementProgressCompletedCriteria", getAdvancementProgressCompletedCriteriaMethodNameMap);

        Map<String, String> advancementFromJsonMethodNameMap = new ConcurrentHashMap<>();
        advancementFromJsonMethodNameMap.put("v1_17_R1", "a");
        advancementFromJsonMethodNameMap.put("v1_18_R1", "a");
        advancementFromJsonMethodNameMap.put("v1_18_R2", "a");
        advancementFromJsonMethodNameMap.put("v1_19_R1", "a");
        advancementFromJsonMethodNameMap.put("v1_19_R2", "a");
        advancementFromJsonMethodNameMap.put("v1_19_R3", "a");
        advancementFromJsonMethodNameMap.put("v1_20_R1", "a");
        methodNameMap.put("advancementFromJson", advancementFromJsonMethodNameMap);

        Map<String, String> treeNodeRunMethodNameMap = new ConcurrentHashMap<>();
        treeNodeRunMethodNameMap.put("v1_17_R1", "a");
        treeNodeRunMethodNameMap.put("v1_18_R1", "a");
        treeNodeRunMethodNameMap.put("v1_18_R2", "a");
        treeNodeRunMethodNameMap.put("v1_19_R1", "a");
        treeNodeRunMethodNameMap.put("v1_19_R2", "a");
        treeNodeRunMethodNameMap.put("v1_19_R3", "a");
        treeNodeRunMethodNameMap.put("v1_20_R1", "a");
        methodNameMap.put("treeNodeRun", treeNodeRunMethodNameMap);

        //以下加载变量
        fieldNameMap = new ConcurrentHashMap<>();

        Map<String, String> serverAdvancementsFieldNameMap = new ConcurrentHashMap<>();
        serverAdvancementsFieldNameMap.put("v1_17_R1", "c");
        serverAdvancementsFieldNameMap.put("v1_18_R1", "c");
        serverAdvancementsFieldNameMap.put("v1_18_R2", "c");
        serverAdvancementsFieldNameMap.put("v1_19_R1", "c");
        serverAdvancementsFieldNameMap.put("v1_19_R2", "c");
        serverAdvancementsFieldNameMap.put("v1_19_R3", "c");
        serverAdvancementsFieldNameMap.put("v1_20_R1", "c");
        fieldNameMap.put("serverAdvancements", serverAdvancementsFieldNameMap);

        Map<String, String> serverPlayerListFieldNameMap = new ConcurrentHashMap<>();
        serverPlayerListFieldNameMap.put("v1_17_R1", "j");
        serverPlayerListFieldNameMap.put("v1_18_R1", "j");
        serverPlayerListFieldNameMap.put("v1_18_R2", "j");
        serverPlayerListFieldNameMap.put("v1_19_R1", "k");
        serverPlayerListFieldNameMap.put("v1_19_R2", "k");
        serverPlayerListFieldNameMap.put("v1_19_R3", "k");
        serverPlayerListFieldNameMap.put("v1_20_R1", "k");
        fieldNameMap.put("serverPlayerList", serverPlayerListFieldNameMap);
    }

}
