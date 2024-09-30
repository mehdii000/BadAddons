package mehdi.bad.addons.Commands.Subcommands;

import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Config.modules.MovableConfigManager;
import mehdi.bad.addons.Guis.GuiEditorGui;
import net.minecraft.command.ICommandSender;

public class GuiSubcommand implements Subcommand {

	@Override
	public String getCommandName() {
		return "gui";
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
		return "Returns the cheapest pieces of each attribute";
	}

	@Override
	public boolean processCommand(ICommandSender sender, String[] args) {
		MovableConfigManager.load();
		BadAddons.guiToOpen = new GuiEditorGui();
		return true;
	}
	
}
