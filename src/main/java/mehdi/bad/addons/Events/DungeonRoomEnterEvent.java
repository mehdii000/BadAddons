package mehdi.bad.addons.Events;

import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Features.Dungeons.utils.Room;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class DungeonRoomEnterEvent extends Event {
    public final Room room;

    public DungeonRoomEnterEvent(Room room) {
        BadAddons.currentRoom = room;
        this.room = room;
    }

    @Override
    public void setCanceled(boolean cancel) {
        super.setCanceled(cancel);
    }

    public boolean post() {
        MinecraftForge.EVENT_BUS.post(this);
        return isCancelable() && isCanceled();
    }

}