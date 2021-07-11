package net.novauniverse.crates.create;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONObject;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import net.novauniverse.crates.create.editor.CrateEditorInventoryHolder;
import net.novauniverse.crates.create.manager.CrateManager;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.spigot.utils.BukkitSerailization;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

public class CrateData {
	private List<ItemStack> items;
	private Material icon;
	private Material key;
	private String name;
	private String displayName;

	public CrateData(List<ItemStack> items, Material icon, Material key, String name, String displayName) {
		this.items = items;
		this.icon = icon;
		this.key = key;
		this.name = name;
		this.displayName = displayName;
	}

	public List<ItemStack> getItems() {
		return items;
	}

	public Material getIcon() {
		return icon;
	}

	public void setIcon(Material icon) {
		this.icon = icon;
	}

	public Material getKey() {
		return key;
	}

	public void setKey(Material key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public JSONObject serialize() throws IOException {
		JSONObject json = new JSONObject();

		JSONArray itemsJson = new JSONArray();

		for (ItemStack item : items) {
			itemsJson.put(BukkitSerailization.itemStackToBase64(item));
		}

		json.put("name", name);
		json.put("icon", icon.name());
		json.put("key", key.name());
		json.put("display_name", displayName);
		json.put("items", itemsJson);

		return json;
	}

	public ItemStack getKeyItemStack() {
		ItemBuilder builder = new ItemBuilder(key);

		builder.setName(ChatColor.GOLD + "" + ChatColor.BOLD + displayName + " key");
		builder.addLore(ChatColor.WHITE + "This item can be used to");
		builder.addLore(ChatColor.WHITE + "open a " + displayName);

		ItemStack stack = builder.build();

		stack = NBTEditor.set(stack, name, "novacreates", "key", "cratename");

		return stack;
	}

	public boolean isKeyItem(ItemStack item) {
		if (NBTEditor.contains(item, "novacreates", "key", "cratename")) {
			String createName = NBTEditor.getString(item, "novacreates", "key", "cratename");

			if (createName != null) {
				return createName.equalsIgnoreCase(name);
			}
		}

		return false;
	}

	public static CrateData deserialize(JSONObject json) throws IOException {
		JSONArray itemsJson = json.getJSONArray("items");

		List<ItemStack> items = new ArrayList<ItemStack>();

		for (int i = 0; i < itemsJson.length(); i++) {
			items.add(BukkitSerailization.itemStackFromBase64(itemsJson.getString(i)));
		}

		Material icon = Material.DIRT;
		Material key = Material.STICK;

		String iconMaterialName = json.getString("icon");
		String keyMaterialName = json.getString("key");
		String name = json.getString("name");
		String displayName = json.getString("display_name");

		try {
			icon = Material.valueOf(iconMaterialName);
		} catch (Exception e) {
			Log.warn("CreateData", "Error while deserializing crate data for create " + name + ". Material " + iconMaterialName + " is not valid");
		}

		try {
			key = Material.valueOf(keyMaterialName);
		} catch (Exception e) {
			Log.warn("CreateData", "Error while deserializing crate data for create " + name + ". Material " + keyMaterialName + " is not valid");
		}

		return new CrateData(items, icon, key, name, displayName);
	}

	public void save() throws IOException {
		File file = new File(CrateManager.getInstance().getDataFolder().getAbsolutePath() + File.separator + name + ".json");

		Log.debug("CreateData", "Saving crate data to " + file.getAbsolutePath());

		JSONFileUtils.saveJson(file, this.serialize());
	}

	public void openEditor(Player player) {
		CrateEditorInventoryHolder holder = new CrateEditorInventoryHolder(name);
		Inventory inventory = Bukkit.getServer().createInventory(holder, 6 * 9, displayName);

		int i = 0;
		for (ItemStack item : items) {
			inventory.setItem(i, item);
			i++;
		}

		player.openInventory(inventory);
	}
}