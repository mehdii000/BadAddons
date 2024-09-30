package mehdi.bad.addons.Commands;

import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Commands.Subcommands.Subcommand;
import mehdi.bad.addons.Config.ConfigGuiNew;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class BadCommand extends CommandBase {
	
	private Subcommand[] subcommands;
	
	public BadCommand(Subcommand[] subcommands) {
		this.subcommands = subcommands;
	}
	
	@Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
	
    public String getCommandName() {
        return "badaddons";
    }
    
    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("bad", "ba", "bb");
    }
    
    public int getRequiredPermissionLevel() {
        return 0;
    }

    public String getCommandUsage(ICommandSender var1) {
        return "/badaddons <subcommand> <arguments>";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            BadAddons.guiToOpen = new ConfigGuiNew(BadAddons.mc.gameSettings.guiScale);
            return;
        }

        Subcommand closestMatch = null;
        int closestDistance = 5;

        for (Subcommand subcommand : this.subcommands) {
            if (Objects.equals(args[0], subcommand.getCommandName())) {
                try {
                    if (!subcommand.processCommand(sender, Arrays.copyOfRange(args, 1, args.length))) {
                        sender.addChatMessage(new ChatComponentText("§cInvalid subcommand!"));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            }

            int distance = computeLevenshteinDistance(args[0], subcommand.getCommandName());
            if (distance < closestDistance) {
                closestDistance = distance;
                closestMatch = subcommand;
            }
        }

        if (closestMatch != null) {
            String usedAlias = getCommandName();
            sender.addChatMessage(new ChatComponentText("§cSubcommand not found. Did you mean §a/" + usedAlias + " " + closestMatch.getCommandName() + " ?"));
        } else {
            sender.addChatMessage(new ChatComponentText("§cSubcommand not found!"));
        }
    }
    
    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        List<String> possibilities = new LinkedList<>();
        for (Subcommand subcommand : subcommands) {
            possibilities.add(subcommand.getCommandName());
        }
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, possibilities);
        }
        return null;
    }
    
    public int computeLevenshteinDistance(String str1, String str2) {
        int[][] distance = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++) {
            distance[i][0] = i;
        }

        for (int j = 0; j <= str2.length(); j++) {
            distance[0][j] = j;
        }

        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                int cost = str1.charAt(i - 1) == str2.charAt(j - 1) ? 0 : 1;
                distance[i][j] = Math.min(Math.min(distance[i - 1][j] + 1, distance[i][j - 1] + 1),
                        distance[i - 1][j - 1] + cost);
            }
        }

        return distance[str1.length()][str2.length()];
    }
    
}
