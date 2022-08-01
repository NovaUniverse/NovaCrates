package net.novauniverse.crates.commands.crate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.crates.NovaCrates;
import net.novauniverse.crates.create.CrateData;
import net.novauniverse.crates.create.manager.CrateManager;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;

public class CrateSetSortingNumberCommand extends NovaSubCommand {
	public CrateSetSortingNumberCommand() {
		super("setsortingnumber");

		setPermission("novacrates.command.crate.setsortingnumber");
		setPermissionDefaultValue(PermissionDefault.OP);
		setDescription("Set the sorting number of a crate");
		setAllowedSenders(AllowedSenders.ALL);
		setFilterAutocomplete(true);
		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!CrateManager.getInstance().isEnabled()) {
			sender.sendMessage(ChatColor.RED + "Crates are disabled");
			return false;
		}

		if (args.length > 0) {
			String name = args[0];

			CrateData crate = CrateManager.getInstance().getCrate(name);

			if (crate != null) {
				if (args.length >= 2) {
					try {
						int number = Integer.parseInt(args[1]);

						crate.setSortingNumber(number);

						try {
							crate.save();
							sender.sendMessage(ChatColor.GREEN + "The sorting number of " + ChatColor.AQUA + crate.getName() + ChatColor.GREEN + " is now " + ChatColor.AQUA + number);

							try {
								NovaCrates.getCrateManager().reload();
							} catch (IOException e) {
								sender.sendMessage(ChatColor.RED + "Failed to reload create data. " + e.getClass().getName() + " " + e.getMessage());
								e.printStackTrace();
							}
						} catch (IOException e) {
							sender.sendMessage(ChatColor.RED + "Failed to save create data. " + e.getClass().getName() + " " + e.getMessage());
							e.printStackTrace();
						}
					} catch (NumberFormatException nfe) {
						sender.sendMessage(ChatColor.RED + "Please provide a valid number");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Please provide a number");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Could not find crate named " + ChatColor.AQUA + name);
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Please provide the name of the create you want to edit");
		}
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
		if (!CrateManager.getInstance().isEnabled()) {
			return new ArrayList<>();
		}

		List<String> result = new ArrayList<>();

		CrateManager.getInstance().getCrates().forEach(c -> result.add(c.getName()));

		return result;
	}
}