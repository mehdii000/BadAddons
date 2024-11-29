package mehdi.bad.addons.Commands.Subcommands;

import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Features.Dungeons.catacombs.RoomDetection;
import mehdi.bad.addons.Features.Dungeons.utils.MapUtils;
import mehdi.bad.addons.utils.ChatLib;
import mehdi.bad.addons.utils.SkyblockUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

public class GetRelativePositionSubcommand implements Subcommand {

	@Override
	public String getCommandName() {
		return "getrelativeposition";
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
		return "Gets the relative position in a dungeon room";
	}

	@Override
	public boolean processCommand(ICommandSender sender, String[] args) {
		if (SkyblockUtils.isInDungeon() && BadAddons.currentRoom != null) {
			ChatLib.chat("§b[BA] §rRoom: " + BadAddons.currentRoom.name);
			ChatLib.chat("§b[BA] §rActual position: " + BadAddons.mc.thePlayer.getPosition());
			BlockPos relPos = MapUtils.actualToRelative(BadAddons.mc.thePlayer.getPosition(), RoomDetection.roomDirection, RoomDetection.roomCorner);
			ChatLib.chat("§b[BA] §rRelative position: " + relPos);

		} else {
			ChatLib.chat("§b[BA] §cNot in a dungeon room!");

		}
		return true;

	}
	
}
