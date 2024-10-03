package mehdi.bad.addons.Commands.Subcommands;

import mehdi.bad.addons.Objects.UpdateCheck;
import mehdi.bad.addons.utils.ChatLib;
import net.minecraft.command.ICommandSender;

public class UpdatemodSubcommand implements Subcommand {

	@Override
	public String getCommandName() {
		return "updatemod";
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
		return "Sets positions and predicts Ender Pearl landing.";
	}

	@Override
	public boolean processCommand(ICommandSender sender, String[] args) {
		if (UpdateCheck.shouldUpdate && !UpdateCheck.isUpdating) {
			ChatLib.chat("§a[BA] Updating, please wait a moment...");
			UpdateCheck.isUpdating = true;
			UpdateCheck.downloadAndExtractMod();
		} else if (UpdateCheck.isUpdating) {
			ChatLib.chat("§c[BA] Mod is already or is updating!");
		}
		return true;
	}

}