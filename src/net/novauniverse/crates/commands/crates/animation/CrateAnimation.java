package net.novauniverse.crates.commands.crates.animation;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.novauniverse.crates.NovaCrates;
import net.novauniverse.crates.create.CrateData;
import net.zeeraa.novacore.commons.utils.ListUtils;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependantUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.ColoredBlockType;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependantSound;
import net.zeeraa.novacore.spigot.module.modules.gui.holders.GUIReadOnlyHolder;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

public class CrateAnimation {
	private Player player;
	private CrateData crate;
	private Inventory inventory;
	private List<ItemStack> spinner;
	private List<ItemStack> avaliableItems;

	private int speed = 20;
	private int stepsLeft = 0;

	public CrateAnimation(Player player, CrateData crate) {
		this.player = player;
		this.crate = crate;

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

		this.render();

		player.openInventory(inventory);

		this.tick();
	}

	private void tick() {
		VersionIndependantUtils.get().playSound(player, player.getLocation(), VersionIndependantSound.NOTE_HAT, 1F, 1.5F);

		spinner.remove(0);
		this.pushToSpinner();
		this.render();

		// Log.trace("CreateTick", "Speed: " + speed + " Steps left: " + stepsLeft);

		stepsLeft--;
		if (stepsLeft <= 0) {
			speed--;

			if (speed < 15) {
				speed--;
			}

			if (speed < 0) {
				speed = 0;
			}

			if (speed <= 15) {
				int ri = NovaCrates.getInstance().getRandomInstance().nextInt(10);

				if (ri == 1) {
					done();

					return;
				}
			}

			if (speed <= 1) {
				done();

				return;
			}

			int div = 1;

			if (speed < 13) {
				div = 3;
			} else if (speed < 16) {
				div = 2;
			}

			stepsLeft = speed / div;

			if (stepsLeft < 1) {
				stepsLeft = 1;
			}
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				tick();
			}
		}.runTaskLater(NovaCrates.getInstance(), 20 - speed);
	}

	private void done() {
		new BukkitRunnable() {

			@Override
			public void run() {
				ItemStack item = spinner.get(4);

				for (int i = 0; i < inventory.getSize(); i++) {
					inventory.setItem(i, new ItemBuilder(VersionIndependantUtils.get().getColoredItem(DyeColor.WHITE, ColoredBlockType.GLASS_PANE)).setName(" ").build());
				}
				inventory.setItem(4, new ItemBuilder(VersionIndependantUtils.get().getColoredItem(DyeColor.LIME, ColoredBlockType.GLASS_PANE)).setName(" ").build());
				inventory.setItem(22, new ItemBuilder(VersionIndependantUtils.get().getColoredItem(DyeColor.LIME, ColoredBlockType.GLASS_PANE)).setName(" ").build());

				inventory.setItem(13, item);

				VersionIndependantUtils.get().playSound(player, player.getLocation(), VersionIndependantSound.NOTE_PLING);

				player.getInventory().addItem(item);
			}
		}.runTaskLater(NovaCrates.getInstance(), 20L);
	}

	private void pushToSpinner() {
		if (avaliableItems.size() == 0) {
			for (ItemStack item : crate.getItems()) {
				avaliableItems.add(item.clone());
			}

			// Collections.shuffle(avaliableItems);

			ListUtils.shuffleWithRandom(avaliableItems, NovaCrates.getInstance().getRandomInstance());
		}

		spinner.add(avaliableItems.remove(0));
	}

	private void render() {
		int i = 9;

		for (ItemStack item : spinner) {
			inventory.setItem(i, item);
			i++;
		}
	}
}