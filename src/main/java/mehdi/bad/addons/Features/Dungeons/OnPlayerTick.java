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

import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Features.Dungeons.utils.Room;
import mehdi.bad.addons.utils.ChatLib;
import mehdi.bad.addons.utils.SkyblockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class OnPlayerTick {
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (BadAddons.currentRoom == null || !SkyblockUtils.isInDungeon()) return;
        if(e.player.getUniqueID() != Minecraft.getMinecraft().thePlayer.getUniqueID()) {
            return;
        }

        //If all secrets in the room have been completed
        /*if(Waypoints.allFound) {
            Main.currentRoom = new Room(null);

        }*/

        BadAddons.currentRoom.renderLines();

        if(BadAddons.currentRoom.getSecretType() == Room.SECRET_TYPES.BAT) {
            BlockPos pos = e.player.getPosition();
            BlockPos batPos = BadAddons.currentRoom.getSecretLocation();

            if (pos.getX() >= batPos.getX() - 3 && pos.getX() <= batPos.getX() + 3 && pos.getY() >= batPos.getY() - 3 && pos.getY() <= batPos.getY() + 3 && pos.getZ() >= batPos.getZ() - 3 && pos.getZ() <= batPos.getZ() + 3) {
                BadAddons.currentRoom.nextSecret();
                ChatLib.debug("went by bat secret!");
            }
        }

        /* This has been commented out because it is causing it to log the secret multiple times if there are 2 secrets in a row in the route.
        This was originally added because in a specific room, you cannot get the item secret if it spawns and the velocity pushes the item away from you.
        But you can still get the secret if you walk over to the item secret, or just press the next secret keybind if you're lazy.
        */
        if(BadAddons.currentRoom.getSecretType() == Room.SECRET_TYPES.ITEM) {
            BlockPos pos = e.player.getPosition();
            BlockPos itemPos = BadAddons.currentRoom.getSecretLocation();

            if (pos.getX() >= itemPos.getX() - 2 && pos.getX() <= itemPos.getX() + 2 && pos.getY() >= itemPos.getY() - 2 && pos.getY() <= itemPos.getY() + 2 && pos.getZ() >= itemPos.getZ() - 2 && pos.getZ() <= itemPos.getZ() + 2) {
                new Thread(() -> {
                    try {

                        Thread.sleep(1500);
                        if (BadAddons.currentRoom.getSecretType() == Room.SECRET_TYPES.ITEM && itemPos.getX() == BadAddons.currentRoom.getSecretLocation().getX() && itemPos.getY() == BadAddons.currentRoom.getSecretLocation().getY() && itemPos.getZ() == BadAddons.currentRoom.getSecretLocation().getZ()) {
                            BadAddons.currentRoom.nextSecret();
                            ChatLib.debug("picked up item secret lazyway!");
                        }
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }).start();
            }
        }
    }
}