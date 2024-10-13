package mehdi.bad.addons.Features.Kuudra;

import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Config.Configs;
import mehdi.bad.addons.Config.modules.MovableModule;
import mehdi.bad.addons.utils.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class PreSupplyDetection extends MovableModule {

    public PreSupplyDetection() {
        super("KuudraPre", 64, 64, 100, 50);
    }

    private static final PreSpot[] preSpots = {
            new PreSpot("Triangle", new Vec3(-67.5, 77.0, -122.5)),
            new PreSpot("X", new Vec3(-142.5, 77.0, -151.0)),
            new PreSpot("Equals", new Vec3(-65.5, 76.0, -87.5)),
            new PreSpot("Slash", new Vec3(-113.5, 77.0, -68.5)),

            new PreSpot("Shop", new Vec3(-81.0, 76.0, -143.0)),
            new PreSpot("XCannon", new Vec3(-143.0, 76.0, -125.0)),
            new PreSpot("Square", new Vec3(-143.0, 76.0, -80.0))
    };
    public static PreSpot currentPreSpot = null;
    private final String preRegex = "[NPC] Elle: Head over to the main platform, I will join you when I get a bite!";
    private final String spawnRegex = "[NPC] Elle: Not again!";
    private long startPreTime = -1;

    @SubscribeEvent
    public void onChatStuff(ClientChatReceivedEvent  event) {
        if (!SkyblockUtils.isInKuudra()) return;
        if (!Configs.KuudraPreDetection) return;
        if (event.type == 0) {
            String message = StringUtils.stripControlCodes(event.message.getUnformattedText());
            if (message.contains(preRegex) && Configs.KuudraPreDetection) {
                ChatLib.chat("§e[BA] §bKuudra fight begins.");
                startPreTime = System.currentTimeMillis();
                currentPreSpot = checkForPre(BadAddons.mc.thePlayer.getPosition());
            }

            if (message.contains(spawnRegex) && Configs.KuudraPreDetection) {
                ChatLib.chat("§e[BA] §bTook §c" + MathUtils.formatTicks(System.currentTimeMillis() - startPreTime) + "§b for supply to spawn!");
                checkAfterPreSpot();
            }
        }
    }

    public PreSpot checkForPre(BlockPos pos) {
        if (preSpots[0].getInitialPos().distanceTo(new Vec3(pos.getX(), pos.getY(), pos.getZ())) < 15) return preSpots[0];
        if (preSpots[1].getInitialPos().distanceTo(new Vec3(pos.getX(), pos.getY(), pos.getZ())) < 30) return preSpots[1];
        if (preSpots[2].getInitialPos().distanceTo(new Vec3(pos.getX(), pos.getY(), pos.getZ())) < 15) return preSpots[2];
        if (preSpots[3].getInitialPos().distanceTo(new Vec3(pos.getX(), pos.getY(), pos.getZ())) < 15) return preSpots[3];
        return PreSpot.NOSPOT;
    }

    public void checkAfterPreSpot() {
        List<EntityGiantZombie> gzs = BadAddons.mc.theWorld.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityGiantZombie)
                .map(entity -> (EntityGiantZombie) entity)
                .filter(gz -> gz.getHeldItem() != null && gz.getHeldItem().toString().equals("1xitem.skull@3"))
                .collect(Collectors.toList());
        gzs.forEach(supply -> {
            Vec3 supplyPos = new Vec3(supply.posX, 76, supply.posZ);
            if (currentPreSpot.getInitialPos().distanceTo(supplyPos) < 20) {
                BadAddons.mc.thePlayer.playSound("random.orb", 0.7f, 1f);
                ChatLib.chat("§e[BA] §bPre [" + currentPreSpot.getName() + "] didn't spawn!");
                BadAddons.mc.ingameGUI.displayTitle("§CNO-PRE", "", 0, 1000, 0);
            }
        });
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent e) {
        if (!SkyblockUtils.isInKuudra()) return;
        if (Configs.KuudraPreDetection && currentPreSpot != null) {
            Vec3 pos = currentPreSpot.getInitialPos();
            GuiUtils.drawCustomBoundingBoxAtBlock(new BlockPos(pos.xCoord, pos.yCoord, pos.zCoord), Color.WHITE, 12);
        }
    }

    @Override
    public void render() {
        if (!SkyblockUtils.isInKuudra()) return;
        if (!Configs.KuudraPreDetection) return;
        String txt = ":FISHING_ROD:  §7Pre: §b" + (currentPreSpot == null ? "None!" : currentPreSpot.getName());

        int textWidth = RenderUtils.getStringWidthWithItem(txt);
        int textHeight = BadAddons.mc.fontRendererObj.FONT_HEIGHT;

        float scaleX = (float) getWidth() / textWidth;
        float scaleY = (float) getHeight() / textHeight;
        float factor = Math.min(scaleX, scaleY) * 0.9f;

        int centeredX = getX() + (getWidth() - (int)(textWidth * factor)) / 2;
        int centeredY = getY() + (getHeight() - (int)(textHeight * factor)) / 2;

        // Apply scaling
        GlStateManager.scale(factor, factor, 1);
        RenderUtils.renderStringWithItems(txt, centeredX / factor, centeredY / factor, 1, true);
        GlStateManager.scale(1 / factor, 1 / factor, 1); // Revert scaling
    }

}
