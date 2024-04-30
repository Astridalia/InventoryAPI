package com.github.astridalia

import com.github.astridalia.GUI.ButtonCompletion
import org.bukkit.inventory.ItemStack


data class GUIItem (val onClick: ButtonCompletion?, val itemStack: ItemStack)
