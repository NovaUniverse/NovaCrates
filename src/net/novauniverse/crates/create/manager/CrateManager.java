package net.novauniverse.crates.create.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.JSONException;
import org.json.JSONObject;

import net.novauniverse.crates.NovaCrates;
import net.novauniverse.crates.create.CrateData;
import net.novauniverse.crates.create.editor.CrateEditorInventoryHolder;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.gui.GUIAction;
import net.zeeraa.novacore.spigot.module.modules.gui.callbacks.GUIClickCallback;
import net.zeeraa.novacore.spigot.module.modules.gui.holders.GUIReadOnlyHolder;

public class CrateManager extends NovaModule implements Listener {
	private static CrateManager instance;
	private File dataFolder;

	private List<CrateData> crates;

	public static CrateManager getInstance() {
		return instance;
	}

	@Override
	public void onLoad() {
		CrateManager.instance = this;
		this.crates = new ArrayList<>();
		this.dataFolder = new File(NovaCrates.getInstance().getDataFolder() + File.separator + "creates");

		if (!dataFolder.exists()) {
			dataFolder.mkdir();
		}
	}

	public CrateData getCrate(String crateName) {
		for (CrateData crate : crates) {
			if (crate.getName().equalsIgnoreCase(crateName)) {
				return crate;
			}
		}
		return null;
	}

	@Override
	public void onEnable() throws Exception {
		loadCreates();
	}

	public void loadCreates() throws JSONException, IOException {
		for (File file : dataFolder.listFiles()) {
			if (file.isDirectory()) {
				continue;
			}

			if (!FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("json")) {
				continue;
			}

			Log.info(getName(), "Reading create from file " + file.getName());

			JSONObject json = JSONFileUtils.readJSONObjectFromFile(file);

			CrateData create = CrateData.deserialize(json);

			crates.add(create);
		}

		crates.sort(new CrateComparator());
	}

	public File getDataFolder() {
		return dataFolder;
	}

	public List<CrateData> getCrates() {
		return crates;
	}

	@Override
	public String getName() {
		return "PixelmonCreateManager";
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClose(InventoryCloseEvent e) {
		if (e.getInventory().getHolder() instanceof CrateEditorInventoryHolder) {
			CrateEditorInventoryHolder holder = (CrateEditorInventoryHolder) e.getInventory().getHolder();

			CrateData createData = this.getCrate(holder.getCrateName());

			createData.getItems().clear();

			for (ItemStack item : e.getInventory().getContents()) {
				if (item == null) {
					continue;
				}

				if (item.getType() == Material.AIR) {
					continue;
				}

				createData.getItems().add(item);
			}

			try {
				createData.save();
			} catch (IOException e1) {
				e1.printStackTrace();
				e.getPlayer().sendMessage(ChatColor.RED + "Failed to save create data. " + e1.getClass().getName() + " " + e1.getMessage());
				return;
			}

			e.getPlayer().sendMessage(ChatColor.GREEN + "Create saved");
		}
	}

	public void showMenu(Player player) {
		int rows = (int) Math.ceil(crates.size() / 9);

		if (rows == 0) {
			rows = 1;
		}

		GUIReadOnlyHolder holder = new GUIReadOnlyHolder();
		Inventory inventory = Bukkit.getServer().createInventory(holder, rows * 9, ChatColor.GOLD + "Crates");

		int i = 0;
		for (CrateData crate : crates) {
			inventory.setItem(i, crate.getIconItemStack());
			holder.addClickCallback(i, new GUIClickCallback() {

				@Override
				public GUIAction onClick(Inventory clickedInventory, Inventory inventory, HumanEntity entity, int clickedSlot, SlotType slotType, InventoryAction clickType) {
					if (!openCrate(player, crate, true)) {
						player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1F, 1F);
						player.sendMessage(ChatColor.RED + "You dont have the required key to open this crate");
					}
					return GUIAction.CANCEL_INTERACTION;
				}
			});
			i++;
		}

		player.openInventory(inventory);
	}

	public boolean openCrate(Player player, CrateData crate, boolean useKey) {
		if (useKey) {
			boolean removed = false;

			for (int i = 0; i < player.getInventory().getSize(); i++) {
				ItemStack item = player.getInventory().getItem(i);

				if (item != null) {
					if (item.getType() != Material.AIR) {
						if (crate.isKeyItem(item)) {
							if (item.getAmount() == 0) {
								player.getInventory().remove(item);
								removed = true;
								break;
							} else {
								item.setAmount(item.getAmount() - 1);
								player.getInventory().setItem(i, item);
								removed = true;
								break;
							}
						}
					}
				}
			}

			if (!removed) {
				return false;
			}
		}

		crate.open(player);

		return true;
	}

	public void reload() throws JSONException, IOException {
		crates.clear();
		loadCreates();
	}
}