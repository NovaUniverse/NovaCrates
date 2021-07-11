package net.novauniverse.crates.commands.crates.animation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.novauniverse.crates.create.CrateData;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependantUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.ColoredBlockType;
import net.zeeraa.novacore.spigot.module.modules.gui.holders.GUIReadOnlyHolder;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

public class CrateAnimation {
	private Player player;
	private CrateData crate;
	private Inventory inventory;
	private List<ItemStack> spinner;
	private List<ItemStack> avaliableItems;

	public CrateAnimation(Player player, CrateData crate) {
		spinner = new ArrayList<>();
		avaliableItems = new ArrayList<>();

		while (spinner.size() < 9) {
			this.pushToSpinner();
		}

		GUIReadOnlyHolder holder = new GUIReadOnlyHolder();
		inventory = Bukkit.getServer().createInventory(holder, 3 * 9, crate.getDisplayName());

		for (int i = 0; i < inventory.getSize(); i++) {
			inventory.setItem(i, new ItemBuilder(VersionIndependantUtils.get().getColoredItem(DyeColor.WHITE, ColoredBlockType.GLASS_PANE)).setName(" ").build());
		}

		inventory.setItem(4, new ItemBuilder(VersionIndependantUtils.get().getColoredItem(DyeColor.LIME, ColoredBlockType.GLASS_PANE)).setName(" ").build());
		inventory.setItem(22, new ItemBuilder(VersionIndependantUtils.get().getColoredItem(DyeColor.LIME, ColoredBlockType.GLASS_PANE)).setName(" ").build());

		this.draw();

		player.openInventory(inventory);
	}

	private void pushToSpinner() {
		if (avaliableItems.size() == 0) {
			for (ItemStack item : crate.getItems()) {
				avaliableItems.add(item.clone());
			}

			Collections.shuffle(avaliableItems);
		}

		spinner.add(avaliableItems.remove(0));
	}

	private void draw() {
		int i = 9;

		for (ItemStack item : spinner) {
			inventory.setItem(i, item);
			i++;
		}
	}
}