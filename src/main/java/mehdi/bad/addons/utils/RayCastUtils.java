/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.BlockPos
 *  net.minecraft.util.Vec3
 *  org.lwjgl.util.vector.Vector3f
 */
package mehdi.bad.addons.utils;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import mehdi.bad.addons.BadAddons;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class RayCastUtils {

    public static <T extends Entity> List<T> getFacedEntityOfType(Class<T> _class, float range) {
        float stepSize = 0.5f;
        if (BadAddons.mc.thePlayer != null && BadAddons.mc.theWorld != null) {
            Vector3f position = new Vector3f((float)BadAddons.mc.thePlayer.posX, (float)BadAddons.mc.thePlayer.posY + BadAddons.mc.thePlayer.getEyeHeight(), (float)BadAddons.mc.thePlayer.posZ);
            Vec3 look = BadAddons.mc.thePlayer.getLook(0.0f);
            Vector3f step = new Vector3f((float)look.xCoord, (float)look.yCoord, (float)look.zCoord);
            step.scale(stepSize / step.length());
            int i = 0;
            while ((double)i < Math.floor(range / stepSize) - 2.0) {
                List entities = BadAddons.mc.theWorld.getEntitiesWithinAABB(_class, new AxisAlignedBB((double)position.x - 0.5, (double)position.y - 0.5, (double)position.z - 0.5, (double)position.x + 0.5, (double)position.y + 0.5, (double)position.z + 0.5));
                if (!entities.isEmpty()) {
                    return entities;
                }
                position.translate(step.x, step.y, step.z);
                ++i;
            }
        }
        return null;
    }
}

