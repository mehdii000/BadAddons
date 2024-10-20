package mehdi.bad.addons;

import mehdi.bad.addons.Commands.BadCommand;
import mehdi.bad.addons.Commands.CommandDevItemData;
import mehdi.bad.addons.Commands.Subcommands.*;
import mehdi.bad.addons.Config.Config;
import mehdi.bad.addons.Config.Configs;
import mehdi.bad.addons.Config.modules.MovableConfigManager;
import mehdi.bad.addons.Config.modules.MovableListener;
import mehdi.bad.addons.Config.modules.MovablesManager;
import mehdi.bad.addons.Events.PacketEvent;
import mehdi.bad.addons.Events.TickEndEvent;
import mehdi.bad.addons.Features.Dungeons.DungeonRooms;
import mehdi.bad.addons.Features.Dungeons.OnItemPickedUp;
import mehdi.bad.addons.Features.Dungeons.OnPlayerTick;
import mehdi.bad.addons.Features.Dungeons.OnWorldRender;
import mehdi.bad.addons.Features.Dungeons.catacombs.DungeonManager;
import mehdi.bad.addons.Features.Dungeons.catacombs.RoomDetection;
import mehdi.bad.addons.Features.Dungeons.utils.PacketHandler;
import mehdi.bad.addons.Features.Dungeons.utils.Room;
import mehdi.bad.addons.Features.General.*;
import mehdi.bad.addons.Features.Kuudra.AIPearlWaypoints;
import mehdi.bad.addons.Features.Lowballing.TradesTracker;
import mehdi.bad.addons.Objects.NotificationManager;
import mehdi.bad.addons.Objects.ScoreBoard;
import mehdi.bad.addons.Objects.UpdateCheck;
import mehdi.bad.addons.utils.AuctionUtils;
import mehdi.bad.addons.utils.ChatLib;
import mehdi.bad.addons.utils.SkyblockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.commons.compress.utils.IOUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;

@Mod(modid = BadAddons.MODID, version = BadAddons.VERSION, acceptedMinecraftVersions = "[1.8.9]")
public class BadAddons {

    public static final String VERSION = "1.5.1";
    public static final String MODID = "badaddons";
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static ArrayList settings = Config.collect(Configs.class);
    public static GuiScreen guiToOpen = null;
    private boolean checkForUpdate = false;
    public static float partialTicks = 0;

    public static Room currentRoom = null;
    public static ArrayList<Room> visitedRooms = new ArrayList<Room>();

    public static final String ROUTES_PATH = Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + File.separator + "config" + File.separator + "BadAddons"+ File.separator+"routes";


    public static BadCommand commandManager = new BadCommand(new Subcommand[]{
            new ProfitSubcommand(),
            new PercentageSubcommand(),
            new LastTradeSubcommand(),
            new SendTitleSubcommand(),
            new GuiSubcommand(),
            new FlipperSubcommand(),
            new ReplaceWordSubcommand(),
            new CheckupdateSubcommand(),
            new UpdatemodSubcommand()
        });
    
    @SubscribeEvent
    public void onTick(TickEndEvent event) {
        if (!checkForUpdate && SkyblockUtils.isInSkyblock()) {
            UpdateCheck.checkForUpdates();
            checkForUpdate = true;
        }
        SkyblockUtils.currentMap = SkyblockUtils.updateCurrentMap();
        if (guiToOpen != null) {
            mc.displayGuiScreen(guiToOpen);
            guiToOpen = null;
        }

    }

    @SubscribeEvent
    public void onRenderTick(RenderWorldLastEvent event) {
        this.partialTicks = event.partialTicks;
    }

	@EventHandler
    public void preInit(FMLPreInitializationEvent var1) {

        DungeonRooms.preInit(var1);

        MinecraftForge.EVENT_BUS.register(new TickEndEvent());
        Config.load();
        Configs.BedsFlipper = false;
        Config.save();
        ReplacedWordsConfig.load();
        ReplacedWordsConfig.save();
        WhitelistedBedsConfig.load();
        WhitelistedBedsConfig.save();

        MovablesManager.loadModules();
        MovableConfigManager.load();
        MovableConfigManager.save();

    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        DungeonRooms.init();
        checkRoutesData();

        MinecraftForge.EVENT_BUS.register(new MovableListener());

        ClientCommandHandler.instance.registerCommand(commandManager);
        ClientCommandHandler.instance.registerCommand(new CommandDevItemData());
        MinecraftForge.EVENT_BUS.register(this);

        MinecraftForge.EVENT_BUS.register(new ScoreBoard());
        MinecraftForge.EVENT_BUS.register(new NotificationManager());

        MinecraftForge.EVENT_BUS.register(new WeirdFlipper());
        MinecraftForge.EVENT_BUS.register(new TradesTracker());
        MinecraftForge.EVENT_BUS.register(new HighlightGraceItems());
        MinecraftForge.EVENT_BUS.register(new WaypointFetcher());
        MinecraftForge.EVENT_BUS.register(new AIPearlWaypoints());

        // DUNGEONS
        MinecraftForge.EVENT_BUS.register(new DungeonRooms());
        MinecraftForge.EVENT_BUS.register(new DungeonManager());
        MinecraftForge.EVENT_BUS.register(new RoomDetection());
        //MinecraftForge.EVENT_BUS.register(new Waypoints());
        MinecraftForge.EVENT_BUS.register(new OnWorldRender());
        MinecraftForge.EVENT_BUS.register(new OnPlayerTick());
        MinecraftForge.EVENT_BUS.register(new OnItemPickedUp());

        AuctionUtils.initItemsPrices();
    }

    public static void checkRoutesData() {
        try {
            String routesfilePath = BadAddons.ROUTES_PATH + File.separator + "routes.json";;
            String pearlRoutesPath =  BadAddons.ROUTES_PATH + File.separator + "pearlroutes.json";

            // Check if the config directory exists
            File configDir = new File(ROUTES_PATH);
            if (!configDir.exists()) {
                configDir.mkdirs();
            }

            File configFile = new File(routesfilePath);
            if (!configFile.exists()) {
                updateRoutes(configFile);
            }

            File pearlConfigFile = new File(pearlRoutesPath);
            if (!pearlConfigFile.exists()) {
                updatePearlRoutes(pearlConfigFile);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateRoutes(File configFile) {
        try {
            ChatLib.chat("§e[BA] Downloading routes data...");
            URL url = new URL("https://raw.githubusercontent.com/mehdii000/BadAddons/main/routes.json");
            downloadFile(configFile, url);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void updatePearlRoutes(File configFile) {
        try {
            ChatLib.chat("§e[BA] Downloading routes data...");
            URL url = new URL("https://raw.githubusercontent.com/mehdii000/BadAddons/main/pearlroutes.json");
            downloadFile(configFile, url);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void downloadFile(File outputFile, URL url) throws IOException {
        HttpsURLConnection connection =  (HttpsURLConnection) url.openConnection();
        InputStream inputStream = connection.getInputStream();
        OutputStream outputStream = Files.newOutputStream(outputFile.toPath());
        IOUtils.copy(inputStream, outputStream);
        outputStream.close();
        inputStream.close();
    }

    @SubscribeEvent
    public void onServerConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        try{
            Thread.sleep(3000);
        }catch(Exception e){
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.getCurrentServerData() == null) return;
        if (mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel.")) {
            event.manager.channel().pipeline().addBefore("packet_handler", "secretroutes_packet_handler", new PacketHandler());
        }
    }

    @SubscribeEvent
    public void onRecievePacket(PacketEvent.ReceiveEvent e) {
        try {
            if (e.packet instanceof S0DPacketCollectItem) { // Note to Hypixel: This is not manipulating packets, it is simply listening and checking for the collect item packet. If that is the correct packet, it simulates creating an itempickedup event client-side
                S0DPacketCollectItem packet = (S0DPacketCollectItem) e.packet;
                Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(packet.getCollectedItemEntityID());

                if(entity instanceof EntityItem) {
                    EntityItem item = (EntityItem) entity;
                    entity = Minecraft.getMinecraft().theWorld.getEntityByID(packet.getEntityID());
                    if(entity == null) {
                        return;
                    }
                    if(!entity.getCommandSenderEntity().getName().equals(Minecraft.getMinecraft().thePlayer.getName())) {
                        return;
                    }

                    PlayerEvent.ItemPickupEvent itemPickupEvent = new PlayerEvent.ItemPickupEvent(Minecraft.getMinecraft().thePlayer, item);
                    new OnItemPickedUp().onPickupItem(itemPickupEvent);
                }
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

}
