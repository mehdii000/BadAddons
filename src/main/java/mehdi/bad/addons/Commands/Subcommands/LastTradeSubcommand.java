package mehdi.bad.addons.Commands.Subcommands;

import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Guis.LastTradeGUI;
import net.minecraft.command.ICommandSender;

public class LastTradeSubcommand implements Subcommand {

	@Override
	public String getCommandName() {
		return "lastTrade";
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
		return "Outputs your last trade gui.";
	}

	@Override
	public boolean processCommand(ICommandSender sender, String[] args) {

		BadAddons.guiToOpen = new LastTradeGUI();

		return true;
	}

	
	
}
