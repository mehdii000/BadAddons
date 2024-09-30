package mehdi.bad.addons.Features.Dungeons.solvers;

import mehdi.bad.addons.Config.Configs;
import mehdi.bad.addons.Events.DungeonEnterRoomEvent;
import mehdi.bad.addons.utils.ChatLib;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WaterboardSolver {

    private final String WATER_BOARD_ROOM = "Water Board";

    @SubscribeEvent
    public void onEnterDungeonRoom(DungeonEnterRoomEvent event) {
        if (!Configs.SingleFlowSolver) return;
        ChatLib.chat("§7Entered: §b" + event.getRoomName());
    }

}
