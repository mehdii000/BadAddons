package mehdi.bad.addons.Events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class DungeonEnterRoomEvent extends Event {

    private String room_name;

    public DungeonEnterRoomEvent(String room_name) {
        this.room_name = room_name;
    }

    public String getRoomName() {
        return room_name;
    }

    public void post() {
        MinecraftForge.EVENT_BUS.post(this);
    }

}
