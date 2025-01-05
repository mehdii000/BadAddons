package mehdi.bad.addons.Features.Kuudra;

import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Config.Configs;
import mehdi.bad.addons.Events.TickEndEvent;
import mehdi.bad.addons.utils.ChatLib;
import mehdi.bad.addons.utils.RealRenderUtils;
import mehdi.bad.addons.utils.SkyblockUtils;
import mehdi.bad.addons.utils.V2RenderUtils;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class AIPearlWaypoints {

    private long settedTimer = -1;
    private boolean timerExist = false;

    public static Vec3[] suppliesPlacing = {
            new Vec3(-94, 78.125, -106.0),
            new Vec3(-98, 78.125, -112.9375),
            new Vec3(-98, 78.125, -99.0625),
            new Vec3(-106, 78.125, -112.9375),
            new Vec3(-110, 78.125, -106.0),
            new Vec3(-106, 78.125, -99.0625)
    };

    public static Map<String, Vec3> preToPlacingMapping;
    static {
        preToPlacingMapping = new HashMap<>();
        preToPlacingMapping.put("Triangle", new Vec3(1, 2, 3));
        preToPlacingMapping.put("b", new Vec3(4, 5, 6));
        preToPlacingMapping.put("c", new Vec3(7, 8, 9));
    }

    public static Vec3 preferedBuildSplot = null;

    @SubscribeEvent
    public void onChatStuff(ClientChatReceivedEvent event) {
        if (!Configs.SetTimerCommand) return;
        if (event.type == 0) {
            String message = StringUtils.stripControlCodes(event.message.getUnformattedText());

            if (message.contains("@timer")) {
                String[] parts = message.split("@timer", 2);

                if (parts.length > 1 && !parts[1].trim().isEmpty()) {
                    try {
                        int number = Integer.parseInt(parts[1].trim());

                        if (number > 0) {
                            ChatLib.chat("§e[BA] §bStarted a " + number + "s timer.");
                            BadAddons.mc.thePlayer.sendChatMessage("[BA] Started a " + number + "s timer.");
                            settedTimer = System.currentTimeMillis() + (1000L * number);
                            timerExist = true;
                        } else {
                            ChatLib.chat("§e[BA] §cCan't set an empty or invalid timer!");
                        }
                    } catch (NumberFormatException e) {
                        ChatLib.chat("§e[BA] §cInvalid timer format!");
                    }
                } else {
                    ChatLib.chat("§e[BA] §cCan't set an empty timer!");
                }
            }
        }

    }

    @SubscribeEvent
    public void onTickEnd(TickEndEvent event) {
        if (!Configs.SetTimerCommand) return;
        if (System.currentTimeMillis() > settedTimer && timerExist) {
            BadAddons.mc.thePlayer.sendChatMessage("[BA] Timer Ended!");
            settedTimer = -1;
            timerExist = false;
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!SkyblockUtils.isInKuudra()) return;
        if (!Configs.BalistaProgress) return;
        if (KuudraHandler.suppliesPicked < 6) {
            ItemStack heldItem = BadAddons.mc.thePlayer.getHeldItem();
            if (heldItem == null) return;
            if (!heldItem.getDisplayName().contains("Ender Pearl") && !heldItem.getDisplayName().contains("Elle's Supplies")) return;

            for (Vec3 supply : suppliesPlacing) {
                if (isSupplyAvailable(supply.addVector(0, 1, 0))) {
                    V2RenderUtils.drawPixelBox(supply.addVector(0, 1, 0), Color.GREEN, 0.8, event.partialTicks);
                }
            }

        } else if (KuudraHandler.currentPhase == KuudraHandler.Phases.BUILD) {
            for (Vec3 supply : suppliesPlacing) {
                if (isSupplyDone(supply.addVector(0, 1, 0))) {
                    V2RenderUtils.drawPixelBox(supply.addVector(0, 1.4, 0), Color.GREEN, 0.8, event.partialTicks);
                } else {
                    V2RenderUtils.drawPixelBox(supply.addVector(0, 1.4, 0), Color.RED, 0.9, event.partialTicks);
                    RealRenderUtils.render3dString(getPileProgress(supply), supply.xCoord + 0.45, supply.yCoord + 2, supply.zCoord + 0.45, 1, 1.5f, event.partialTicks);
                }
            }

            if (Configs.BuildAssist && preferedBuildSplot != null) {
                V2RenderUtils.drawPixelBox(preferedBuildSplot.addVector(0, 2.5, 0), Color.BLUE, 0.5, event.partialTicks);
                RealRenderUtils.render3dString("§b§lBUILD THIS", preferedBuildSplot.xCoord + 0.45, preferedBuildSplot.yCoord + 5, preferedBuildSplot.zCoord + 0.45, 1, 2f, event.partialTicks);
                if (isSupplyDone(preferedBuildSplot)) {
                    nextSupply();
                }
            }

        }
    }

    private void nextSupply() {
        switch (PreSupplyDetection.currentPreSpot.getName()) {
            case "X":
                AIPearlWaypoints.preferedBuildSplot = AIPearlWaypoints.suppliesPlacing[3];
                break;
            case "Triangle":
                AIPearlWaypoints.preferedBuildSplot = AIPearlWaypoints.suppliesPlacing[0];
                break;
            case "Equals":
                AIPearlWaypoints.preferedBuildSplot = AIPearlWaypoints.suppliesPlacing[5];
                break;
            case "Slash":
                AIPearlWaypoints.preferedBuildSplot = AIPearlWaypoints.suppliesPlacing[4];
                break;
            default:
                break;
        }
    }

    private String getPileProgress(Vec3 supply) {
        AtomicReference<String> returnValue = new AtomicReference<String>("§40%");
        BadAddons.mc.thePlayer.worldObj.getEntitiesWithinAABB(EntityArmorStand.class,
                        new AxisAlignedBB(supply.xCoord - 2, supply.yCoord - 3, supply.zCoord + 2, supply.xCoord + 2, supply.yCoord + 3, supply.zCoord - 2))
                .forEach(stand -> {
                    String name = stand.getName().replaceAll("§.", "");
                    if (name.contains("PROGRESS")) {
                        returnValue.set("§c" + name.split("PROGRESS")[1]);
                    }
                });
        return returnValue.get();
    }

    private boolean isSupplyDone(Vec3 supply) {
        AtomicBoolean returnValue = new AtomicBoolean(false);
        BadAddons.mc.thePlayer.worldObj.getEntitiesWithinAABB(EntityArmorStand.class,
                        new AxisAlignedBB(supply.xCoord - 2, supply.yCoord - 3, supply.zCoord + 2, supply.xCoord + 2, supply.yCoord + 3, supply.zCoord - 2))
                .forEach(stand -> {
                    String name = stand.getName().replaceAll("§.", "");
                    if (name.contains("COMPLETE") && name.contains("PROGRESS")) {
                        returnValue.set(true);
                    } else {
                        stand.setInvisible(true);
                    }
                });
        return returnValue.get();
    }

    private boolean isSupplyAvailable(Vec3 supply) {
        AtomicBoolean returnValue = new AtomicBoolean(false);
        BadAddons.mc.thePlayer.worldObj.getEntitiesWithinAABB(EntityArmorStand.class,
                        new AxisAlignedBB(supply.xCoord - 2, supply.yCoord - 3, supply.zCoord + 2, supply.xCoord + 2, supply.yCoord + 3, supply.zCoord - 2))
                .forEach(stand -> {
                    String name = stand.getName().replaceAll("§.", "");
                    if (name.contains("BRING SUPPLY CHEST HERE")) {
                        returnValue.set(true);
                    }
                });
        return returnValue.get();
    }

    public int reverse(int x) {
        int len = (int) Math.log10(Math.abs(x)) + 1;
        for (int i = 1; i < len; i++) {
            int last = x % 10;
            x /= 10;
            x += last * len * 10;
        }
        return x;
    }

}
