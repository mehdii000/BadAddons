package mehdi.bad.addons;

import mehdi.bad.addons.Commands.BadCommand;
import mehdi.bad.addons.Commands.CommandDevItemData;
import mehdi.bad.addons.Commands.Subcommands.*;
import mehdi.bad.addons.Config.Config;
import mehdi.bad.addons.Config.Configs;
import mehdi.bad.addons.Config.modules.MovableConfigManager;
import mehdi.bad.addons.Config.modules.MovableListener;
import mehdi.bad.addons.Config.modules.MovablesManager;
import mehdi.bad.addons.Events.TickEndEvent;
import mehdi.bad.addons.Features.Dungeons.DungeonRooms;
import mehdi.bad.addons.Features.Dungeons.catacombs.DungeonManager;
import mehdi.bad.addons.Features.Dungeons.catacombs.RoomDetection;
import mehdi.bad.addons.Features.Dungeons.catacombs.Waypoints;
import mehdi.bad.addons.Features.Dungeons.utils.PacketHandler;
import mehdi.bad.addons.Features.General.*;
import mehdi.bad.addons.Features.Kuudra.AIPearlWaypoints;
import mehdi.bad.addons.Features.Lowballing.TradesTracker;
import mehdi.bad.addons.Objects.*;
import mehdi.bad.addons.utils.AuctionUtils;
import mehdi.bad.addons.utils.ChatLib;
import mehdi.bad.addons.utils.SkyblockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
import java.util.Iterator;

@Mod(modid = BadAddons.MODID, version = BadAddons.VERSION, acceptedMinecraftVersions = "[1.8.9]")
public class BadAddons {

    public static final String VERSION = "1.4.2";
    public static final String MODID = "badaddons";
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static ArrayList settings = Config.collect(Configs.class);
    public static GuiScreen guiToOpen = null;
    private boolean checkForUpdate = false;
    public static float partialTicks = 0;

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

        AuctionUtils.initItemsPrices();
        Iterator keyBinds = KeyBindUtils.keyBinds.iterator();
        while (keyBinds.hasNext()) {
            KeyBind key = (KeyBind) keyBinds.next();
            ClientRegistry.registerKeyBinding(key.mcKeyBinding());
        }

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
        MinecraftForge.EVENT_BUS.register(new Waypoints());


    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        DungeonRooms.init();
        checkRoutesData();
    }

    public static void checkRoutesData() {
        try {
            String filePath = ROUTES_PATH+File.separator+ "routes.json";

            // Check if the config directory exists
            File configDir = new File(ROUTES_PATH);
            if (!configDir.exists()) {
                configDir.mkdirs();
            }

            File configFile = new File(filePath);
            if (!configFile.exists()) {
                updateRoutes(configFile);
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

}
