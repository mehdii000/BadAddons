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
import mehdi.bad.addons.Features.Dungeons.DungeonRoomDetection;
import mehdi.bad.addons.Features.Dungeons.RoomLoader;
import mehdi.bad.addons.Features.Dungeons.solvers.WaterboardSolver;
import mehdi.bad.addons.Features.General.*;
import mehdi.bad.addons.Features.Lowballing.TradesTracker;
import mehdi.bad.addons.Objects.*;
import mehdi.bad.addons.utils.AuctionUtils;
import mehdi.bad.addons.utils.SkyblockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Iterator;

@Mod(modid = BadAddons.MODID, version = BadAddons.VERSION, acceptedMinecraftVersions = "[1.8.9]")
public class BadAddons {

    public static final String VERSION = "1.4";
    public static final String MODID = "badaddons";
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static ArrayList settings = Config.collect(Configs.class);
    public static GuiScreen guiToOpen = null;
    private boolean checkForUpdate = false;
    public static float partialTicks = 0;

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

        new RoomLoader();
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
        MinecraftForge.EVENT_BUS.register(new DungeonRoomDetection());
        MinecraftForge.EVENT_BUS.register(new WaterboardSolver());
        MinecraftForge.EVENT_BUS.register(new WaypointFetcher());
    }
	
}
