package ru.AMGewka.proxyCheck;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.io.File;

public class PlayerJoinListener implements Listener {
    private final ProxyCheck plugin;

    public PlayerJoinListener(ProxyCheck plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        if (!plugin.getWhiteList().contains(playerName) && !plugin.getBlackList().contains(playerName)) {
            // Проверяем, есть ли у игрока право обхода
            if (player.hasPermission("ProxyCheck.bypass")) {
                return; // Игрок может зайти, не проверяя
            }

            // Проверяем IP игрока через API
            new Thread(() -> {
                String ip = player.getAddress().getAddress().getHostAddress();
                boolean isProxy = ApiClient.checkProxy(ip, plugin.getApiKey());

                if (isProxy) {
                    plugin.getBlackList().add(playerName);
                    ApiClient.saveList(new File(plugin.getDataFolder(), "BlackList.json"), plugin.getBlackList());
                    player.kickPlayer("Вы используете VPN или прокси.");
                } else {
                    plugin.getWhiteList().add(playerName);
                    ApiClient.saveList(new File(plugin.getDataFolder(), "WhiteList.json"), plugin.getWhiteList());
                }
            }).start();
        }
    }
}
