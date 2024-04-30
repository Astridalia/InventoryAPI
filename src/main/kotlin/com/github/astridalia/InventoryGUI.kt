package com.github.astridalia

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

abstract class InventoryGUI<T : JavaPlugin>(open val plugin: T, open val size: Int = InventoryType.CHEST.defaultSize) : InventoryHolder {


    private val itemPositionMap: MutableMap<Int, InventoryItem> = mutableMapOf()

    private fun createInventory(): Inventory {
        val validSize = when {
            size < 9 -> 9
            size > 54 -> 54
            size % 9 != 0 -> (size / 9 + 1) * 9
            else -> size
        }
        return Bukkit.createInventory(this, validSize, title)
    }

    protected abstract val title: String
    private val inventory: Inventory = createInventory()
    abstract fun canClose(player: Player): Boolean

    open fun onClose(player: Player) {}

    protected open fun generate() {
        itemPositionMap.forEach { (index, item) ->
            inventory.setItem(index, item.itemStack)
        }
    }

    private fun set(index: Int, item: InventoryItem) {
        if (index >= size)
            throw IllegalArgumentException("Invalid index $index for inventory of size $size [${javaClass.name}]")
        itemPositionMap.remove(index)
        itemPositionMap[index] = item
        generate()
    }

    protected fun set(index: Int, itemStack: ItemStack, onClick: ButtonCompletion) {
        set(index, InventoryItem(onClick, itemStack))
    }

    protected fun clear() {
        inventory.clear()
    }

    enum class ButtonAction {
        CLOSE_GUI,
        CANCEL
    }

    open fun open(player: Player) {
        generate()
        player.openInventory(inventory)
    }



    open fun handleOnClick(event: InventoryClickEvent) {

        val clickedInventory = event.clickedInventory ?: return

        if (clickedInventory != inventory) return
        if (event.whoClicked !is Player) return

        val index = event.slot
        val item = itemPositionMap[index]

        val player = event.whoClicked as Player
        event.isCancelled = true


        val result = item?.onClick?.onClick(player, item.itemStack) ?: return

        if (result == ButtonAction.CLOSE_GUI && canClose(player)) {
            event.whoClicked.closeInventory()
        }
    }
}

