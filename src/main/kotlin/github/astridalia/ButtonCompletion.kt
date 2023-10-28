package github.astridalia

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface ButtonCompletion {
    fun onClick(player: Player, currentItem: ItemStack?): InventoryGUI.ButtonAction?

}