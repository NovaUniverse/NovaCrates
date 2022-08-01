package net.novauniverse.crates.commands.crate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.crates.create.CrateData;
import net.novauniverse.crates.create.manager.CrateManager;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

public class CrateAddCommand extends NovaSubCommand {
	public CrateAddCommand() {
		super("add");

		setPermission("novacrates.command.crate.add");
		setPermissionDefaultValue(PermissionDefault.OP);
		setDescription("Add a new crate");
		setAllowedSenders(AllowedSenders.ALL);

		addHelpSubCommand();
		setEmptyTabMode(true);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!CrateManager.getInstance().isEnabled()) {
			sender.sendMessage(ChatColor.RED + "Crates are disabled");
			return false;
		}

		if (args.length > 0) {
			String name = args[0];

			if (CrateManager.getInstance().getCrate(name) != null) {
				sender.sendMessage(ChatColor.RED + "A crate with that name already exists");
				return true;
			}

			List<ItemStack> placeholder = new ArrayList<>();

			placeholder.add(new ItemBuilder(Material.DIRT).setName("Placeholder").build());

			CrateData newCreate = new CrateData(placeholder, Material.CHEST, Material.STICK, name, name, 1);

			try {
				newCreate.save();
				CrateManager.getInstance().getCrates().add(newCreate);
				sender.sendMessage(ChatColor.GOLD + "Added a new crate named " + ChatColor.AQUA + name);
			} catch (IOException e) {
				e.printStackTrace();
				sender.sendMessage(ChatColor.RED + "Failed to save crate data. " + e.getClass().getName() + " " + e.getMessage());
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Please provilde a name for the crate");
		}
		return true;
	}
}