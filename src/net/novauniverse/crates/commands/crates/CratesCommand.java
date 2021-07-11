package net.novauniverse.crates.commands.crates;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.crates.NovaCrates;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class CratesCommand extends NovaCommand {

	public CratesCommand() {
		super("creates", NovaCrates.getInstance());

		setPermission("novacreates.command.creates");
		setPermissionDefaultValue(PermissionDefault.TRUE);
		setAllowedSenders(AllowedSenders.PLAYERS);
		setDescription("Open the create menu");
		setEmptyTabMode(true);
		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		return true;
	}
}