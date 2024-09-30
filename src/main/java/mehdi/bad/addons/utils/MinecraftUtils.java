package mehdi.bad.addons.utils;

import mehdi.bad.addons.BadAddons;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class MinecraftUtils {

    public static World getWorld() {
        return BadAddons.mc.theWorld;
    }

    public static NetworkPlayerInfo getPlayerInfo(String var0) {
        EntityPlayer var1 = getWorld().getPlayerEntityByName(var0);
        return var1 == null ? null : BadAddons.mc.getNetHandler().getPlayerInfo(var0);
    }

    public static String[] fetchTabList() {
        if (BadAddons.mc.thePlayer == null || BadAddons.mc.thePlayer.sendQueue == null) return new String[]{""};
        List<String> names = new ArrayList<>();
        for (NetworkPlayerInfo info : BadAddons.mc.thePlayer.sendQueue.getPlayerInfoMap()) {
            String name = BadAddons.mc.ingameGUI.getTabList().getPlayerName(info);
            if (name != null && !name.isEmpty()) {
                names.add(name);
            }
        }
        return names.toArray(new String[0]);
    }

    public static EntityPlayerSP getPlayer() {
        return BadAddons.mc.thePlayer;
    }
    
}
