package mehdi.bad.addons.Commands.Subcommands;

import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Features.Kuudra.SupplyHighlight;
import mehdi.bad.addons.Objects.Pathfinding;
import net.minecraft.command.ICommandSender;

public class SendNotifCommand implements Subcommand {

	@Override
	public String getCommandName() {
		return "pathfind";
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
		return "Outputs custom notification";
	}

	@Override
	public boolean processCommand(ICommandSender sender, String[] args) {
		if (SupplyHighlight.pathStart == null && SupplyHighlight.pathEnd == null) {
			SupplyHighlight.pathStart = sender.getPosition();
		} else if (SupplyHighlight.pathStart != null & SupplyHighlight.pathEnd == null) {
			SupplyHighlight.pathEnd = sender.getPosition();
			SupplyHighlight.path = Pathfinding.getShortestPathBetween(BadAddons.mc.theWorld, SupplyHighlight.pathStart, SupplyHighlight.pathEnd, 1);
		} else {
			SupplyHighlight.pathStart = null;
			SupplyHighlight.pathEnd = null;
			SupplyHighlight.path = null;
		}
		return true;
	}



}
