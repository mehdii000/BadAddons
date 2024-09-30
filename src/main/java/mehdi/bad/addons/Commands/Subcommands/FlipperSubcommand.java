package mehdi.bad.addons.Commands.Subcommands;

import mehdi.bad.addons.Features.General.ReplacedWordsConfig;
import mehdi.bad.addons.Features.General.WhitelistedBedsConfig;
import mehdi.bad.addons.utils.ChatLib;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
import java.util.Map;

public class FlipperSubcommand implements Subcommand {

	@Override
	public String getCommandName() {
		return "flipper";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public String getCommandUsage() {
		return "";
	}

	@Override
	public String getCommandDescription() {
		return "Opens setting menu for beds flipper";
	}

	@Override
	public boolean processCommand(ICommandSender sender, String[] args) {
		ReplacedWordsConfig.load();
		if (args.length == 0) {
			// Display help message when no arguments are provided
			ChatLib.chat("§b/bb flipper §aadd §7<ITEM_NAME>");
			ChatLib.chat("§b/bb flipper §cdelete §7<ITEM_NAME>");
			ChatLib.chat("§b/bb flipper list");
		} else if (args.length == 1 && args[0].equals("list")) {
			// Display a list of replaced words
			ChatLib.chat("§6WHITELISTED ITEMS:");
			for (Map.Entry<String, String> entry : WhitelistedBedsConfig.whitelistedBeds.entrySet()) {
				ChatLib.chat("§b" + entry.getKey() + "§7 || LoreCheck: §b" + entry.getValue());
			}
		} else if (args.length >= 2) {
			if (args[0].equals("delete")) {
				// Delete a word from the HashMap, join the arguments to create the key
				String key = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
				if (WhitelistedBedsConfig.whitelistedBeds.containsKey(key)) {
					WhitelistedBedsConfig.whitelistedBeds.remove(key);
					WhitelistedBedsConfig.save();
					ChatLib.chat("§cDeleted entry with key: §e" + key);
				} else {
					ChatLib.chat("§cEntry with key §e\"" + key + "\" §cnot found.");
				}
			} else if (args[0].equals("add")) {
				String key = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
				if (WhitelistedBedsConfig.whitelistedBeds.containsKey(key)) {
					ChatLib.chat("§7Item already exists with key: §b" + key);
				} else {
					WhitelistedBedsConfig.whitelistedBeds.put(key, "");
					WhitelistedBedsConfig.save();
					ChatLib.chat("§7Whitelisted item: §b" + key);
				}
			}
		} else {
			ChatLib.chat("§b/bb flipper §a<ITEM_NAME>");
		}

		return true;

	}
	
}
