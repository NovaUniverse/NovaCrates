package net.novauniverse.crates.commands.crate;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.crates.create.CrateData;
import net.novauniverse.crates.create.manager.CrateManager;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;

public class CrateEditCommand extends NovaSubCommand {
	public CrateEditCommand() {
		super("edit");

		setPermission("novacrates.command.crate.edit");
		setPermissionDefaultValue(PermissionDefault.OP);
		setDescription("Edit a crate");
		setAllowedSenders(AllowedSenders.PLAYERS);
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
				crate.openEditor((Player) sender);
			} else {
				sender.sendMessage(ChatColor.RED + "Could not find crate named " + ChatColor.AQUA + name);
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Please provide the name of the crate you want to edit");
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