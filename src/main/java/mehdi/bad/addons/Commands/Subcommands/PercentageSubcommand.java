package mehdi.bad.addons.Commands.Subcommands;

import mehdi.bad.addons.utils.AuctionUtils;
import mehdi.bad.addons.utils.ChatLib;
import net.minecraft.command.ICommandSender;

public class PercentageSubcommand implements Subcommand {

	@Override
	public String getCommandName() {
		return "percentage";
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
		return "Outputs percentage of a number";
	}

	@Override
	public boolean processCommand(ICommandSender sender, String[] args) {

		if (args.length < 2) {
			ChatLib.chat("§7/bb percentage §b<NUMBER> §e<PERCENTAGE%>");
		} else {
			try {
				double amount = parseAmount(args[0]);
				double percentage = parsePercentage(args[1]);
				double result = amount - (amount * (percentage / 100));

				ChatLib.chat("§7[BA] §6" + AuctionUtils.formatPrice(amount) + " §7- §e" + percentage + "% §7 is §c" + AuctionUtils.formatPrice(result));
			} catch (NumberFormatException e) {
				ChatLib.chat("§cError: Please enter valid numbers.");
			}
		}

		return true;
	}

	private double parseAmount(String arg) {
		double multiplier = 1;
		if (arg.endsWith("k")) {
			multiplier = 1_000;
			arg = arg.substring(0, arg.length() - 1);
		} else if (arg.endsWith("m")) {
			multiplier = 1_000_000;
			arg = arg.substring(0, arg.length() - 1);
		} else if (arg.endsWith("b")) {
			multiplier = 1_000_000_000;
			arg = arg.substring(0, arg.length() - 1);
		}

		return Double.parseDouble(arg) * multiplier;
	}

	private double parsePercentage(String arg) {
		if (arg.contains("%")) return Double.parseDouble(arg.replace("%", ""));
		return Double.parseDouble(arg);
	}

	private String formatAmount(double amount) {
		if (amount >= 1_000_000_000) {
			return String.format("%.2fb", amount / 1_000_000_000);
		} else if (amount >= 1_000_000) {
			return String.format("%.2fm", amount / 1_000_000);
		} else if (amount >= 1_000) {
			return String.format("%.2fk", amount / 1_000);
		} else {
			return String.format("%.2f", amount);
		}
	}


}
