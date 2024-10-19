/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2023 yourboykyle
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

package mehdi.bad.addons.Features.Dungeons;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Config.Configs;
import mehdi.bad.addons.Features.Dungeons.catacombs.DungeonManager;
import mehdi.bad.addons.Features.Dungeons.catacombs.RoomDetection;
import mehdi.bad.addons.Features.Dungeons.utils.MapUtils;
import mehdi.bad.addons.Features.Dungeons.utils.Room;
import mehdi.bad.addons.Features.Dungeons.utils.RotationUtils;
import mehdi.bad.addons.utils.*;
import mehdi.bad.addons.utils.multistorage.Triple;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class OnWorldRender {

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!Configs.DungeonRoutes) return;
        if (BadAddons.currentRoom == null || !SkyblockUtils.isInDungeon()) return;

        try {
            if (DungeonManager.gameStage != 2) {
                return;
            }
            renderingCallback(BadAddons.currentRoom.currentSecretWaypoints, event, BadAddons.currentRoom.currentSecretIndex);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void renderingCallback(JsonObject currentSecretWaypoints, RenderWorldLastEvent event, int index2){
        //SecretUtils.renderSecrets(event);
        ArrayList<BlockPos> etherwarpPositions = new ArrayList<>();
        ArrayList<BlockPos> minesPositions = new ArrayList<>();
        ArrayList<BlockPos> interactsPositions = new ArrayList<>();
        ArrayList<BlockPos> superboomsPositions = new ArrayList<>();
        ArrayList<Triple<Double, Double, Double>> enderpearlPositons = new ArrayList<>();
        ArrayList<Tuple<Float, Float>> enderpearlAngles = new ArrayList<>();

        GlStateManager.disableDepth();
        GlStateManager.disableCull();
        ///GlStateManager.disableBlend();


        // Render the etherwarps
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("etherwarps") != null && index2 == BadAddons.currentRoom.currentSecretIndex) {
            JsonArray etherwarpLocations = currentSecretWaypoints.get("etherwarps").getAsJsonArray();
            for (JsonElement etherwarpLocationElement : etherwarpLocations) {
                JsonArray etherwarpLocation = etherwarpLocationElement.getAsJsonArray();

                DungeonRooms.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(etherwarpLocation.get(0).getAsInt(), etherwarpLocation.get(1).getAsInt(), etherwarpLocation.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);

                etherwarpPositions.add(pos);
                GuiUtils.drawSelectionFilledBoxAtBlock(pos, Color.MAGENTA, 50);
            }
        }

        // Render the mines
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("mines") != null && index2 == BadAddons.currentRoom.currentSecretIndex) {
            JsonArray mineLocations = currentSecretWaypoints.get("mines").getAsJsonArray();
            for (JsonElement mineLocationElement : mineLocations) {
                JsonArray mineLocation = mineLocationElement.getAsJsonArray();

                DungeonRooms.checkRoomData();
                BlockPos pos = MapUtils. relativeToActual(new BlockPos(mineLocation.get(0).getAsInt(), mineLocation.get(1).getAsInt(), mineLocation.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);
                minesPositions.add(pos);
                GuiUtils.drawBoundingBoxAtBlock(pos, Color.RED);
            }
        }

        // Render the interacts
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("interacts") != null && index2 == BadAddons.currentRoom.currentSecretIndex) {
            JsonArray interactLocations = currentSecretWaypoints.get("interacts").getAsJsonArray();
            for (JsonElement interactLocationElement : interactLocations) {
                JsonArray interactLocation = interactLocationElement.getAsJsonArray();

                DungeonRooms.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(interactLocation.get(0).getAsInt(), interactLocation.get(1).getAsInt(), interactLocation.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);
                interactsPositions.add(pos);
                GuiUtils.drawBoundingBoxAtBlock(pos, Color.CYAN);

            }
        }

        // Render the tnts
        /*if (currentSecretWaypoints != null && currentSecretWaypoints.get("tnts") != null && index2 == BadAddons.currentRoom.currentSecretIndex) {
            JsonArray tntLocations = currentSecretWaypoints.get("tnts").getAsJsonArray();
            for (JsonElement tntLocationElement : tntLocations) {
                JsonArray tntLocation = tntLocationElement.getAsJsonArray();

                DungeonRooms.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(tntLocation.get(0).getAsInt(), tntLocation.get(1).getAsInt(), tntLocation.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);
                superboomsPositions.add(pos);
                GuiUtils.drawBoundingBoxAtBlock(pos, Color.RED);
            }
        }*/
        // Render normal lines if config says so
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("locations") != null && Configs.DungeonRoutesType == 1  && index2 == BadAddons.currentRoom.currentSecretIndex) {
            GlStateManager.enableDepth();
            List<Triple<Double, Double, Double>> lines = new LinkedList<>();

            JsonArray lineLocations = currentSecretWaypoints.get("locations").getAsJsonArray();
            for (JsonElement lineLocationElement : lineLocations) {
                JsonArray lineLocation = lineLocationElement.getAsJsonArray();

                DungeonRooms.checkRoomData();
                Triple<Double, Double, Double> linePos = MapUtils.relativeToActual(lineLocation.get(0).getAsDouble(), lineLocation.get(1).getAsDouble(), lineLocation.get(2).getAsDouble(), RoomDetection.roomDirection, RoomDetection.roomCorner);
                linePos.setOne(linePos.getOne() + 0.5);
                linePos.setTwo(linePos.getTwo() + 0.5);
                linePos.setThree(linePos.getThree() + 0.5);
                lines.add(linePos);
            }

            V2RenderUtils.drawMultipleNormalLines(lines, event.partialTicks, Color.BLUE, 3);
            GlStateManager.disableDepth();
        }

        // Render the ender pearls
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("enderpearls") != null && index2 == BadAddons.currentRoom.currentSecretIndex) {
            JsonArray enderpearlAnglesArray = currentSecretWaypoints.get("enderpearlangles").getAsJsonArray();
            for (JsonElement pearlAngleElement : enderpearlAnglesArray) {
                JsonArray pearlAngle = pearlAngleElement.getAsJsonArray();

                DungeonRooms.checkRoomData();
                enderpearlAngles.add(new Tuple<>(pearlAngle.get(0).getAsFloat(), pearlAngle.get(1).getAsFloat()));
            }

            JsonArray pearlLocations = currentSecretWaypoints.get("enderpearls").getAsJsonArray();
            int index = 0;
            for (JsonElement pearlLocationElement : pearlLocations) {

                JsonArray pearlLocation = pearlLocationElement.getAsJsonArray();

                DungeonRooms.checkRoomData();
                double posX = pearlLocation.get(0).getAsDouble();
                double posY = pearlLocation.get(1).getAsDouble();
                double posZ = pearlLocation.get(2).getAsDouble();

                Triple<Double, Double, Double> positions = MapUtils.relativeToActual(posX, posY, posZ, RoomDetection.roomDirection, RoomDetection.roomCorner);
                posX = positions.getOne() - 0.25;
                posY = positions.getTwo();
                posZ = positions.getThree() - 0.25;
                GuiUtils.drawBoundingBoxAtBlock(new BlockPos(posX, posY, posZ), Color.MAGENTA);
                double yaw = RotationUtils.relativeToActualYaw(enderpearlAngles.get(index).getSecond(), RoomDetection.roomDirection);
                double pitch = enderpearlAngles.get(index).getFirst();

                double yawRadians = Math.toRadians(yaw);
                double pitchRadians = Math.toRadians(pitch);


                double length = 10.0D;
                double x = -Math.sin(yawRadians) * Math.cos(pitchRadians); // (z)
                double y = -Math.sin(pitchRadians); // z
                double z = Math.cos(yawRadians) * Math.cos(pitchRadians); // (x)

                double sideLength = Math.sqrt(x * x + y * y + z * z);
                x /= sideLength;
                y /= sideLength;
                z /= sideLength;


                double newX = posX + x * length + 0.25;
                double newY = posY + y * length + 1.62;
                double newZ = posZ + z * length + 0.25;
                //sendVerboseMessage("Origin: (" + (posX +0.25f)+ ", " + (posY +1.62f) + ", " + posZ +(0.25)+") to End: (" + newX + ", " + newY + ", " + newZ + ") with a angles of ("+yaw+", "+pitch+") -> ("+yawRadians+", "+pitchRadians+")", verboseTAG);
                //SecretRoutesRenderUtils.drawBoxAtBlock(newX, newY, newZ, SRMConfig.enderpearls, 0.03125, 0.03125);
                V2RenderUtils.drawNormalLine(posX + 0.25F, posY + 1.62F, posZ + 0.25F, newX, newY, newZ, Color.RED, event.partialTicks, true, 3);
                enderpearlPositons.add(new Triple<>(posX, posY, posZ));
                index++;
            }
        }
        if (currentSecretWaypoints != null && currentSecretWaypoints.get("secret") != null) {
            JsonObject secret = currentSecretWaypoints.get("secret").getAsJsonObject();
            String type = secret.get("type").getAsString();
            JsonArray location = secret.get("location").getAsJsonArray();

            DungeonRooms.checkRoomData();
            BlockPos pos = MapUtils.relativeToActual(new BlockPos(location.get(0).getAsInt(), location.get(1).getAsInt(), location.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);

            if (type.equals("interact")) {
                if (Configs.DungeonRoutesText) RealRenderUtils.render3dString("§bInteract", pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, 1, event.partialTicks);
                V2RenderUtils.drawPixelBox(new Vec3(pos.getX(), pos.getY(), pos.getZ()), Color.BLUE, 1, event.partialTicks);
            } else if (type.equals("item")) {
                if (Configs.DungeonRoutesText) RealRenderUtils.render3dString("§bItem",pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, 1, event.partialTicks);
                V2RenderUtils.drawPixelBox(new Vec3(pos.getX(), pos.getY(), pos.getZ()), Color.BLUE, 0.5f, event.partialTicks);
            } else if (type.equals("bat")) {
                if (Configs.DungeonRoutesText) RealRenderUtils.render3dString("§bBAT", pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, 1, event.partialTicks);
                V2RenderUtils.drawPixelBox(new Vec3(pos.getX(), pos.getY(), pos.getZ()), Color.BLUE, 0.5f, event.partialTicks);
            }

            if (BadAddons.mc.thePlayer.getPosition().distanceSq(pos.getX(), pos.getY(), pos.getZ()) <= 3 && BadAddons.mc.thePlayer.isSneaking()) {
                BadAddons.currentRoom.nextSecret();
            }

            int ew = 0;
            for (BlockPos etherwarpPos : etherwarpPositions) {
                ew++;
                String text = "§dWARP " + ew;
                if (Configs.DungeonRoutesText) RealRenderUtils.render3dString(text, etherwarpPos.getX() + 0.5, etherwarpPos.getY() + 1.5, etherwarpPos.getZ() + 0.5, 1, 3, event.partialTicks);
            }

            for (BlockPos minePos : minesPositions) {
                if (Configs.DungeonRoutesText) RealRenderUtils.render3dString("§cMINE", minePos.getX() + 0.5, minePos.getY() + 0.5, minePos.getZ() + 0.5, 1, 0.5f, event.partialTicks);
            }


            for (BlockPos superboomPos : superboomsPositions) {
                if (Configs.DungeonRoutesText) RealRenderUtils.render3dString("§cBOOM", superboomPos.getX() + 0.5, superboomPos.getY() + 1, superboomPos.getZ() + 0.5, 1, 2, event.partialTicks);
            }


            for (Triple<Double, Double, Double> enderpearlPos : enderpearlPositons) {
                if (Configs.DungeonRoutesText) RealRenderUtils.render3dString("§2PEARL", enderpearlPos.getOne() + 0.5, enderpearlPos.getTwo() + 1, enderpearlPos.getThree() + 0.5, 1, 2, event.partialTicks);

            }
        }

        // Render the start / end waypoint text
        JsonObject waypoints = currentSecretWaypoints;
        if (waypoints != null && waypoints.get("locations") != null && waypoints.get("locations").getAsJsonArray().size() >0 && waypoints.get("locations").getAsJsonArray().get(0) != null) {
            if (index2 == 0) {
                // First secret in the route (the start)
                JsonArray startCoords = currentSecretWaypoints.get("locations").getAsJsonArray().get(0).getAsJsonArray();

                DungeonRooms.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(startCoords.get(0).getAsInt(), startCoords.get(1).getAsInt(), startCoords.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);

                // Render the text
                Vec3 eyePos = BadAddons.mc.thePlayer.getPositionEyes(event.partialTicks);
                V2RenderUtils.drawNormalLine(eyePos.xCoord, eyePos.yCoord, eyePos.zCoord, pos.getX(), pos.getY(), pos.getZ(), Color.GREEN, event.partialTicks, false, 2);
                RealRenderUtils.render3dString("§aSTART", pos.getX()+0.5, pos.getY(), pos.getZ()+0.5, 1, 2, event.partialTicks);

            }
            if (index2 == BadAddons.currentRoom.currentSecretRoute.getAsJsonArray().size() - 1) {
                JsonObject secret = currentSecretWaypoints.get("secret").getAsJsonObject();
                String type = secret.get("type").getAsString();
                JsonArray location = secret.get("location").getAsJsonArray();

                DungeonRooms.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(location.get(0).getAsInt(), location.get(1).getAsInt(), location.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);
                // Render the text
                RealRenderUtils.render3dString("§4EXIT", pos.getX()+0.5, pos.getY(), pos.getZ()+0.5, 1, 2, event.partialTicks);

            }
        }
        //GlStateManager.enableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (BadAddons.currentRoom == null || !SkyblockUtils.isInDungeon()) return;
        if (e.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            EntityPlayer p = e.entityPlayer;
            BlockPos pos = e.pos;
            Block block = e.world.getBlockState(e.pos).getBlock();

            if (block != Blocks.chest && block != Blocks.trapped_chest && block != Blocks.lever && block != Blocks.skull) {
                return;
            }

            if (BadAddons.currentRoom.getSecretType() == Room.SECRET_TYPES.INTERACT) {
                BlockPos interactPos = BadAddons.currentRoom.getSecretLocation();

                if (pos.getX() == interactPos.getX() && pos.getY() == interactPos.getY() && pos.getZ() == interactPos.getZ()) {
                    BadAddons.currentRoom.nextSecret();
                    ChatLib.debug("Right clicked secret!");
                }
            }
        }
    }

}