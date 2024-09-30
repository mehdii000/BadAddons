package mehdi.bad.addons.Commands;

import java.util.ArrayList;
import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Config.ConfigGuiNew;
import mehdi.bad.addons.utils.ChatLib;
import mehdi.bad.addons.utils.MinecraftUtils;
import mehdi.bad.addons.utils.NBTUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;

public class CommandDevItemData extends CommandBase {
    public String getCommandName() {
        return "devitemdata";
    }
    
    public int getRequiredPermissionLevel() {
        return 0;
    }

    public String getCommandUsage(ICommandSender var1) {
        return this.getUsage();
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (MinecraftUtils.getPlayer().getHeldItem() != null) {
        	ItemStack held = MinecraftUtils.getPlayer().getHeldItem();
        	
        	ChatLib.chat("NAME: §d" + held.getDisplayName());
        	ChatLib.chat("TAGS:" + held.getDisplayName());
        	for (String s : NBTUtils.getAllTags(held)) {
        		ChatLib.chat(" §7-> §r§a" + s);
        	}
        	
        }
    }

    public ArrayList getCommandAliases() {
        ArrayList var1 = new ArrayList();
        var1.add("devid");
        return var1;
    }

    private String getUsage() {
        return "&c§l/devitemdata §r§d[returns the data of the item your holding]";
    }
}
