package net.novauniverse.crates;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.novauniverse.crates.commands.crate.CrateCommand;
import net.novauniverse.crates.commands.crates.CratesCommand;
import net.novauniverse.crates.create.manager.CrateManager;
import net.zeeraa.novacore.spigot.command.CommandRegistry;
import net.zeeraa.novacore.spigot.module.ModuleManager;

public class NovaCrates extends JavaPlugin {
	private static NovaCrates instance;

	public static NovaCrates getInstance() {
		return instance;
	}

	public static CrateManager getCrateManager() {
		return CrateManager.getInstance();
	}

	@Override
	public void onEnable() {
		NovaCrates.instance = this;

		try {
			FileUtils.forceMkdir(getDataFolder());
		} catch (IOException e) {
			e.printStackTrace();
		}

		ModuleManager.loadModule(CrateManager.class, true);
		CommandRegistry.registerCommand(new CrateCommand());
		CommandRegistry.registerCommand(new CratesCommand());

	}

	@Override
	public void onDisable() {
		ModuleManager.disable(CrateManager.class);
		HandlerList.unregisterAll((Plugin) this);
		Bukkit.getServer().getScheduler().cancelTasks(this);
	}
}