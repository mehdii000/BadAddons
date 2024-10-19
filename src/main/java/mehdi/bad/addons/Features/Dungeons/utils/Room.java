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

package mehdi.bad.addons.Features.Dungeons.utils;

import com.google.gson.*;
import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Config.Configs;
import mehdi.bad.addons.Features.Dungeons.DungeonRooms;
import mehdi.bad.addons.Features.Dungeons.catacombs.RoomDetection;
import mehdi.bad.addons.utils.V2RenderUtils;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;

import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

public class Room {
    int c = 0;
    public enum WAYPOINT_TYPES {
        LOCATIONS,
        ETHERWARPS,
        MINES,
        INTERACTS,
        TNTS,
        ENDERPEARLS,
    };
    public enum SECRET_TYPES {
        INTERACT,
        ITEM,
        BAT,
        EXITROUTE
    };
    public String name;
    public JsonArray currentSecretRoute;
    public int currentSecretIndex = 0;
    public JsonObject currentSecretWaypoints;
    public boolean roomDone = false;

    public Room(String roomName) {
        currentSecretIndex = 0;

        try {
            name = roomName;

            if (roomName != null) {
                String filePath;
                filePath = BadAddons.ROUTES_PATH + File.separator + "routes.json";

                Gson gson = new GsonBuilder().create();
                FileReader reader = new FileReader(filePath);

                JsonObject data = gson.fromJson(reader, JsonObject.class);

                if(data != null && data.get(name) != null) {
                    currentSecretRoute = data.get(name).getAsJsonArray();
                    currentSecretWaypoints = currentSecretRoute.get(currentSecretIndex).getAsJsonObject();
                }
            } else {
                currentSecretRoute = null;
            }
        } catch(Exception e) {
           e.printStackTrace();
        }
    }

    public Room(String roomName, String filePath) {
        currentSecretIndex = 0;

        try {
            name = roomName;

            if (roomName != null) {
                Gson gson = new GsonBuilder().create();
                FileReader reader = new FileReader(filePath);

                JsonObject data = gson.fromJson(reader, JsonObject.class);

                if(data != null && data.get(name) != null) {
                    currentSecretRoute = data.get(name).getAsJsonArray();
                    currentSecretWaypoints = currentSecretRoute.get(currentSecretIndex).getAsJsonObject();
                }
            } else {
                currentSecretRoute = null;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void lastSecretKeybind() {

        if(currentSecretIndex > 0) {
            currentSecretIndex--;
        }

        if(!(currentSecretIndex >= currentSecretRoute.size())) {
            currentSecretWaypoints = currentSecretRoute.get(currentSecretIndex).getAsJsonObject();
        } else {
            roomDone = true;
            currentSecretWaypoints = null;
        }
    }

    public void nextSecret() {
        currentSecretIndex++;

        if(!(currentSecretIndex >= currentSecretRoute.size())) {
            currentSecretWaypoints = currentSecretRoute.get(currentSecretIndex).getAsJsonObject();
        } else {
            currentSecretWaypoints = null;
        }
    }

    public void nextSecretKeybind() {
        if(currentSecretRoute != null) {
            if(currentSecretIndex < currentSecretRoute.size() - 1) {
                currentSecretIndex++;
            }

            if (!(currentSecretIndex >= currentSecretRoute.size())) {
                currentSecretWaypoints = currentSecretRoute.get(currentSecretIndex).getAsJsonObject();
            } else {
                currentSecretWaypoints = null;
            }
        }
    }




    public SECRET_TYPES getSecretType() {
        if(currentSecretWaypoints != null && currentSecretWaypoints.get("secret") != null && currentSecretWaypoints.get("secret").getAsJsonObject().get("type") != null) {
            String type = currentSecretWaypoints.get("secret").getAsJsonObject().get("type").getAsString();
            if(type.equals("interact")) {
                return SECRET_TYPES.INTERACT;
            } else if(type.equals("item")) {
                return SECRET_TYPES.ITEM;
            } else if(type.equals("bat")) {
                return SECRET_TYPES.BAT;
            } else if(type.equals("exitroute")) {
                return SECRET_TYPES.EXITROUTE;
            }
        }

        return null;
    }

    public BlockPos getSecretLocation() {
        if(currentSecretWaypoints.get("secret") != null && currentSecretWaypoints.get("secret").getAsJsonObject().get("location") != null) {
            JsonArray location = currentSecretWaypoints.get("secret").getAsJsonObject().get("location").getAsJsonArray();

            DungeonRooms.checkRoomData();
            return MapUtils.relativeToActual(new BlockPos(location.get(0).getAsInt(), location.get(1).getAsInt(), location.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);
        }
        return null;
    }

    public void renderLines() {
        if(currentSecretWaypoints != null && currentSecretWaypoints.get("locations") != null) {
            // Render the lines
            List<BlockPos> lines = new LinkedList<>();

            JsonArray lineLocations = currentSecretWaypoints.get("locations").getAsJsonArray();
            for (JsonElement lineLocationElement : lineLocations) {
                JsonArray lineLocation = lineLocationElement.getAsJsonArray();

                DungeonRooms.checkRoomData();
                lines.add(MapUtils.relativeToActual(new BlockPos(lineLocation.get(0).getAsInt(), lineLocation.get(1).getAsInt(), lineLocation.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner));
            }

            if(Configs.DungeonRoutesType == 0) {
                //Add tick delay
                if(c<10){
                    c++;
                    return;
                }
                c = 0;
                int particleType = 26;
                try{
                    V2RenderUtils.drawLineMultipleParticles(EnumParticleTypes.getParticleFromId(particleType), lines);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}