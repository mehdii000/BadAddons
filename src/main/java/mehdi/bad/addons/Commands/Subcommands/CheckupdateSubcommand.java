package mehdi.bad.addons.Commands.Subcommands;

import mehdi.bad.addons.Objects.UpdateCheck;
import net.minecraft.command.ICommandSender;

public class CheckupdateSubcommand implements Subcommand {

	@Override
	public String getCommandName() {
		return "checkforupdate";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public String getCommandUsage() {
		return "";
	}

	@Override
	public String getCommandDescription() {
		return "Checks for an update!";
	}

	@Override
	public boolean processCommand(ICommandSender sender, String[] args) {
		UpdateCheck.checkForUpdates();
		return true;
	}

}