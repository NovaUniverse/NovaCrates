package net.novauniverse.crates.commands.crate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.crates.create.CrateData;
import net.novauniverse.crates.create.manager.CrateManager;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;

public class CrateSetDisplayNameCommand extends NovaSubCommand {
	public CrateSetDisplayNameCommand() {
		super("setdisplayname");

		setPermission("novacretes.command.crate.setdisplayname");
		setPermissionDefaultValue(PermissionDefault.OP);
		setDescription("Set the display name of a create");
		setAllowedSenders(AllowedSenders.PLAYERS);
		setFilterAutocomplete(true);
		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if(!CrateManager.getInstance().isEnabled()) {
			sender.sendMessage(ChatColor.RED + "Crates are disabled");
			return false;
		}
		
		if (args.length > 0) {
			String name = args[0];

			CrateData crate = CrateManager.getInstance().getCrate(name);

			if (crate != null) {
				if (args.length > 1) {
					String newName = "";

					for (int i = 1; i < args.length; i++) {
						newName += args[i] + " ";
					}

					newName = ChatColor.translateAlternateColorCodes('&', newName);

					crate.setDisplayName(newName.trim());

					try {
						crate.save();
						sender.sendMessage(ChatColor.GREEN + "The name of " + ChatColor.AQUA + crate.getName() + ChatColor.GREEN + " is now " + ChatColor.AQUA + newName);
					} catch (IOException e) {
						sender.sendMessage(ChatColor.RED + "Failed to save create data. " + e.getClass().getName() + " " + e.getMessage());
						e.printStackTrace();
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Please provide a new name for the create. & can be used to set a colored name");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Could not find create named " + ChatColor.AQUA + name);
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