package net.novauniverse.crates.commands.crate;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.crates.NovaCrates;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class CrateCommand extends NovaCommand {

	public CrateCommand() {
		super("crate", NovaCrates.getInstance());

		setPermission("novacrates.command.crate");
		setPermissionDefaultValue(PermissionDefault.OP);
		setDescription("Main command for crates");
		setAllowedSenders(AllowedSenders.ALL);
		addSubCommand(new CrateAddCommand());
		addSubCommand(new CrateEditCommand());
		addSubCommand(new CrateSetIconCommand());
		addSubCommand(new CrateSetKeyCommand());
		addSubCommand(new CrateGiveKeyCommand());
		addSubCommand(new CrateSetDisplayNameCommand());
		addHelpSubCommand();
		setEmptyTabMode(true);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		sender.sendMessage(ChatColor.GOLD + "Use" + ChatColor.AQUA + " /crate help" + ChatColor.GOLD + " for help");
		return true;
	}
}