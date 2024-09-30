package mehdi.bad.addons.Config.modules;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MovableListener {

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent e) {
        if (e.type != RenderGameOverlayEvent.ElementType.HELMET) return;
        MovablesManager.renderModules();
    }

}
