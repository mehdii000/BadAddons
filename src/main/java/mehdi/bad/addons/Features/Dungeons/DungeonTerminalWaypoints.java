package mehdi.bad.addons.Features.Dungeons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Config.Configs;
import mehdi.bad.addons.utils.ChatLib;
import mehdi.bad.addons.utils.SkyblockUtils;
import mehdi.bad.addons.utils.V2RenderUtils;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DungeonTerminalWaypoints {

    private static final DungeonTerminal[] ALL_TERMINALS = turnJsonIntoTerms();
    private DungeonTerminal[] terminalsDone = {};
    private boolean isInTerminals = false;
    private int currentPhase = 0;
    private int devicesDone = 0;
    private final String terminalRegex = "(?<name>\\w+) activated a terminal! \\((?<start>\\d+)/(?<end>\\d+)\\)";
    private final String deviceRegex = "(?<name>\\w+) completed a device! \\((?<start>\\d+)/(?<end>\\d+)\\)";


    @SubscribeEvent
    public void onChatStuff(ClientChatReceivedEvent event) {
        if (!Configs.DungeonsTerminalWaypoints) return;
        if (!SkyblockUtils.isInDungeon() || !isInTerminals) return;
        if (event.type == 0) {
            String message = StringUtils.stripControlCodes(event.message.getUnformattedText());

            if (message.contains("[BOSS] Goldor: Who dares trespass into my domain?")) {
                terminalsDone = new DungeonTerminal[]{};
                isInTerminals = true;
                currentPhase = 0;
                devicesDone = 0;
            }
            if (message.contains("[BOSS] Goldor: You have done it, you destroyed the factory…")) {
                terminalsDone = new DungeonTerminal[]{};
                isInTerminals = false;
                currentPhase = 0;
                devicesDone = 0;
            }

            Result res = getNameStartEnd(message);
            if (res != null) {
                ChatLib.chat("§e[BA] §b" + res.name + " did terminal §c" + res.startEnd[0] + " §7[" + (res.startEnd[1]-res.startEnd[0]) + " Left]");
                getClosestTermAndPushToDone(BadAddons.mc.theWorld.getPlayerEntityByName(res.name).getPosition());
                if (res.startEnd[0] == res.startEnd[1]) {
                    currentPhase++;
                    devicesDone = 0;
                    ChatLib.chat("§e[BA] §bAll terms are done! §7[Phase " + currentPhase + "]");
                }
            }


        }
    }

    @SubscribeEvent
    public void onWorldRenderEvent(RenderWorldLastEvent event) {
        if (!Configs.DungeonsTerminalWaypoints) return;
        if (SkyblockUtils.isInDungeon() && isInTerminals) {
            for (DungeonTerminal term : getIntersection(ALL_TERMINALS, terminalsDone)) {
                V2RenderUtils.renderBeacon(term.pos.getX(), term.pos.getY(), term.pos.getZ(), Color.BLUE.getRGB(), 0.8f, event.partialTicks);
                V2RenderUtils.renderBlockModel(term.pos, Blocks.redstone_block, event.partialTicks);
            }
        }
    }

    public static DungeonTerminal[] getIntersection(DungeonTerminal[] array1, DungeonTerminal[] array2) {
        // Step 1: Convert one array to a HashSet
        Set<DungeonTerminal> set = new HashSet<>();
        for (DungeonTerminal terminal : array1) {
            set.add(terminal);
        }

        // Step 2: Check for common elements in the second array
        List<DungeonTerminal> result = new ArrayList<>();
        for (DungeonTerminal terminal : array2) {
            if (set.contains(terminal)) {
                result.add(terminal); // add the matching terminal to the result list
            }
        }

        // Convert the result list back to an array
        return result.toArray(new DungeonTerminal[0]);
    }

    private void getClosestTermAndPushToDone(BlockPos position) {
        DungeonTerminal closestTerminal = null;
        double closestDistance = Double.MAX_VALUE;

        // Loop through all terminals
        for (DungeonTerminal terminal : ALL_TERMINALS) {
            // Check if the phase is 0
            if (terminal.getPhase() == 0) {
                // Calculate the distance between the provided position and the terminal's position
                double distance = position.distanceSq(terminal.getPos());

                // If this terminal is closer than the current closest, update the closest
                if (distance < closestDistance) {
                    closestDistance = distance;
                    terminalsDone[terminalsDone.length + 1] = terminal;
                }
            }
        }
    }

    private Result getNameStartEnd(String message) {
        Pattern terminalPattern = Pattern.compile(terminalRegex);
        Pattern devicePattern = Pattern.compile(deviceRegex);
        // Check terminal message
        Matcher terminalMatcher = terminalPattern.matcher(message);
        if (terminalMatcher.find()) {
            String name = terminalMatcher.group("name");
            int start = Integer.parseInt(terminalMatcher.group("start"));
            int end = Integer.parseInt(terminalMatcher.group("end"));
            return new Result(name, new int[]{start, end});
        }
        // Check device message
        Matcher deviceMatcher = devicePattern.matcher(message);
        if (deviceMatcher.find()) {
            String name = deviceMatcher.group("name");
            int start = Integer.parseInt(deviceMatcher.group("start"));
            int end = Integer.parseInt(deviceMatcher.group("end"));
            devicesDone++;
            return new Result(name, new int[]{start, end});
        }
        // If no match is found, return null
        return null;
    }

    private static DungeonTerminal[] turnJsonIntoTerms() {
        String filePath = BadAddons.ROUTES_PATH + File.separator + "f7terminals.json";
        Gson gson = new GsonBuilder().create();
        FileReader reader;
        try {
            reader = new FileReader(filePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + filePath, e);
        }

        // Parse the JSON file into a JsonArray
        JsonArray jsonArray = gson.fromJson(reader, JsonArray.class);

        List<DungeonTerminal> terminals = new ArrayList<>();

        // Loop through the JsonArray and extract the DungeonTerminal objects
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject obj = jsonArray.get(i).getAsJsonObject();

            // Check if the type is "terminal"
            if (obj.get("type").getAsString().equals("terminal")) {
                JsonArray locationArray = obj.get("location").getAsJsonArray();
                BlockPos pos = new BlockPos(
                        locationArray.get(0).getAsInt(),
                        locationArray.get(1).getAsInt(),
                        locationArray.get(2).getAsInt()
                );

                int phase = obj.get("phase").getAsInt();
                terminals.add(new DungeonTerminal(pos, phase));
            }
        }

        // Return the list as an array of DungeonTerminal
        return terminals.toArray(new DungeonTerminal[0]);
    }

    static class DungeonTerminal {
        private BlockPos pos;
        private int phase;
        public DungeonTerminal(BlockPos pos, int phase) {
            this.pos = pos;
            this.phase = phase;
        }

        public BlockPos getPos() {
            return pos;
        }

         public int getPhase() {
             return phase;
         }
     }

     static class Result {
        private String name;
        private int[] startEnd;

        public Result(String name, int[] startEnd) {
            this.name = name;
            this.startEnd = startEnd;
        }

        public String getName() {
            return name;
        }

        public int[] getStartEnd() {
            return startEnd;
        }
    }

}
