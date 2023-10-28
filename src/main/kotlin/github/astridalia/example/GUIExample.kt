package github.astridalia.example

import github.astridalia.ButtonCompletion
import github.astridalia.InventoryGUI
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class GUIExample(plugin: JavaPlugin) : InventoryGUI<JavaPlugin>(plugin) {
    private var clicks = 1

    init {
        generate()
    }

    override val size: Int = InventoryType.CHEST.defaultSize
    override val title: String = "Hello"
    override fun canClose(player: Player): Boolean {
        return true
    }

    override fun onClose(player: Player) {
        player.sendMessage("You closed the GUI!")
    }

    override fun generate() {
        clear()
        set(0, ItemStack(Material.DIAMOND), object : ButtonCompletion {
            override fun onClick(player: Player, currentItem: ItemStack?): ButtonAction? {
                ++clicks
                player.sendMessage("You $clicks clicked the diamond!")
                return null
            }
        })
}

    override fun getInventory(): Inventory {
        return Bukkit.createInventory(this, size, title)
    }
}