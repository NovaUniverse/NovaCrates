package net.novauniverse.crates.commands.crate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.crates.create.CrateData;
import net.novauniverse.crates.create.manager.CrateManager;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependantUtils;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;

public class CrateSetKeyCommand extends NovaSubCommand {

	public CrateSetKeyCommand() {
		super("setkey");

		setPermission("novacrates.command.crate.setkey");
		setPermissionDefaultValue(PermissionDefault.OP);
		setDescription("Set the key of a crate to the item in your hand");
		setAllowedSenders(AllowedSenders.PLAYERS);
		setFilterAutocomplete(true);
		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (args.length > 0) {
			String name = args[0];

			CrateData create = CrateManager.getInstance().getCrate(name);

			if (create != null) {
				Player player = (Player) sender;
				ItemStack item = VersionIndependantUtils.get().getItemInMainHand(player);

				if (item != null) {
					if (item.getType() != Material.AIR) {
						create.setKey(item.getType());

						try {
							create.save();
							sender.sendMessage(ChatColor.GREEN + "The icon of " + ChatColor.AQUA + create.getName() + ChatColor.GREEN + " is now " + ChatColor.AQUA + item.getType().name());
						} catch (IOException e) {
							sender.sendMessage(ChatColor.RED + "Failed to save crate data. " + e.getClass().getName() + " " + e.getMessage());
							e.printStackTrace();
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Invalid item in hand");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Invalid item in hand");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Could not find create named " + ChatColor.AQUA + name);
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Please provide the name of the crate you want to edit");
		}
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
		List<String> result = new ArrayList<>();

		for (CrateData create : CrateManager.getInstance().getCrates()) {
			result.add(create.getName());
		}

		return result;
	}
}