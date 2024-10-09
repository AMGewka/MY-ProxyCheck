package ru.AMGewka.proxyCheck;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.HashSet;
import java.util.List;

public class ProxyCheck extends JavaPlugin implements Listener {
    private HashSet<String> whiteList;
    private HashSet<String> blackList;
    private String apiKey;

    @Override
    public void onEnable() {
        saveDefaultConfig(); // Сохранение конфигурации по умолчанию
        apiKey = getConfig().getString("api_key");
        createFiles();
        loadLists();
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
    }

    private void createFiles() {
        File whiteListFile = new File(getDataFolder(), "WhiteList.json");
        File blackListFile = new File(getDataFolder(), "BlackList.json");

        try {
            if (!whiteListFile.exists()) {
                whiteListFile.createNewFile();
            }
            if (!blackListFile.exists()) {
                blackListFile.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLists() {
        this.whiteList = new HashSet<>(ApiClient.loadList(new File(getDataFolder(), "WhiteList.json")));
        this.blackList = new HashSet<>(ApiClient.loadList(new File(getDataFolder(), "BlackList.json")));

        // Убедимся, что списки не null
        if (this.whiteList == null) {
            getLogger().warning("WhiteList пуст, инициализируется как пустой.");
            this.whiteList = new HashSet<>();
        }
        if (this.blackList == null) {
            getLogger().warning("BlackList пуст, инициализируется как пустой.");
            this.blackList = new HashSet<>();
        }

        getLogger().info("Загруженный WhiteList имеет строк: " + this.whiteList.size());
        getLogger().info("Загруженный BlackList имеет строк: " + this.blackList.size());
    }

    public HashSet<String> getWhiteList() {
        return whiteList;
    }

    public HashSet<String> getBlackList() {
        return blackList;
    }

    public String getApiKey() {
        return apiKey;
    }
}