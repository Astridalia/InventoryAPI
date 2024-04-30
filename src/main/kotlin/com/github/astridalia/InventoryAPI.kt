package com.github.astridalia

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*


class InventoryAPI<T : JavaPlugin>(val plugin: T) : Listener {
    private val playerToGUIMap: MutableMap<UUID, InventoryGUI<T>> = HashMap<UUID, InventoryGUI<T>>()

    init {
        check(plugin.isEnabled) { "Your plugin must be initialized before instantiating an instance of GUIAPI." }
        plugin.server
            .pluginManager
            .registerEvents(this, plugin)
    }

    fun openGUI(player: Player, gui: InventoryGUI<T>) {
        gui.open(player)
        playerToGUIMap[player.uniqueId] = gui
    }

    fun getOpenGUI(player: Player): InventoryGUI<T>? {
        return playerToGUIMap[player.uniqueId]
    }

    @EventHandler
    private fun onClick(event: InventoryClickEvent) {
        if (event.whoClicked !is Player) return
        val open: InventoryGUI<T> = getOpenGUI(event.whoClicked as Player) ?: return
        open.handleOnClick(event)
    }

    @EventHandler
    private fun onInventoryClose(event: InventoryCloseEvent) {
        if (event.player !is Player) return
        val player = event.player as Player
        val openGUI: InventoryGUI<T> = getOpenGUI(player) ?: return
        if (!openGUI.canClose(player)) {
            // Delay task to prevent overflow
            Bukkit.getScheduler().runTaskTimer(plugin, Runnable { openGUI.open(player) }, 0L, 2L)
            return
        }
        openGUI.onClose(player)
        playerToGUIMap.remove(player.uniqueId)
    }
}