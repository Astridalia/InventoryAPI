package com.github.astridalia

import org.bukkit.inventory.ItemStack


data class InventoryItem(var onClick: ButtonCompletion, var itemStack: ItemStack)
