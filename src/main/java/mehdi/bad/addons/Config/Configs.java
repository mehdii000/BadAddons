package mehdi.bad.addons.Config;

public class Configs {

	// GENERAL
	@Property(
            type = Property.Type.FOLDER,
            name = "General"
    )
    public static boolean GeneralFolder = false;
	
	//CRIMSON ISLES
	@Property(
            type = Property.Type.FOLDER,
            name = "Misc & Visual"
    )
    public static boolean MiscFolder = false;

    @Property(
            type = Property.Type.FOLDER,
            name = "Maps",
            parent = "Misc & Visual"
    )
    public static boolean CrimsonIslesMapFolder = false;

    @Property(
            type = Property.Type.FOLDER,
            name = "Dungeons",
            parent = "Misc & Visual"
    )
    public static boolean DungeonsFolder = false;

    @Property(
            type = Property.Type.FOLDER,
            name = "Trackers",
            parent = "Misc & Visual"
    )
    public static boolean MiscTrackersFolder = false;

    @Property(
            type = Property.Type.FOLDER,
            name = "§b/bb gui §7to change locations",
            parent = "Misc & Visual"
    )
    public static boolean GuiLocationsUslessFolder = false;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "Crimson Isles Map",
            description = "Draws a map of the crimson isles, the map is pre rendered so\n its not bannable nerds. \n§cOnly Works In The Nether",
            parent = "Maps"
    )
    public static boolean CrimsonIslesMap = false;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "FetchWaypoints",
            description = "Fetches waypoints coords from chat and renders them.",
            parent = "Maps"
    )
    public static boolean FetchWaypoints = false;

    @Property(
            type = Property.Type.FOLDER,
            name = "Flipping",
            description = "QOL thing mainly for flippers.",
            parent = "General"
    )
    public static boolean AuctionHouseFolder = false;

    /*--------------------------------LOWBALLING------------------------------*/

    @Property(
            type = Property.Type.FOLDER,
            name = "Lowballing",
            description = "QOL thing mainly for lowballers.",
            parent = "General"
    )
    public static boolean LowballingFolder = false;

    @Property(
            type = Property.Type.TEXT,
            name = "TradesWebhook",
            description = "The url of the discord webhook to send trades to.",
            parent = "Lowballing"
    )
    public static String TradesWebhook = "";

    @Property(
            type = Property.Type.BOOLEAN,
            name = "TrackTrades",
            description = "A System that tracks trades and sends them as discord hooks.",
            parent = "Lowballing"
    )
    public static boolean TrackTrades = false;

    /*-------------------------------------------------------------------------*/

    @Property(
            type = Property.Type.BOOLEAN,
            name = "BedsFlipper",
            description = "A System that shows §a1/3 §7of all bed auction items. \nSupports mutliple settings.",
            parent = "Flipping"
    )
    public static boolean BedsFlipper = false;

    @Property(type = Property.Type.SELECT, name = "Flipper Mode", parent = "Flipping",
            options = {"§aAll", "§bProfit-Only", "§eWhitelist (/bb flipper)", "§cWhitelistOnlyLbin"},
            description = "Select a flipper §efilter §7type, ask §eautophobic §7about it.")
    public static int FlipperType = 0;

    @Property(type = Property.Type.NUMBER, name = "Minimum Profit In 500k's", parent = "Flipping",
            min = 0, max = 100, step = 1)
    public static int FlipperMinProfit = 1;

    @Property(type = Property.Type.NUMBER, name = "Min seconds elapsed since flip was created", parent = "Flipping",
            min = 1, max = 60, step = 1)
    public static int FlipperMinTime = 20;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "CheckForLore",
            description = "Wether the whitelisted bed filter will check for item lore or not!",
            parent = "Flipping"
    )
    public static boolean BedsFlipperLoreCheck = false;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "HighlightGraceItems",
            description = "Highlight items that are in grace period.",
            parent = "Flipping"
    )
    public static boolean HighlightGraceItems = false;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "Dungeon Routes",
            description = "they call me a §ccommunist, pretty self explanatory",
            parent = "Dungeons"
    )
    public static boolean DungeonRoutes = false;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "DynamicRoutesMode",
            description = "Basically same thing but its a little bit smarter, unlike me.",
            parent = "Dungeons"
    )
    public static boolean DynamicDungeonRoutes = false;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "Draw Text",
            description = "Draws a text above the waypoints indicating type.",
            parent = "Dungeons"
    )
    public static boolean DungeonRoutesText = true;

    @Property(type = Property.Type.SELECT, name = "RoutesMethod", parent = "Dungeons",
            options = {"§dPearls", "§2AOTV"},
            description = "Select a rendering type for the routes.")
    public static int DungeonRoutesMethod = 1;

    @Property(type = Property.Type.SELECT, name = "RoutesType", parent = "Dungeons",
            options = {"§6Particles", "§bLine"},
            description = "Select a rendering type for the routes.")
    public static int DungeonRoutesType = 1;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "TerminalWaypoints",
            description = "Highlights floor 7 terminal waypoints.",
            parent = "Dungeons"
    )
    public static boolean DungeonsTerminalWaypoints = false;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "Render All Terminals",
            description = "Wether to rendera all terminals or only specific phase terminals.",
            parent = "Dungeons"
    )
    public static boolean DungeonsTerminalsRenderAll = false;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "TimerCommand",
            description = "allows you or your party to use @timer <seconds> to set a timer!",
            parent = "Dungeons"
    )
    public static boolean SetTimerCommand = false;
	
	// BLOCKS 
    @Property(
            type = Property.Type.FOLDER,
            name = "Rendering",
            description = "Changes rendering stuff.",
            parent = "General"
    )
    public static boolean RenderingFolder = false;

    @Property(
            type = Property.Type.FOLDER,
            name = "Debug",
            description = "dev stuff you dum dum",
            parent = "General"
    )
    public static boolean DebugFolder = false;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "ShowScanningMessage",
            description = "Shows a scanning message in chat for beds flipper",
            parent = "Debug"
    )
    public static boolean debugShowScanning = false;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "WordsReplacing",
            description = "Adds the ability to repalce words via /bb repalceword",
            parent = "Rendering"
    )
    public static boolean WordReplacing = false;
	
	// KUUDRA
    @Property(
            type = Property.Type.FOLDER,
            name = "Kuudra",
            description = "Kuudra good stuff."
    )
    public static boolean KuudraFolder = false;

    @Property(
            type = Property.Type.FOLDER,
            name = "Waypoints",
            description = "Kuudra pres and safe spots waypoints stuff.",
            parent = "Kuudra"
    )
    public static boolean KuudraWaypointsFolder = false;

    @Property(
            type = Property.Type.FOLDER,
            name = "FreshHighlight",
            description = "Kuudra freshing stuff.",
            parent = "Kuudra"
    )
    public static boolean KuudraFreshFolder = false;

    @Property(
            type = Property.Type.FOLDER,
            name = "KuudraGameplay",
            description = "Kuudra important blah blah stuff.",
            parent = "Kuudra"
    )
    public static boolean KuudraGameplayFolder = false;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "SupplyCount",
            description = "Shows a title everytime a supply is recovered!",
            parent = "KuudraGameplay"
    )
    public static boolean SupplyCount = false;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "Party Dps Tracker",
            description = "Tracks party's DPS §8(Damage per second) §7in kuudra §cP5§7.",
            parent = "KuudraGameplay"
    )
    public static boolean PartyDpsTracker = false;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "§bSuppliesWaypoints",
            description = "Highlights kuudra supplies in the lava.",
            parent = "Waypoints"
    )
    public static boolean KuudraSuppliesWaypoints = false;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "§bRenderBeaconBeam",
            description = "Wether to render  beacon or only a box.",
            parent = "Waypoints"
    )
    public static boolean SuppliesWaypointsBeacon = false;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "SuppliesWaypointsText",
            description = "Draws a §4Supply §7in the kuudra supply waypoint.",
            parent = "Waypoints"
    )
    public static boolean SuppliesWaypointsString = true;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "TentacleHider",
            description = "Hides kuudra tentacles in the last phase of t5",
            parent = "KuudraGameplay"
    )
    public static boolean TentacleHider = false;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "FreshDisplay",
            description = "Displays a title/notification when §afresh tools §7perk activates.",
            parent = "FreshHighlight"
    )
    public static boolean FreshDisplay = false;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "FreshMessage",
            description = "Sends a message in chat to notify others that u freshed.",
            parent = "FreshHighlight"
    )
    public static boolean FreshChatMessage = false;

    @Property(
            type = Property.Type.TEXT,
            name = "FreshText",
            description = "Custom message that appears when u fresh",
            parent = "FreshHighlight"
    )
    public static String FreshDisplayString = "FRESH";

    @Property(
            type = Property.Type.BOOLEAN,
            name = "PresWaypoints",
            description = "Highlights supply-pres waypoints.",
            parent = "Waypoints"
    )
    public static boolean KuudraPresHighlight = false;

    @Property(type = Property.Type.SELECT, name = "WaypointsType", parent = "Waypoints",
            options = {"§bCube/Text", "§aText"},
            description = "Select a waypoints rendering type.")
    public static int KuudraPresHighlightType = 0;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "PresWaypointsName",
            description = "Name each pre waypoint based on their location.",
            parent = "Waypoints"
    )
    public static boolean KuudraPresNames = false;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "Proximity",
            description = "Highlights pearl waypoints dynamically to your pre.",
            parent = "Waypoints"
    )
    public static boolean KuudraWaypointsProximity = false;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "§cHidePerkUpgrades",
            description = "Hides the usless spec upgrades via replacing everything by BALLISTA MECHANICS\nRAHHHHHHHH Hail Spec Route",
            parent = "KuudraGameplay"
    )
    public static boolean HidePerkUpgrades = false;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "KuudraPreDetection",
            description = "Marks and highilghts your current & last pre!",
            parent = "KuudraGameplay"
    )
    public static boolean KuudraPreDetection = false;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "BetterPickupMessage",
            description = "Replaces the message you get when you place a supply.",
            parent = "KuudraGameplay"
    )
    public static boolean BetterPickupSupplyMessage = false;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "NoBlindness",
            description = "Hides blindness effect.",
            parent = "KuudraGameplay"
    )
    public static boolean HideBlindness = false;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "BallistaProgress",
            description = "Draws a large string indicating kuudra ballista progress",
            parent = "KuudraGameplay"
    )
    public static boolean BalistaProgress = false;

    @Property(
            type = Property.Type.BOOLEAN,
            name = "BoxKuudra",
            description = "Draws a large box to esp kuudra.",
            parent = "KuudraGameplay"
    )
    public static boolean BoxKuudra = false;

    @Property(type = Property.Type.SELECT, name = "BoxKuudraSize", parent = "KuudraGameplay",
            options = {"1", "2", "3", "4"},
            description = "Select a size for outline.")
    public static int BoxKuudraSize = 0;

}
