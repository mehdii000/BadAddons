package mehdi.bad.addons.Features.Kuudra;

import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Config.Configs;
import mehdi.bad.addons.Config.modules.MovableModule;
import mehdi.bad.addons.Events.RenderEntityModelEvent;
import mehdi.bad.addons.Events.SlotClickEvent;
import mehdi.bad.addons.Events.TickEndEvent;
import mehdi.bad.addons.utils.*;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class KuudraHandler extends MovableModule {

    private String ccstun = "";
    private long timeSinceEaten = 0;
    private long timeOfStunning = 0;
    private long timeSinceFresh = 0;
    private long timeSinceLastPhaseStarted = 0;

    private List<String> teammates = new ArrayList<String>();
    private HashMap<String, Integer> pickSupplies = new HashMap<String, Integer>();
    public static int suppliesPicked = 0;

    public static Phases currentPhase = Phases.NONE;

    public List<String> freshers = new ArrayList<String>();
    public static long freshTimeFromBuildStart = 0;

    private float trackedDps = 0;
    private boolean lastBreathed = false;

    private final String SUPPLY_REGEX = "(?:\\[\\w+\\]\\s)?(\\w+)\\srecovered one of Elle's supplies";

    public KuudraHandler() {
        super("KuudraSplits", 16, 16, 100, 200);
    }

    @SubscribeEvent
    public void onChatStuff(ClientChatReceivedEvent event) {
        if (event.type != 0) return;
        if (!SkyblockUtils.isInKuudra()) return;
        String message = StringUtils.stripControlCodes(event.message.getUnformattedText());

        if (message.contains("recovered one of Elle's supplies! ") && Configs.SupplyCount) {
            Pattern pattern = Pattern.compile(SUPPLY_REGEX);
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                String picker = matcher.group(1);
                BadAddons.mc.ingameGUI.displayTitle("§a" + picker, "§aSupplied", 0, 2500, 0);
                if (this.teammates.contains(picker)) {
                    this.pickSupplies.put(picker, Integer.valueOf(this.pickSupplies.getOrDefault(picker, Integer.valueOf(0)).intValue() + 1));
                    suppliesPicked++;
                }
                if (Configs.BetterPickupSupplyMessage) {
                    event.setCanceled(true);
                    ChatLib.chat("§a" + picker + " got supply after §c" + MathUtils.detailedFormatTicks(System.currentTimeMillis()-PreSupplyDetection.startPreTime));
                }
            }
        }

        if (message.contains(" is now ready!") && Configs.SupplyCount) {
            if (!teammates.contains(message.split(" is now ready!")[0])) teammates.add(message.split(" is now ready!")[0]);
        }

        if (message.contains(" has been eaten by Kuudra!") && (!message.contains("Elle"))) {
            String stunner = message.split(" has been eaten by Kuudra!")[0];
            timeOfStunning = 0;
            timeSinceEaten = System.currentTimeMillis();
            if (teammates.contains(stunner)) ccstun = stunner;
        }

        // Handle Fresh Tools perk
        if (message.contains("Your Fresh Tools Perk bonus doubles your building speed for the next 10 seconds!") && Configs.FreshDisplay) {
            timeSinceFresh = System.currentTimeMillis() + 10000;
            BadAddons.mc.thePlayer.playSound("random.orb", 0.7f, 1f);
            String freshDisplayMessage = "§a" + Configs.FreshDisplayString.replace("&", "§");
            BadAddons.mc.ingameGUI.displayTitle(freshDisplayMessage, "", 0, 2500, 0);
            if (Configs.FreshChatMessage) BadAddons.mc.thePlayer.sendChatMessage("FRESHED! After " + MathUtils.detailedFormatTicks(System.currentTimeMillis() - freshTimeFromBuildStart));
        }

        if (Configs.FreshDisplay && KuudraUtils.matchesFreshRegex(message)) {
            freshers.add(KuudraUtils.extractName(message));
        }

        if (message.contains("Elle: Damaging Kuudra directly is futile. His thick skin is impenetrable with conventional weapons!")) {
            lastBreathed = true;
        }

        if (message.contains("Elle: OMG! Great work collecting my supplies!")) {
            ChatLib.chat("§e[BA] §bBuild-Phase started!");
            currentPhase = Phases.BUILD;
            suppliesPicked = 6;
            freshTimeFromBuildStart = System.currentTimeMillis();

            switch (PreSupplyDetection.currentPreSpot.getName()) {
                case "X":
                    AIPearlWaypoints.preferedBuildSplot = AIPearlWaypoints.suppliesPlacing[3];
                    break;
                case "Triangle":
                    AIPearlWaypoints.preferedBuildSplot = AIPearlWaypoints.suppliesPlacing[1];
                    break;
                case "Equals":
                    AIPearlWaypoints.preferedBuildSplot = AIPearlWaypoints.suppliesPlacing[2];
                    break;
                case "Slash":
                    AIPearlWaypoints.preferedBuildSplot = AIPearlWaypoints.suppliesPlacing[5];
                    break;
                default:
                    ChatLib.chat("§e[BA] You dont have a pre registerd yet!");
                    break;
            }

        }
        if (message.contains("The Ballista is finally ready!") && message.contains("Elle")) {
            ChatLib.chat("§e[BA] §bBuild-Phase ended!");
            currentPhase = Phases.STUN;
            freshTimeFromBuildStart = 0;
        }

        if (message.contains(" destroyed one of Kuudra")) {
            BadAddons.mc.ingameGUI.displayTitle("§c§lATTACK", "§cBadAddons On Top", 0, 2500, 0);
            timeOfStunning = System.currentTimeMillis();
        }

        if (message.contains("KUUDRA DOWN")) {
            currentPhase = Phases.DONE;
        }

    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        resetKuudraHandler();
    }

    EspBox[] InnerEspBoxes = {
            new EspBox("§eTRIANGLE", -97, 157, -112, Color.YELLOW),
            new EspBox("§eTRIANGLE", -95.5, 161, -105.5, Color.YELLOW),
            new EspBox("§aSQUARE", -110, 155, -106, Color.GREEN),
            new EspBox("§aSQUARE", -43.5, 120, -149.5, Color.GREEN),
            new EspBox("§aSQUARE", -45.5, 135, -138.5, Color.GREEN),
            new EspBox("§aSQUARE", -35.5, 138, -124.5, Color.GREEN),
            new EspBox("§aSQUARE", -26.5, 126, -111.5, Color.GREEN),
            new EspBox("§dEQUAL", -106, 165, -101, Color.PINK),
            new EspBox("§cSLASH", -105, 157, -100, Color.RED),
            new EspBox("§5X", -102, 160, -110, Color.MAGENTA),
            new EspBox("§5X-Canon", -112, 155, -107, Color.MAGENTA)
    };

    EspBox[] DoublePearlLineups = {
            new EspBox("§eTRI §7-> §aSL", -81, 160, -128, Color.YELLOW),
            new EspBox("§eTRI §7-> §aSR", -72, 160, -134, Color.YELLOW),

            new EspBox("§5X §7-> §5CANON", -129, 162, -113, Color.MAGENTA),
            new EspBox("§5X §7-> §aSQUARE", -141, 151, -83, Color.MAGENTA),

            new EspBox("§dEQUAL §7-> §aSQUARE", -76, 150, -137, Color.PINK),

            new EspBox("§cSLASH §7-> §5CANON", -136, 155, -133, Color.RED),
            new EspBox("§cSLASH §7-> §aSQUARE", -140, 152, -92, Color.RED)
    };

    // Outer Esp Boxes with labels and coordinates
    EspBox[] EspBoxes = {
            new EspBox("§eTRIANGLE", -70.5, 79, -134.5, Color.YELLOW),
            new EspBox("§eTRIANGLE", -85.5, 78, -128.5, Color.YELLOW),
            new EspBox("§eTRIANGLE", -67.5, 77, -122.5, Color.YELLOW),
            new EspBox("§aSQUARE", -140.5, 78, -90.5, Color.GREEN),
            new EspBox("§dEQUAL", -65.5, 76, -87.5, Color.PINK),
            new EspBox("§cSLASH", -113.5, 77, -68.5, Color.RED),
            new EspBox("§5X", -134.5, 77, -138.5, Color.MAGENTA),
            new EspBox("§5X-Canon", -130.5, 79, -113.5, Color.MAGENTA)
    };

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent e) {

        if (!SkyblockUtils.isInKuudra()) return;
        if (Configs.BalistaProgress && currentPhase == Phases.BUILD) {
            for (Entity entity : BadAddons.mc.theWorld.loadedEntityList) {
                if (entity instanceof EntityArmorStand && entity.getDisplayName().getUnformattedText().contains("Building Progress ")) {
                    String progress = entity.getDisplayName().getUnformattedText().split("Building Progress ")[1].split("%")[0] + "%";
                    RealRenderUtils.render3dString("§e" + progress, entity.posX, entity.posY + (timeSinceFresh > 0 ? 3 : 1), entity.posZ, 0x00FF00, 21, e.partialTicks);
                    if (Configs.FreshDisplay && timeSinceFresh > 0) {
                        RealRenderUtils.render3dString("§c" + MathUtils.formatTicks(timeSinceFresh - System.currentTimeMillis()), entity.posX, entity.posY, entity.posZ, 0x00FF00, 15, e.partialTicks);
                    }
                }
            }
        }

        if (Configs.BoxKuudra && currentPhase != Phases.NONE) {

            List<EntityMagmaCube> cubs = BadAddons.mc.theWorld.loadedEntityList.stream()
                    .filter(entity -> entity instanceof EntityMagmaCube)
                    .map(entity -> (EntityMagmaCube) entity)
                    .filter(cube -> cube.getSlimeSize() >= 25)
                    .collect(Collectors.toList());

            if (currentPhase == Phases.LAST) {
                for (EntityMagmaCube magmaCube : cubs) {
                    V2RenderUtils.drawPixelBox(magmaCube.getPositionVector().addVector(-7, 0, -7), Color.GREEN, 14, e.partialTicks);
                    RealRenderUtils.render3dString("§e"+ KuudraUtils.getHP() + "M§7/§a" + KuudraUtils.getLastPhaseMaxtHP() + "M§c❤", magmaCube.posX, magmaCube.posY + 7, magmaCube.posZ, 1, 4 * Configs.BoxKuudraSize, e.partialTicks);
                }
            } else {
                for (EntityMagmaCube magmaCube : cubs) {
                    if (magmaCube.posY < 30 || magmaCube.posY > 71) {
                        V2RenderUtils.drawPixelBox(magmaCube.getPositionVector().addVector(-7, 0, -7), Color.GREEN, 14, e.partialTicks);
                        RealRenderUtils.render3dString("§a" + KuudraUtils.getHitsHP() + "% §7Hits", magmaCube.posX, magmaCube.posY + 7, magmaCube.posZ, 1, 4 * Configs.BoxKuudraSize, e.partialTicks);
                    }
                }
            }

        }

        if ((Configs.KuudraPresHighlight || Configs.KuudraWaypointsProximity) && (currentPhase == Phases.SUPPLIES || currentPhase == Phases.NONE)) {
            Vec3 playerPos = BadAddons.mc.thePlayer.getPositionVector();
            Set<Integer> nearbyColors = new HashSet<>();  // Store the colors of nearby EspBoxes

            // If proximity is enabled, check proximity for outer EspBoxes and store nearby colors
            if (Configs.KuudraWaypointsProximity) {
                for (EspBox box : EspBoxes) {
                    // Check if the EspBox is within proximity (4 block radius)
                    if (playerPos.squareDistanceTo(new Vec3(box.x + 0.5, box.y + 0.5, box.z + 0.5)) <= 30) {  // 4^2 = 16
                        nearbyColors.add(box.color.getRGB());  // Add the color of the nearby EspBox
                    }
                }
            }

            // Draw InnerEspBoxes based on the proximity check
            for (EspBox innerBox : InnerEspBoxes) {
                // If proximity is disabled, draw all InnerEspBoxes
                // If proximity is enabled, draw only InnerEspBoxes with matching colors of nearby EspBoxes
                if (!Configs.KuudraWaypointsProximity || nearbyColors.contains(innerBox.color.getRGB())) {
                    GuiUtils.drawBoundingBoxAtBlock(new BlockPos(innerBox.x, innerBox.y, innerBox.z), innerBox.color);
                }
            }
            if (Configs.DoublePearlLineups) {
                for (EspBox doublePearlLineup : DoublePearlLineups) {
                    if (!Configs.KuudraWaypointsProximity || nearbyColors.contains(doublePearlLineup.color.getRGB())) {
                        GuiUtils.drawBoundingBoxAtBlock(new BlockPos(doublePearlLineup.x, doublePearlLineup.y, doublePearlLineup.z), Color.getHSBColor(0.3889f, 0.6f, 1.0f));
                        RealRenderUtils.render3dString(doublePearlLineup.label, doublePearlLineup.x, doublePearlLineup.y - 2, doublePearlLineup.z, 1, 3, e.partialTicks);
                    }
                }
            }

            // Render EspBoxes as normal
            for (EspBox box : EspBoxes) {
                if (Configs.KuudraPresHighlightType == 0) {
                    BlockPos blk = new BlockPos(box.x, box.y - 1, box.z);
                    GuiUtils.drawBoundingBoxAtBlock(blk, box.color);
                    if (Configs.KuudraPresNames) RealRenderUtils.render3dString(box.label, box.x, box.y+0.25, box.z, 0x00FF00, 2f, e.partialTicks);
                } else {
                    RealRenderUtils.render3dString(box.label, box.x, box.y+0.25, box.z, 0x00FF00, 1.5f, e.partialTicks);
                }
            }
        }

        if (Configs.KuudraSuppliesWaypoints && suppliesPicked < 6) {
            processCrates(BadAddons.mc.theWorld, e.partialTicks);
        }

    }

    @SubscribeEvent
    public void onTickEnd(TickEndEvent e) {
        if (System.currentTimeMillis() > timeSinceFresh) timeSinceFresh = -1;
        if (!SkyblockUtils.isInKuudra()) return;

        if (BadAddons.mc.thePlayer.posY < 20 && currentPhase == Phases.STUN) {
            ChatLib.chat("§e[BA] §bLast phase started!");
            trackedDps = 0;
            timeSinceLastPhaseStarted = System.currentTimeMillis();
            currentPhase = Phases.LAST;
        }

        if (currentPhase == Phases.LAST) {
            float currentHP = KuudraUtils.getHP();
            long dt = System.currentTimeMillis() - timeSinceLastPhaseStarted;

            if (dt > 0) {
                float damageDealt = KuudraUtils.getLastPhaseMaxtHP() - currentHP;
                trackedDps = (float) Math.round(10 * (damageDealt / (dt / 1000.0f))) / 10;
            } else {
                trackedDps = KuudraUtils.getLastPhaseMaxtHP();
            }
        }

    }

    // Main method to process crates
    public void processCrates(World world, float partialTicks) {
        if (!Configs.KuudraSuppliesWaypoints) {
            return;
        }
        if (world == null || world.loadedEntityList == null) {
            return;
        }
        List<EntityGiantZombie> gzs = world.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityGiantZombie)
                .map(entity -> (EntityGiantZombie) entity)
                .filter(gz -> gz.getHeldItem() != null && gz.getHeldItem().toString().equals("1xitem.skull@3"))
                .collect(Collectors.toList());

        for (EntityGiantZombie supply : gzs) {

            float yaw = supply.getRotationYawHead();
            double x = supply.posX + (3.7 * Math.cos((yaw + 130) * (Math.PI / 180)));
            double z = supply.posZ + (3.7 * Math.sin((yaw + 130) * (Math.PI / 180)));

            /*GuiUtils.drawSmallBoundingBoxAtBlock(new BlockPos(x, 75, z), Color.YELLOW);
            RealRenderUtils.render3dString("§4CRATE", x, 74.5, z, 1, 1.25f, partialTicks);
            if (Configs.SuppliesWaypointsBeacon) RealRenderUtils.renderBeaconBeamFloat(x, 72, z, 0xcbed4e, 0.8f, partialTicks, true);*/

            V2RenderUtils.renderCustomBeacon("Supply", new Vec3(x, 74.5, z), Color.YELLOW, Configs.SuppliesWaypointsBeacon, partialTicks);

        }

    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderEntity(RenderEntityModelEvent e) {
        if (!SkyblockUtils.isInKuudra()) return;
        if (Configs.TentacleHider) {
            if (e.entity instanceof EntityMagmaCube) {
                if (e.entity.posY < 15) {
                    boolean result = isWithin(e.entity.posX, e.entity.posZ, -115, -85);
                    e.entity.setInvisible(result);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onInventoryRendering(GuiScreenEvent.DrawScreenEvent.Post post) {
        if (!SkyblockUtils.isInKuudra()) return;
        if (!Configs.HidePerkUpgrades) return;
        if (post.gui instanceof GuiChest) {
            ContainerChest chest = (ContainerChest) ((GuiChest) post.gui).inventorySlots;
            if (chest != null) {
                String title = StringUtils.stripControlCodes(chest.getLowerChestInventory().getName());
                if (title.contains("Perk Menu") && GuiUtils.isSlotContainsItem(chest, 5, 2, Item.getItemFromBlock(Blocks.crafting_table))) {
                    GuiUtils.fillSlots(chest, 4, 2, 6, 3, chest.getSlot(13).getStack());
                }
            }
        }

    }

    @SubscribeEvent
    public void onMouseClick(SlotClickEvent e) {
        if (!SkyblockUtils.isInKuudra()) return;
        if (!Configs.HidePerkUpgrades) return;
        if (e.guiContainer.inventorySlots instanceof ContainerChest) {
            ContainerChest chest = (ContainerChest) e.guiContainer.inventorySlots;
            if (chest.getLowerChestInventory().getName().contains("Perk Menu") && GuiUtils.isSlotContainsItem(chest, 5, 2, Item.getItemFromBlock(Blocks.crafting_table)) && GuiUtils.isSlotWithin(e.slot, chest, 4, 2, 6, 3)) {
                if (Configs.debugShowScanning) ChatLib.chat("§e[BA] Clicking " + chest.getSlot(13).getStack().toString());
                e.setCanceled(true);
                GuiUtils.sendMouseClick(chest.windowId, 13);
            }

        }
    }

    private boolean isWithin(double x, double z, double min, double max) {
        return x >= min && x <= max && z >= min && z <= max;
    }

    @Override
    public void render() {
        if (!SkyblockUtils.isInSkyblock()) return;
        if (SkyblockUtils.isInKuudra()) {
            BadAddons.mc.fontRendererObj.drawStringWithShadow("§dKuudra Gaming  §7(§e" + suppliesPicked + "§7/6) §7" + (Configs.debugShowScanning ? currentPhase.name() + " / " + KuudraUtils.getHitsHP() : currentPhase), getX(), getY(), -1);
            int totalLines = teammates != null ? teammates.size() : 0;

            for (int i = 0; i < totalLines; i++) {
                if (teammates.get(i) != null) {
                    int supplies = pickSupplies.getOrDefault(teammates.get(i), 0);
                    double stunTime = 0;

                    if (timeSinceEaten != 0) {
                        if (timeOfStunning == 0) {
                            stunTime = Math.round((System.currentTimeMillis() - timeSinceEaten) / 10.0) / 100.0;
                        } else {
                            stunTime = Math.round((timeOfStunning - timeSinceEaten) / 10.0) / 100.0;
                        }
                    }

                    RenderUtils.renderStringWithItems(
                            (ccstun != null && ccstun.equals(teammates.get(i))
                                    ? ":IRON_PICKAXE: §a[STUN] §r" + teammates.get(i) + " §e" + supplies + "§7 / §b" + stunTime + "s"
                                    : ":CHEST: §e[DPS] §r" + teammates.get(i) + " §e" + supplies),
                            getX(),
                            getY() + 16 + (15 * i),
                            -1,
                            true
                    );
                }
            }

            if (Configs.FreshDisplay && freshers != null && !freshers.isEmpty()) {
                totalLines++;
                RenderUtils.renderStringWithItems(":WATER_BUCKET: §aFRESHES:", getX(), getY() + 16 + (15 * totalLines), -1, true);
                totalLines++;
                for (int i = 0; i < freshers.size(); i++) {
                    RenderUtils.renderStringWithItems(" §7- §7" +freshers.get(i), getX(), getY() + 16 + (15 * (totalLines + i)), -1, true);
                }
                totalLines += freshers.size();
            }

            if (Configs.PartyDpsTracker) {
                totalLines++;
                if (currentPhase == Phases.LAST || currentPhase == Phases.DONE) {
                    RenderUtils.renderStringWithItems(":BOW: §7Last §7Breathed: " + isLastBreathed(), getX(), getY() + 16 + (15 * totalLines), -1, true);
                    totalLines++;
                    RenderUtils.renderStringWithItems(":IRON_SWORD: §7Party §7Dps: §c" + (trackedDps == 0 ? "NaN" : "§e" + trackedDps + "m"), getX(), getY() + 16 + (15 * totalLines), -1, true);

                }
                if (currentPhase == Phases.SUPPLIES || currentPhase == Phases.BUILD) RenderUtils.renderStringWithItems(":BOW: §7Last §7Breathed: " + isLastBreathed(), getX(), getY() + 16 + (15 * totalLines), -1, true);
            }

        }
    }

    private String isLastBreathed() {
        if (lastBreathed) return "§eLikely";
        return "§cNope";
    }

    private void resetKuudraHandler() {
        timeSinceEaten = 0;
        timeOfStunning = 0;
        timeSinceLastPhaseStarted = 0;
        teammates.clear();
        pickSupplies.clear();
        freshers.clear();
        suppliesPicked = 0;
        trackedDps = 0;
        lastBreathed = false;
        AIPearlWaypoints.preferedBuildSplot = null;
        currentPhase = Phases.NONE;
        PreSupplyDetection.startPreTime = 0;
        freshTimeFromBuildStart = 0;
    }

    public class EspBox {
        public String label;
        public double x, y, z;
        public Color color;

        public EspBox(String label, double x, double y, double z, Color color) {
            this.label = label;
            this.x = x;
            this.y = y;
            this.z = z;
            this.color = color;
        }
    }

    public enum Phases {
        NONE,
        SUPPLIES,
        BUILD,
        STUN,
        LAST,
        DONE
    }

}


