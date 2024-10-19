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
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class OnItemPickedUp {
    public static boolean itemSecretOnCooldown = false; // True: do not add item secret waypoint, False: add item secret waypoint

    @SubscribeEvent
    public void onPickupItem(PlayerEvent.ItemPickupEvent e) {
        if (BadAddons.currentRoom == null || !SkyblockUtils.isInDungeon()) return;
        if(BadAddons.currentRoom.getSecretType() == Room.SECRET_TYPES.ITEM) {
            BlockPos pos = e.player.getPosition();
            BlockPos itemPos = BadAddons.currentRoom.getSecretLocation();

            if (pos.getX() >= itemPos.getX() - 10 && pos.getX() <= itemPos.getX() + 10 && pos.getY() >= itemPos.getY() - 10 && pos.getY() <= itemPos.getY() + 10 && pos.getZ() >= itemPos.getZ() - 10 && pos.getZ() <= itemPos.getZ() + 10) {
                BadAddons.currentRoom.nextSecret();
                ChatLib.debug("picked up item secret!");
            }
        }
    }
}