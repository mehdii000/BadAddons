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
import java.util.concurrent.atomic.AtomicBoolean;
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
    public static long startPreTime = 0;

    @SubscribeEvent
    public void onChatStuff(ClientChatReceivedEvent  event) {
        if (!SkyblockUtils.isInKuudra()) return;
        if (event.type == 0) {
            String message = StringUtils.stripControlCodes(event.message.getUnformattedText());
            if (message.contains(preRegex)) {
                ChatLib.chat("§e[BA] §bKuudra fight begins.");
                KuudraHandler.currentPhase = KuudraHandler.Phases.SUPPLIES;
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
        AtomicBoolean noCloseSupplies = new AtomicBoolean(true);
        List<EntityGiantZombie> gzs = BadAddons.mc.theWorld.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityGiantZombie)
                .map(entity -> (EntityGiantZombie) entity)
                .filter(gz -> gz.getHeldItem() != null && gz.getHeldItem().toString().equals("1xitem.skull@3"))
                .collect(Collectors.toList());
        gzs.forEach(supply -> {
            Vec3 supplyPos = new Vec3(supply.posX, 76, supply.posZ);
            if (currentPreSpot.getInitialPos().distanceTo(supplyPos) <= 20) {
                noCloseSupplies.set(false); // Found a supply close to the prespot
            }
        });
        if (noCloseSupplies.get()) {
            BadAddons.mc.thePlayer.playSound("random.orb", 1f, 1f);
            ChatLib.chat("§e[BA] §4Pre [" + currentPreSpot.getName() + "] didn't spawn!");

            switch (currentPreSpot.getName()) {
                case "X":
                    ChatLib.chat("§e[BA] X -> §4X-CANON");
                    BadAddons.mc.ingameGUI.displayTitle("§cX-CANON", "", 0, 2500, 0);

                    break;
                case "Triangle":
                    ChatLib.chat("§e[BA] Tri -> §4SHOP");
                    BadAddons.mc.ingameGUI.displayTitle("§cSHOP", "", 0, 2500, 0);

                    break;
                case "Equals":
                    ChatLib.chat("§e[BA] Equals -> §4X-CANON");
                    BadAddons.mc.ingameGUI.displayTitle("§cSQUARE", "", 0, 2500, 0);
                    break;
                case "Slash":
                    ChatLib.chat("§e[BA] Slash -> §4SHOP");
                    BadAddons.mc.ingameGUI.displayTitle("§cSQUARE", "", 0, 2500, 0);
                    break;
                default:
                    ChatLib.chat("§e[BA] §aYou sure " + currentPreSpot.getName() + " is a pre? bruh...");
                    break;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent e) {
        if (!SkyblockUtils.isInKuudra()) return;
        if (BadAddons.mc.thePlayer.posY < 65) return;
        if (Configs.KuudraPreDetection && currentPreSpot != null && Configs.debugShowScanning) {
            Vec3 pos = currentPreSpot.getInitialPos();
            GuiUtils.drawCustomBoundingBoxAtBlock(new BlockPos(pos.xCoord - 6, pos.yCoord, pos.zCoord - 6), Color.WHITE, 2);
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
