package mehdi.bad.addons.utils;

import java.util.ArrayList;
import java.util.List;

import mehdi.bad.addons.BadAddons;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTUtils {
    public static boolean getBooleanFromExtraAttributes(ItemStack var0, String var1) {
        NBTTagCompound var2 = getExtraAttributes(var0);
        return var2 != null && var2.hasKey(var1) && var2.getBoolean(var1);
    }

    public static int getIntFromExtraAttributes(ItemStack var0, String var1) {
        NBTTagCompound var2 = getExtraAttributes(var0);
        return var2 != null && var2.hasKey(var1) ? var2.getInteger(var1) : 0;
    }

    public static NBTTagCompound getCompoundFromExtraAttributes(ItemStack var0, String var1) {
        NBTTagCompound var2 = getExtraAttributes(var0);
        return var2 != null && var2.hasKey(var1) ? var2.getCompoundTag(var1) : null;
    }

    private static NBTTagCompound getExtraAttributes(ItemStack var0) {
        return var0 == null ? null : var0.getSubCompound("ExtraAttributes", false);
    }

    public static boolean isBookUltimateFromName(String var0) {
        return var0.contains("§d§l");
    }
    
    public static boolean hasDesiredLore(ItemStack itemStack, String desiredLore) {
        if (itemStack.hasTagCompound()) {
            NBTTagCompound tagCompound = itemStack.getTagCompound();
            if (tagCompound.hasKey("display", 10)) {
                NBTTagCompound displayTag = tagCompound.getCompoundTag("display");
                if (displayTag.hasKey("Lore", 9)) {
                    NBTTagList loreList = displayTag.getTagList("Lore", 8);
                    List<String> loreLines = new ArrayList<>();
                    for (int i = 0; i < loreList.tagCount(); i++) {
                        String lore = loreList.getStringTagAt(i);
                        loreLines.add(lore);
                    }
                    for (String lore : loreLines) {
                        if (lore.contains(desiredLore)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean isItemRecombobulated(ItemStack var0) {
        return getIntFromExtraAttributes(var0, "rarity_upgrades") == 1;
    }

    public static List getLore(ItemStack var0) {
        return var0 == null ? new ArrayList() : var0.getTooltip(BadAddons.mc.thePlayer, BadAddons.mc.gameSettings.advancedItemTooltips);
    }

    public static boolean isItemFullQuality(ItemStack var0) {
        return getIntFromExtraAttributes(var0, "baseStatBoostPercentage") == 50;
    }

    public static String getStringFromExtraAttributes(ItemStack var0, String var1) {
        NBTTagCompound var2 = getExtraAttributes(var0);
        return var2 != null && var2.hasKey(var1) ? var2.getString(var1) : "";
    }
    
    public static List<String> getAllTags(ItemStack stack) {
        List<String> tags = new ArrayList<String>();
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            return tags;
        }
        getAllTags("", nbt, tags);
        return tags;
    }

    private static void getAllTags(String prefix, NBTTagCompound nbt, List<String> tags) {
        for (String key : nbt.getKeySet()) {
            NBTBase tag = nbt.getTag(key);
            if (tag instanceof NBTTagCompound) {
                getAllTags(prefix + key + ".", (NBTTagCompound) tag, tags);
            } else {
                tags.add(prefix + key + " | " + tag.toString());
            }
        }
    } 
    
    public static boolean isBookUltimate(ItemStack var0) {
        try {
            return isBookUltimateFromName((String) getLore(var0).get(1));
        } catch (Exception var2) {
            return false;
        }
    }

    public static String getUUID(ItemStack var0) {
        return getStringFromExtraAttributes(var0, "uuid");
    }

    public static String getSkyBlockID(ItemStack var0) {
        return getStringFromExtraAttributes(var0, "id");
    }

    public static boolean isItemStarred(ItemStack var0) {
        return getIntFromExtraAttributes(var0, "dungeon_item_level") != 0;
    }
}
