package mehdi.bad.addons.Features.Dungeons;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Features.Dungeons.catacombs.RoomDetection;
import mehdi.bad.addons.Features.Dungeons.utils.DungeonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DungeonRooms {
    public static JsonObject roomsJson;
    public static JsonObject waypointsJson;
    public static HashMap<String,HashMap<String,long[]>> ROOM_DATA = new HashMap<>();
    public static String configDir;

    public static void preInit(final FMLPreInitializationEvent event) {
        configDir = event.getModConfigurationDirectory().toString();
    }

    public static void init() {

        List<Path> paths = DungeonUtils.getAllPaths("catacombs");
        final ExecutorService executor = Executors.newFixedThreadPool(4); //don't need 8 threads cause it's just 1x1 that takes longest
        Future<HashMap<String, long[]>> future1x1 = executor.submit(() -> DungeonUtils.pathsToRoomData("1x1", paths));
        Future<HashMap<String, long[]>> future1x2 = executor.submit(() -> DungeonUtils.pathsToRoomData("1x2", paths));
        Future<HashMap<String, long[]>> future1x3 = executor.submit(() -> DungeonUtils.pathsToRoomData("1x3", paths));
        Future<HashMap<String, long[]>> future1x4 = executor.submit(() -> DungeonUtils.pathsToRoomData("1x4", paths));
        Future<HashMap<String, long[]>> future2x2 = executor.submit(() -> DungeonUtils.pathsToRoomData("2x2", paths));
        Future<HashMap<String, long[]>> futureLShape = executor.submit(() -> DungeonUtils.pathsToRoomData("L-shape", paths));
        Future<HashMap<String, long[]>> futurePuzzle = executor.submit(() -> DungeonUtils.pathsToRoomData("Puzzle", paths));
        Future<HashMap<String, long[]>> futureTrap = executor.submit(() -> DungeonUtils.pathsToRoomData("Trap", paths));

        //register classes
        //MinecraftForge.EVENT_BUS.register(new DungeonManager());
        //MinecraftForge.EVENT_BUS.register(new RoomDetection());
        //MinecraftForge.EVENT_BUS.register(new Waypoints());

        //get room and waypoint info
        try (BufferedReader roomsReader = new BufferedReader(new InputStreamReader(BadAddons.mc.getResourceManager()
                .getResource(new ResourceLocation("roomdetection", "dungeonrooms.json")).getInputStream()));
             BufferedReader waypointsReader = new BufferedReader(new InputStreamReader(BadAddons.mc.getResourceManager()
                     .getResource(new ResourceLocation("roomdetection", "secretlocations.json")).getInputStream()))
        ) {
            Gson gson = new Gson();
            roomsJson = gson.fromJson(roomsReader, JsonObject.class);

            waypointsJson = gson.fromJson(waypointsReader, JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //set RoomData to futures - this will block if the rest of init was fast
        try {
            ROOM_DATA.put("1x1", future1x1.get());
            ROOM_DATA.put("1x2", future1x2.get());
            ROOM_DATA.put("1x3", future1x3.get());
            ROOM_DATA.put("1x4", future1x4.get());
            ROOM_DATA.put("2x2", future2x2.get());
            ROOM_DATA.put("L-shape", futureLShape.get());
            ROOM_DATA.put("Puzzle", futurePuzzle.get());
            ROOM_DATA.put("Trap", futureTrap.get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }

    public static void checkRoomData() {
        if(RoomDetection.roomName == null) {
            RoomDetection.roomName = "undefined";
        }
        if(RoomDetection.roomCorner == null) {
            RoomDetection.roomCorner = new Point(0, 0);
        }
        if(RoomDetection.roomDirection == null) {
            RoomDetection.roomDirection = "NW";
        }
    }

}