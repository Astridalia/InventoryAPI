package com.github.astridalia

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.function.BiConsumer


abstract class GUI<T : JavaPlugin?>(val plugin: T) : InventoryHolder {
    private val inventory: Inventory

    private val itemPositionMap: MutableMap<Int, GUIItem> = HashMap()

    init {
        this.inventory = Bukkit.createInventory(this, size, title!!)
    }

    abstract val size: Int
    abstract val title: String?

    abstract fun canClose(player: Player?): Boolean

    fun onClose(player: Player?) {}

    protected fun generate() {
        itemPositionMap.forEach { (index: Int?, item: GUIItem) ->
            inventory.setItem(
                index, item.itemStack
            )
        }
    }

    private fun set(index: Int, item: GUIItem?) {
        require(index < size) { "Invalid index " + index + " for inventory of size " + size + " [" + javaClass.name + "]" }

        itemPositionMap.remove(index)
        if (item?.itemStack != null) {
            itemPositionMap[index] = item
        }

        generate()
    }

    protected fun set(index: Int, itemStack: ItemStack) {
        set(index, GUIItem(null, itemStack))
    }

    protected fun set(index: Int, itemStack: ItemStack, onClick: ButtonCompletion) {
        set(index, GUIItem(onClick, itemStack))
    }

    protected fun clear() {
        inventory.clear()
    }

    interface ButtonCompletion {
        fun onClick(whoClicked: Player?, clickedItem: ItemStack?): ButtonAction?
    }

    enum class ButtonAction {
        CLOSE_GUI,
        CANCEL
    }

    fun open(player: Player) {
        generate()
        player.openInventory(inventory)
    }

    fun handleOnClick(event: InventoryClickEvent) {
        if (event.clickedInventory == null) return
        if (event.clickedInventory != inventory) return
        if (event.whoClicked !is Player) return

        val index = event.slot
        val item: GUIItem = itemPositionMap[index] ?: return

        if (item.onClick == null) {
            event.isCancelled = true
            return
        }

        val player = event.whoClicked as Player

        event.isCancelled = true

        val result: ButtonAction? = item.onClick.onClick(player, event.currentItem)
        if (result == ButtonAction.CLOSE_GUI && canClose(player)) {
            event.whoClicked.closeInventory()
        }
    }
}