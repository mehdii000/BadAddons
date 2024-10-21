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
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DungeonTerminalWaypoints {

    private static final DungeonTerminal[] ALL_TERMINALS = turnJsonIntoTerms();
    private final List<DungeonTerminal> terminalsDone = new ArrayList<>();
    private boolean isInTerminals = false;
    private int currentPhase = 0;
    private int devicesDone = 0;

    private static final Pattern terminalPattern = Pattern.compile("(?<name>\\w+) activated a terminal! \\((?<start>\\d+)/(?<end>\\d+)\\)");
    private static final Pattern devicePattern = Pattern.compile("(?<name>\\w+) completed a device! \\((?<start>\\d+)/(?<end>\\d+)\\)");

    @SubscribeEvent
    public void onChatStuff(ClientChatReceivedEvent event) {
        if (!SkyblockUtils.isInDungeon()) return;

        if (event.type == 0) {
            String message = StringUtils.stripControlCodes(event.message.getUnformattedText());

            if (message.contains("[BOSS] Goldor: Who dares trespass into my domain?")) {
                resetTerminals();
                isInTerminals = true;
            } else if (message.contains("[BOSS] Goldor: You have done it, you destroyed the factory…")) {
                resetTerminals();
                isInTerminals = false;
            }

            Result res = getNameStartEnd(message);
            if (res != null) {
                ChatLib.chat("§e[BA] §b" + res.name + " did terminal §c" + res.startEnd[0] + " §7[" + (res.startEnd[1] - res.startEnd[0]) + " Left]");
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
        if (!Configs.DungeonsTerminalWaypoints || !SkyblockUtils.isInDungeon() || !isInTerminals) return;

        for (DungeonTerminal term : ALL_TERMINALS) {
            if (!terminalsDone.contains(term) && term.phase == currentPhase) { // Render only terminals not yet done
                V2RenderUtils.renderBeacon(term.getPos().getX(), term.getPos().getY(), term.getPos().getZ(), Color.BLUE.getRGB(), 0.8f, event.partialTicks);
                V2RenderUtils.renderBlockModel(term.getPos(), Blocks.diamond_block, event.partialTicks);
            }
        }
    }

    private List<DungeonTerminal> getRemainingTerminals() {
        Set<DungeonTerminal> doneSet = new HashSet<>(terminalsDone);
        List<DungeonTerminal> remaining = new ArrayList<>();

        for (DungeonTerminal terminal : ALL_TERMINALS) {
            if (!doneSet.contains(terminal)) {
                remaining.add(terminal);
            }
        }
        return remaining;
    }

    private void getClosestTermAndPushToDone(BlockPos position) {
        DungeonTerminal closestTerminal = null;
        double closestDistance = Double.MAX_VALUE;

        for (DungeonTerminal terminal : getRemainingTerminals()) {
            if (terminal.getPhase() == currentPhase) {
                double distance = position.distanceSq(terminal.getPos());

                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestTerminal = terminal;
                }
            }
        }

        if (closestTerminal != null) {
            terminalsDone.add(closestTerminal);
        }
    }

    private Result getNameStartEnd(String message) {
        Matcher terminalMatcher = terminalPattern.matcher(message);
        if (terminalMatcher.find()) {
            String name = terminalMatcher.group("name");
            int start = Integer.parseInt(terminalMatcher.group("start"));
            int end = Integer.parseInt(terminalMatcher.group("end"));
            return new Result(name, new int[]{start, end});
        }

        Matcher deviceMatcher = devicePattern.matcher(message);
        if (deviceMatcher.find()) {
            String name = deviceMatcher.group("name");
            int start = Integer.parseInt(deviceMatcher.group("start"));
            int end = Integer.parseInt(deviceMatcher.group("end"));
            devicesDone++;
            return new Result(name, new int[]{start, end});
        }

        return null;
    }

    private void resetTerminals() {
        terminalsDone.clear();
        currentPhase = 0;
        devicesDone = 0;
    }

    private static DungeonTerminal[] turnJsonIntoTerms() {
        String filePath = BadAddons.ROUTES_PATH + File.separator + "f7terminals.json";
        Gson gson = new GsonBuilder().create();

        try (FileReader reader = new FileReader(filePath)) {
            JsonArray jsonArray = gson.fromJson(reader, JsonArray.class);
            List<DungeonTerminal> terminals = new ArrayList<>();

            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject obj = jsonArray.get(i).getAsJsonObject();
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
            return terminals.toArray(new DungeonTerminal[0]);
        } catch (IOException e) {
            throw new RuntimeException("File not found: " + filePath, e);
        }
    }

    static class DungeonTerminal {
        private final BlockPos pos;
        private final int phase;

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
        private final String name;
        private final int[] startEnd;

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
