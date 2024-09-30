package mehdi.bad.addons.Features.Dungeons;

import mehdi.bad.addons.Events.DungeonEnterRoomEvent;
import mehdi.bad.addons.Events.TickEndEvent;
import mehdi.bad.addons.Objects.ScoreBoard;
import mehdi.bad.addons.utils.SkyblockUtils;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DungeonRoomDetection {

    private static String currentRoom = "none";

    @SubscribeEvent
    public void onTickStuff(TickEndEvent e) {
        if (SkyblockUtils.isInDungeon() && SkyblockUtils.isInSkyblock()) {
            Data data = Data.getDataFromDecodedString(ScoreBoard.getLines() == null ? "null" : ScoreBoard.getLines().get(ScoreBoard.getLines().size()-1));
            if (data != null) {
                DungeonRoomDetection.currentRoom = data.name;
                DungeonEnterRoomEvent event = new DungeonEnterRoomEvent(data.name);
                event.post();
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload e) {
        DungeonRoomDetection.currentRoom = "out";
    }

}
