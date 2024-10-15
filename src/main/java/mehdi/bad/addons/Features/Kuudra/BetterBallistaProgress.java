package mehdi.bad.addons.Features.Kuudra;

import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Config.Configs;
import mehdi.bad.addons.Config.modules.MovableModule;
import mehdi.bad.addons.utils.RenderUtils;
import mehdi.bad.addons.utils.SkyblockUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BetterBallistaProgress extends MovableModule {

    public BetterBallistaProgress() {
        super("BallistaProgress", 64, 128, 100, 50);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent e) {
        if (!SkyblockUtils.isInKuudra()) return;

    }

    @Override
    public void render() {
        if (!SkyblockUtils.isInKuudra()) return;
        if (!Configs.BetterBallistaBuilding) return;
        if (KuudraHandler.buildingPhase) {
            String txt = ":PLANKS:  §7Progress: §c";

            int textWidth = RenderUtils.getStringWidthWithItem(txt);
            int textHeight = BadAddons.mc.fontRendererObj.FONT_HEIGHT;

            float scaleX = (float) getWidth() / textWidth;
            float scaleY = (float) getHeight() / textHeight;
            float factor = Math.min(scaleX, scaleY);

            int centeredX = getX() + (getWidth() - (int)(textWidth * factor)) / 2;
            int centeredY = getY() + (getHeight() - (int)(textHeight * factor)) / 2;

            // Apply scaling
            GlStateManager.scale(factor, factor, 1);
            RenderUtils.renderStringWithItems(txt, centeredX / factor, centeredY / factor, 1, true);
            GlStateManager.scale(1 / factor, 1 / factor, 1); // Revert scaling

        }
    }

}
