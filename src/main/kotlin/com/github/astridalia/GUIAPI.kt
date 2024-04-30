package com.github.astridalia

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*


class GUIAPI<T : JavaPlugin>(private val plugin: T) : Listener {
    private val playerToGUIMap: MutableMap<UUID, GUI<T>> = mutableMapOf()

    init {
        check(plugin.isEnabled) { "Your plugin must be initialized before instantiating an instance of GUIAPI." }

        plugin.server
            .pluginManager
            .registerEvents(this, plugin)
    }

    fun openGUI(player: Player, gui: GUI<T>) {
        gui.open(player)
        playerToGUIMap[player.uniqueId] = gui
    }

    fun getOpenGUI(player: Player): GUI<T>? {
        return playerToGUIMap[player.uniqueId]
    }

    @EventHandler
    private fun onClick(event: InventoryClickEvent) {
        if (event.whoClicked !is Player) return
        val open = getOpenGUI(event.whoClicked as Player) ?: return
        open.handleOnClick(event)
    }

    @EventHandler
    private fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return
        val openGUI = getOpenGUI(player) ?: return
        if (!openGUI.canClose(player)) {
            Bukkit.getServer()
                .scheduler
                .runTaskLater(plugin, Runnable {
                    openGUI.open(player)
                }, 10)
            return
        }

        openGUI.onClose(player)
        playerToGUIMap.remove(player.uniqueId)
    }
}