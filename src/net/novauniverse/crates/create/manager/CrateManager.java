package net.novauniverse.crates.create.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.json.JSONException;
import org.json.JSONObject;

import net.novauniverse.crates.NovaCrates;
import net.novauniverse.crates.create.CrateData;
import net.novauniverse.crates.create.editor.CrateEditorInventoryHolder;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.spigot.module.NovaModule;

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
		
	}
}