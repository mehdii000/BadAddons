package mehdi.bad.addons.Commands.Subcommands;

import mehdi.bad.addons.Objects.TitleManager;
import net.minecraft.command.ICommandSender;

public class SendTitleSubcommand implements Subcommand {

	@Override
	public String getCommandName() {
		return "sendtitle";
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
		return "Outputs custom title";
	}

	@Override
	public boolean processCommand(ICommandSender sender, String[] args) {
		StringBuilder string = new StringBuilder();
		for (String s : args) {
			string.append(s);
			string.append(" ");
		}
		TitleManager.pushTitle(string.toString(), 1500);
		return true;
	}

	
	
}
