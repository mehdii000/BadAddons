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

package mehdi.bad.addons.Features.Dungeons.utils;

import mehdi.bad.addons.Features.Dungeons.DungeonRooms;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.InflaterInputStream;

public class DungeonUtils {
    public static boolean dungeonOverride = false;

    /**
     * @return List of the paths to every .skeleton room data file
     */
    public static List<Path> getAllPaths (String folderName) {
        List<Path> paths = new ArrayList<>();
        try {
            URI uri = DungeonRooms.class.getResource("/assets/roomdetection/" + folderName).toURI();
            Path Path;
            FileSystem fileSystem = null;
            if (uri.getScheme().equals("jar")) {
                fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                Path = fileSystem.getPath("/assets/roomdetection/" + folderName);
            } else {
                Path = Paths.get(uri);
            }
            Stream<Path> walk = Files.walk(Path, 3);
            for (Iterator<Path> it = walk.iterator(); it.hasNext();) {
                Path path = it.next();
                String name = path.getFileName().toString();
                if (name.endsWith(".skeleton")) paths.add(path);
            }
            if (fileSystem != null) fileSystem.close();
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return paths;
    }

    /**
     * Converts the .skeleton files into a readable format.
     * @return room data as a hashmap
     */
    public static HashMap<String, long[]> pathsToRoomData (String parentFolder, List<Path> allPaths) {
        HashMap<String, long[]> allRoomData = new HashMap<>();
        try {
            for (Path path : allPaths) {
                if (!path.getParent().getFileName().toString().equals(parentFolder)) continue;
                String name = path.getFileName().toString();
                InputStream input = DungeonRooms.class.getResourceAsStream(path.toString());
                ObjectInputStream data = new ObjectInputStream(new InflaterInputStream(input));
                long[] roomData = (long[]) data.readObject();
                allRoomData.put(name.substring(0, name.length() - 9), roomData);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return allRoomData;
    }

    /**
     * Packs block info into a single 8 byte primitive long. Normally, first pair of bytes will be x coordinate, second
     * pair will be y coordinate, third pair will be z coordinate, and last pair will be block id and metadata.
     * @return primitive long containing block info
     */
    public static long shortToLong(short a, short b, short c, short d) {
        return ((long)((a << 16) | (b & 0xFFFF)) << 32) | (((c << 16) | (d & 0xFFFF)) & 0xFFFFFFFFL);
    }

    /**
     * @return Array of four shorts containing the values stored in the long
     */
    public static short[] longToShort(long l) {
        return new short[]{(short) (l >> 48), (short) (l >> 32), (short) (l >> 16), (short) (l)};
    }
}
