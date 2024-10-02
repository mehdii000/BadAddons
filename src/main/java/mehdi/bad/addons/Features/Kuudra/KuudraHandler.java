package mehdi.bad.addons.Features.Kuudra;

import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Commands.Subcommands.CreatepearlSubcommand;
import mehdi.bad.addons.Config.Configs;
import mehdi.bad.addons.Config.modules.MovableModule;
import mehdi.bad.addons.Events.RenderEntityModelEvent;
import mehdi.bad.addons.Events.SlotClickEvent;
import mehdi.bad.addons.Events.TickEndEvent;
import mehdi.bad.addons.Objects.NotificationManager;
import mehdi.bad.addons.utils.*;
import net.minecraft.block.BlockNetherBrick;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector3f;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class KuudraHandler extends MovableModule {

    private Vector3f toStandOn;
    private Vector3f toBreak;
    private int clickToStun = 0;

    private String ccstun = "";
    private long timeSinceEaten = 0;
    private long timeOfStunning = 0;
    private long timeSinceFresh = 0;
    private List<String> teammates = new ArrayList<String>();
    private HashMap<String, Integer> pickSupplies = new HashMap<String, Integer>();

    private final String SUPPLY_REGEX = "(?:\\[\\w+\\]\\s)?(\\w+)\\srecovered one of Elle's supplies";

    public KuudraHandler() {
        super("KuudraSplits", 16, 16, 100, 200);
    }

    @SubscribeEvent
    public void onChatStuff(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();

        if (Configs.SupplyCount) {
            if (message.contains(" recovered one of Elle's supplies")) {
                Pattern pattern = Pattern.compile(SUPPLY_REGEX);
                Matcher matcher = pattern.matcher(message);
                String picker = matcher.group(1);
                NotificationManager.pushNotification("§e§l" + message.split(SUPPLY_REGEX)[0], "§aPicked-Up A Supply", 3000);
                if (teammates.contains(picker)) {
                    pickSupplies.put(picker, pickSupplies.getOrDefault(picker, 0) + 1);
                }
            }

            if (message.contains(" is now ready!") && Configs.SupplyCount) {
                if (!teammates.contains(message.split(" is now ready!")[0])) teammates.add(message.split(" is now ready!")[0]);
            }
            if (message.contains(" has been eaten by Kuudra!") && (!message.contains("Elle"))) {
                String stunner = message.split(" has been eaten by Kuudra!")[0];
                timeOfStunning = 0;
                clickToStun = 0;
                timeSinceEaten = System.currentTimeMillis();
                if (teammates.contains(stunner)) ccstun = stunner;
            }
        }

        if (message.contains("Your Fresh Tools Perk bonus doubles your building speed for the next 10 seconds!") && Configs.FreshDisplay) {
            timeSinceFresh = (System.currentTimeMillis() + 10000);
            BadAddons.mc.thePlayer.playSound("random.orb", 0.7f, 1f);
            switch (Configs.FreshDisplayType) {
                case 0:
                    BadAddons.mc.ingameGUI.displayTitle("§a" + Configs.FreshDisplayString.replace("&", "§"), "", 0, 2500, 0);
                    break;
                case 1:
                    NotificationManager.pushNotification("§a" + Configs.FreshDisplayString.replace("&", "§"), "", 2500);
                    break;
            }
        }

        if (message.contains(" destroyed one of Kuudra")) {
            BadAddons.mc.ingameGUI.displayTitle("§c§lATTACK", "", 0, 2500, 0);
            timeOfStunning = System.currentTimeMillis();
        }

    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        timeOfStunning = 0;
        timeSinceEaten = 0;
        if (!Configs.InstastunHelper) toStandOn = null; toBreak = null;
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
            new EspBox("§cSLASH", -105, 157, -100, Color.RED)
    };

    // Outer Esp Boxes with labels and coordinates
    EspBox[] EspBoxes = {
            new EspBox("§eTRIANGLE", -70.5, 79, -134.5, Color.YELLOW),
            new EspBox("§eTRIANGLE", -85.5, 78, -128.5, Color.YELLOW),
            new EspBox("§eTRIANGLE", -67.5, 77, -122.5, Color.YELLOW),
            new EspBox("§aSQUARE", -140.5, 78, -90.5, Color.GREEN),
            new EspBox("§dEQUAL", -65.5, 76, -87.5, Color.PINK),
            new EspBox("§cSLASH", -113.5, 77, -68.5, Color.RED)
    };

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent e) {

        if (CreatepearlSubcommand.lookAtPosition != null) {
            GuiUtils.drawBoundingBoxAtBlock(CreatepearlSubcommand.lookAtPosition, Color.YELLOW);
        }

        if (!SkyblockUtils.isInKuudra()) return;
        if (Configs.BalistaProgress) {
            for (Entity entity : BadAddons.mc.theWorld.loadedEntityList) {
                if (entity instanceof EntityArmorStand && entity.getDisplayName().getUnformattedText().contains("Building Progress ")) {
                    String progress = entity.getDisplayName().getUnformattedText().split("Building Progress ")[1].split("%")[0] + "%";
                    RealRenderUtils.render3dString("§e" + progress, entity.posX, entity.posY + (timeSinceFresh > 0 ? 3 : 1), entity.posZ, 0x00FF00, 22, e.partialTicks);

                    if (Configs.FreshDisplay && timeSinceFresh > 0) {
                        RealRenderUtils.render3dString("§c" + MathUtils.formatTicks(timeSinceFresh - System.currentTimeMillis()), entity.posX, entity.posY + 1, entity.posZ, 0x00FF00, 15, e.partialTicks);
                    }

                }
            }
        }

        if (Configs.KuudraPresHighlight || Configs.KuudraWaypointsProximity) {
            Vec3 playerPos = BadAddons.mc.thePlayer.getPositionVector();
            Set<Integer> nearbyColors = new HashSet<>();  // Store the colors of nearby EspBoxes

            // If proximity is enabled, check proximity for outer EspBoxes and store nearby colors
            if (Configs.KuudraWaypointsProximity) {
                for (EspBox box : EspBoxes) {
                    // Check if the EspBox is within proximity (4 block radius)
                    if (playerPos.squareDistanceTo(new Vec3(box.x + 0.5, box.y + 0.5, box.z + 0.5)) <= 24) {  // 4^2 = 16
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

            // Render EspBoxes as normal
            for (EspBox box : EspBoxes) {
                if (Configs.KuudraPresHighlightType == 0) {
                    GuiUtils.drawBoundingBoxAtBlock(new BlockPos(box.x, box.y - 1, box.z), Configs.KuudraPresNames ? Color.WHITE : box.color);
                    if (Configs.KuudraPresNames) RealRenderUtils.render3dString(box.label, box.x, box.y+0.25, box.z, 0x00FF00, 2f, e.partialTicks);
                } else {
                    RealRenderUtils.render3dString(box.label, box.x, box.y+0.25, box.z, 0x00FF00, 3f, e.partialTicks);
                }
            }
        }

        if (Configs.InstastunHelper) {
            if (toStandOn != null && toBreak != null) {
                GuiUtils.drawBoundingBoxAtBlock(new BlockPos(toStandOn.x, toStandOn.y, toStandOn.z), Color.CYAN);
                GuiUtils.drawBoundingBoxAtBlock(new BlockPos(toBreak.x, toBreak.y, toBreak.z), Color.YELLOW);
                RealRenderUtils.render3dString("§6" + clickToStun, toBreak.getX() + 0.5, toBreak.getY() + 1, toBreak.getZ() + 0.5, -1, 5, e.partialTicks);
            }
        }

        if (Configs.KuudraSuppliesWaypoints) {
            processCrates(BadAddons.mc.theWorld, e.partialTicks);
        }

    }

    @SubscribeEvent
    public void onTickEnd(TickEndEvent e) {
        if (System.currentTimeMillis() > timeSinceFresh) timeSinceFresh = -1;
        if (!SkyblockUtils.isInKuudra()) return;
        if (Configs.HideBlindness && BadAddons.mc.thePlayer.isPotionActive(Potion.blindness.getId())) {
            BadAddons.mc.thePlayer.removePotionEffect(Potion.blindness.getId());
        }
    }

    // Method to get all entities of a specific type from a chunk
    private <T extends Entity> List<T> getEntitiesInChunk(World world, int chunkX, int chunkZ, Class<T> entityClass) {
        List<T> entities = new ArrayList<>();
        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

        for (ClassInheritanceMultiMap<Entity> entityMultiMap : chunk.getEntityLists()) {
            for (Entity entity : entityMultiMap) {
                if (entityClass.isInstance(entity)) {
                    entities.add(entityClass.cast(entity));
                }
            }
        }
        return entities;
    }

    private List<EntityArmorStand> get3x3Stands(int x, int z, int diff, World world) {
        List<EntityArmorStand> stands = new ArrayList<>();

        stands.addAll(getEntitiesInChunk(world, x, z, EntityArmorStand.class));
        stands.addAll(getEntitiesInChunk(world, x + diff, z + diff, EntityArmorStand.class));
        stands.addAll(getEntitiesInChunk(world, x + diff, z - diff, EntityArmorStand.class));
        stands.addAll(getEntitiesInChunk(world, x - diff, z + diff, EntityArmorStand.class));
        stands.addAll(getEntitiesInChunk(world, x - diff, z - diff, EntityArmorStand.class));
        stands.addAll(getEntitiesInChunk(world, x + diff, z, EntityArmorStand.class));
        stands.addAll(getEntitiesInChunk(world, x - diff, z, EntityArmorStand.class));
        stands.addAll(getEntitiesInChunk(world, x, z + diff, EntityArmorStand.class));
        stands.addAll(getEntitiesInChunk(world, x, z - diff, EntityArmorStand.class));

        return stands;
    }

    // Main method to process crates
    public void processCrates(World world, float partialTicks) {
        if (!Configs.KuudraSuppliesWaypoints) {
            return;
        }

        List<EntityGiantZombie> gzs = world.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityGiantZombie)
                .map(entity -> (EntityGiantZombie) entity)
                .filter(gz -> gz.posY < 67)
                .collect(Collectors.toList());

        List<double[]> crates = new ArrayList<>();

        for (EntityGiantZombie supply : gzs) {
            List<EntityArmorStand> stands = getEntitiesInChunk(world, supply.chunkCoordX, supply.chunkCoordZ, EntityArmorStand.class);

            List<EntityArmorStand> active = stands.stream()
                    .filter(stand -> stand.getCustomNameTag().contains("SUPPLIES") || stand.getCustomNameTag().contains("FUEL CELL"))
                    .collect(Collectors.toList());

            if (!active.isEmpty()) {
                active.forEach(crate -> crates.add(new double[]{crate.posX, crate.posY, crate.posZ}));
            } else {
                double x = Math.round(supply.posX);
                double z = Math.round(supply.posZ);

                List<EntityArmorStand> nearbyStands = get3x3Stands((int) x, (int) z, 8, world);

                List<EntityArmorStand> filteredNearbyStands = nearbyStands.stream()
                        .filter(stand -> stand.getCustomNameTag().contains("SUPPLIES") || stand.getCustomNameTag().contains("FUEL CELL"))
                        .collect(Collectors.toList());

                if (!filteredNearbyStands.isEmpty()) {
                    filteredNearbyStands.forEach(crate -> crates.add(new double[]{crate.posX, crate.posY, crate.posZ}));
                } else {
                    crates.add(new double[]{supply.posX + 1, supply.posY, supply.posZ + 1});
                }
            }
        }

        crates.forEach(crate -> {
            //RealRenderUtils.renderWaypointText("§4§lSUPPLY", crate[0], 60, crate[2], partialTicks);
            RealRenderUtils.renderBeaconBeam(new BlockPos(crate[0], 70, crate[2]), 0x1fd8f1, 1.0F, partialTicks, false);
        });

    }

    @SubscribeEvent
    public void onLeftClick(MouseEvent e) {
        if (!SkyblockUtils.isInKuudra()) return;
        if (!Configs.InstastunHelper) return;
        if (BadAddons.mc.inGameHasFocus && SkyblockUtils.isInSkyblock()) {
            if (e.button == 0 && e.buttonstate) {
                MovingObjectPosition target = Minecraft.getMinecraft().objectMouseOver;
                if (target != null && target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    IBlockState blockState = Minecraft.getMinecraft().theWorld.getBlockState(target.getBlockPos());
                    if (blockState.getBlock() instanceof BlockNetherBrick) {
                        clickToStun++;
                        toBreak = new Vector3f(target.getBlockPos().getX(), target.getBlockPos().getY(), target.getBlockPos().getZ());
                        toStandOn = new Vector3f(BadAddons.mc.thePlayer.getPosition().getX(), BadAddons.mc.thePlayer.getPosition().getY()-1, BadAddons.mc.thePlayer.getPosition().getZ());
                    }
                }
            }
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
                ChatLib.chat("§e[BA] Clicking " + chest.getSlot(13).getStack().toString());
                e.setCanceled(true);
                GuiUtils.sendMouseClick(chest.windowId, 13);
            }

        }
    }

    private void drawColoredRect(int x, int y, Color color) {
        Gui.drawRect(x, y, x + 16, y + 16, color.getRGB());
    }

    private void highlightFillEntity(Color color, RenderEntityModelEvent e) {
        OutlineUtils.drawFillEntity(e, color);
    }
    private void highlightEntity(Color color, RenderEntityModelEvent e) {
        OutlineUtils.outlineEntity(e, color, 3);
    }


    private boolean isWithin(double x, double z, double min, double max) {
        return x >= min && x <= max && z >= min && z <= max;
    }

    @Override
    public void render() {

        if (!SkyblockUtils.isInSkyblock()) return;
        if (SkyblockUtils.currentMap.contains("Kuudra") && SkyblockUtils.currentMap.contains("Hollow")) {
            BadAddons.mc.fontRendererObj.drawStringWithShadow("§dKuudra Gaming", getX(), getY(), -1);
            for (int i = 0; i < teammates.size(); i++) {
                if (teammates.get(i) != null) {
                    int supplies = (pickSupplies.get(teammates.get(i)) == null ? 0 : pickSupplies.get(teammates.get(i)));
                    double stunTime = timeOfStunning == 0 ? (timeSinceEaten == 0 ? 0 : (double)(Math.round((System.currentTimeMillis() - timeSinceEaten) / 10)) / 100) : (timeSinceEaten == 0 ? 0 : (double)(Math.round((timeOfStunning - timeSinceEaten) / 10)) / 100);

                    RenderUtils.renderStringWithItems((Objects.equals(teammates.get(i), ccstun)
                            ? "§a[STUN] §r" + teammates.get(i) + " §e" + supplies + "§7/6 :IRON_PICKAXE: §b" + stunTime + "s"
                            : "§e[DPS] §r" + teammates.get(i) + " §e" + supplies + "§7/6 :CHEST:"
                    ), getX(), getY() + 16 + (14*i), -1, true);
                }
            }

        } else {
            timeSinceEaten = 0;
            timeOfStunning = 0;
            if (!teammates.isEmpty()) {
                teammates.clear();
                pickSupplies.clear();
            }
        }

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

}


