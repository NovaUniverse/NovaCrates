package net.novauniverse.crates.commands.crate;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.crates.create.CrateData;
import net.novauniverse.crates.create.manager.CrateManager;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;

public class CrateGiveKeyCommand extends NovaSubCommand {
	public CrateGiveKeyCommand() {
		super("givekey");

		setPermission("novacrates.command.crate.givekey");
		setPermissionDefaultValue(PermissionDefault.OP);
		setDescription("Give a key to the provided player. If no name is provided it will be given to the user of the command");
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

		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Please provide the name of the crate you want to get a key for");
		} else {
			String name = args[0];

			CrateData crate = CrateManager.getInstance().getCrate(name);

			if (crate != null) {

				if (args.length > 1) {
					Player player = Bukkit.getServer().getPlayer(args[1]);

					if (player != null) {
						if (player.isOnline()) {
							player.getInventory().addItem(crate.getKeyItemStack());
							sender.sendMessage(ChatColor.GREEN + "Key added");
						} else {
							sender.sendMessage(ChatColor.RED + "That player is not online");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Could not find a player with that name");
					}
				} else {
					if (sender instanceof InventoryHolder) {
						InventoryHolder holder = (InventoryHolder) sender;

						holder.getInventory().addItem(crate.getKeyItemStack());

						sender.sendMessage(ChatColor.GREEN + "Key added");
					} else {
						sender.sendMessage(ChatColor.RED + "You need to provinde a player to give the key to since you dont have an inventory");
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Could not find crate named " + ChatColor.AQUA + name);
			}
		}

		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
		if (!CrateManager.getInstance().isEnabled()) {
			return new ArrayList<>();
		}

		List<String> result = new ArrayList<>();

		if (args.length == 0 || args.length == 1) {
			CrateManager.getInstance().getCrates().forEach(c -> result.add(c.getName()));
		} else if (args.length == 2) {
			Bukkit.getServer().getOnlinePlayers().forEach(p -> result.add(p.getName()));
		}

		return result;
	}
}