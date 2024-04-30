package com.github.astridalia

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin


abstract class GUI<T : JavaPlugin?>(private val plugin: T) : InventoryHolder {
    private val inventory: Inventory
    private val slotItemMap: MutableMap<Int, GUIItem> = HashMap()

    protected abstract val inventorySize: Int
    protected abstract val inventoryTitle: String

    init {
        this.inventory = Bukkit.createInventory(this, inventorySize, inventoryTitle)
    }

    abstract fun canClose(player: Player?): Boolean

    protected fun generateInventory() {
        slotItemMap.forEach { (index, item) ->
            inventory.setItem(index, item.itemStack)
        }
    }

    private fun setSlotItem(index: Int, item: GUIItem?) {
        require(index < inventorySize) { "Invalid index $index for inventory of size $inventorySize [${javaClass.name}]" }
        slotItemMap.remove(index)
        item?.let { slotItemMap[index] = it }
        generateInventory()
    }

    protected fun setSlotItem(index: Int, itemStack: ItemStack, onClick: ButtonCompletion? = null) {
        setSlotItem(index, GUIItem(onClick, itemStack))
    }

    protected fun clearInventory() {
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
        generateInventory()
        player.openInventory(inventory)
    }

    fun handleOnClick(event: InventoryClickEvent) {
        if (event.clickedInventory != inventory) return
        if (event.whoClicked !is Player) return

        val index = event.slot
        val item = slotItemMap[index] ?: return

        event.isCancelled = true

        val player = event.whoClicked as Player
        val result = item.onClick?.onClick(player, event.currentItem)

        if (result == ButtonAction.CLOSE_GUI && canClose(player)) {
            event.whoClicked.closeInventory()
        }
    }
}