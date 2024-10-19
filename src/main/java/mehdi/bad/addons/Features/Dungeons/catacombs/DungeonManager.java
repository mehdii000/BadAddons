/*
 * Dungeon Rooms Mod - Secret Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2021 Quantizr(_risk)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package mehdi.bad.addons.Features.Dungeons.catacombs;

import mehdi.bad.addons.Features.Dungeons.utils.MapUtils;
import mehdi.bad.addons.utils.ChatLib;
import mehdi.bad.addons.utils.SkyblockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;

public class DungeonManager {
    Minecraft mc = Minecraft.getMinecraft();
    public static int gameStage = 0; //0: Not in Dungeon, 1: Entrance/Not Started, 2: Room Clear, 3: Boss, 4: Done

    public static boolean guiToggled = true;
    public static boolean motdToggled = true;

    public static Integer[][] map;
    public static Point[] entranceMapCorners;
    public static Point entrancePhysicalNWCorner;

    public static int tickAmount = 0;

    long bloodTime = Long.MAX_VALUE;

    boolean oddRun = true; //if current run number is even or odd

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onChat(ClientChatReceivedEvent event) {
        if (!SkyblockUtils.isInDungeon()) return;
        String message = event.message.getFormattedText();

        //gameStage set from 0 to 1 in the onTick function later
        if (message.startsWith("§e[NPC] §bMort§f: §rHere, I found this map when I first entered the dungeon.§r")) {
            gameStage = 2;
        } else if (message.startsWith("§r§c[BOSS] The Watcher§r§f: You have proven yourself. You may pass.§r")) {
            bloodTime = System.currentTimeMillis() + 5000; //5 seconds because additional messages might come through
        } else if (System.currentTimeMillis() > bloodTime && ((message.startsWith("§r§c[BOSS] ") && !message.contains(" The Watcher§r§f:")) || message.startsWith("§r§4[BOSS] "))) {
            if (gameStage != 3) {
                gameStage = 3;
                //this part mostly so /room json doesn't error out
                RoomDetection.resetCurrentRoom();
                RoomDetection.roomName = "Boss Room";
                RoomDetection.roomCategory = "General";
                //RoomDetection.newRoom() //uncomment to display Boss Room in gui
            }
        } else if (message.contains("§r§c☠ §r§eDefeated §r")) {
            gameStage = 4;
            RoomDetection.resetCurrentRoom();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        EntityPlayerSP player = mc.thePlayer;

        if (!SkyblockUtils.isInDungeon()) return; //From this point forward, everything assumes that Utils.inCatacombs == true
        tickAmount++;

        if ((gameStage == 0 || gameStage == 1) && tickAmount % 20 == 0) {

            if (gameStage == 0) {
                ChatLib.chat("§cBA-Debug> §rJust checking stuff...");
                gameStage = 1;
            }

            Integer[][] map = MapUtils.updatedMap();
            if (map != null) {
                if (gameStage==1) ChatLib.chat("§cBA-Debug> §rFound map in hotbar!");
                gameStage = 2;
                return;
            }

            if (gameStage == 1 && entrancePhysicalNWCorner == null) {
                if (!player.getPositionVector().equals(new Vec3(0.0D,0.0D,0.0D))) {
                    //this point is calculated using math, not scanning, which may cause issues when reconnecting to a run
                    entrancePhysicalNWCorner = MapUtils.getClosestNWPhysicalCorner(player.getPositionVector());
                }
            }

            tickAmount = 0;
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        tickAmount = 0;
        gameStage = 0;
        map = null;
        entranceMapCorners = null;
        entrancePhysicalNWCorner = null;
        RoomDetection.entranceMapNullCount = 0;

        bloodTime = Long.MAX_VALUE;

        if (RoomDetection.stage2Executor != null) RoomDetection.stage2Executor.shutdown();

        Waypoints.allSecretsMap.clear();

        RoomDetection.resetCurrentRoom();
    }
}