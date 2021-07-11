package net.novauniverse.crates.create.editor;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CrateEditorInventoryHolder implements InventoryHolder {
	private String crateName;

	public CrateEditorInventoryHolder(String crateName) {
		this.crateName = crateName;
	}

	@Override
	public Inventory getInventory() {
		return null;
	}

	public String getCrateName() {
		return crateName;
	}
}