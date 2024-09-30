package mehdi.bad.addons.Commands.Subcommands;

import mehdi.bad.addons.utils.AuctionUtils;
import mehdi.bad.addons.utils.ChatLib;
import net.minecraft.command.ICommandSender;

public class ProfitSubcommand implements Subcommand {

	@Override
	public String getCommandName() {
		return "profit";
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
		return "Calculates the profit of a lowball.";
	}

	@Override
	public boolean processCommand(ICommandSender sender, String[] args) {

		if (args.length < 2) {
			ChatLib.chat("§7/bb profit §a<BUYPRICE> §6<SELLPRICE>");
		} else {
			try {
				double buy = parseAmount(args[0]);
				double sell = parseAmount(args[1]);
				double taxPercentage;

				// Determine the BIN auction tax percentage based on the selling price
				if (sell >= 100000000) {
					taxPercentage = 2.5;  // 2.5% for selling price >= 100,000,000 Coins
				} else if (sell >= 10000000) {
					taxPercentage = 2.0;  // 2% for selling price between 10,000,000 and 99,999,999 Coins
				} else {
					taxPercentage = 1.0;  // 1% for selling price between 1 and 9,999,999 Coins
				}

				double binTax = sell * (taxPercentage / 100);  // BIN auction tax
				double claimingTax = sell * 0.01;              // 1% claiming tax
				double totalTax = binTax + claimingTax;        // Total tax
				double profit = sell - buy - totalTax;         // Net profit after all taxes

				// Display taxes in one line with different colors
				ChatLib.chat("§7[BA] Taxes: §e-" + AuctionUtils.formatPrice(binTax) +
						" §7| Claiming: §7-" + AuctionUtils.formatPrice(claimingTax) +
						" §7| Total: §c-" + AuctionUtils.formatPrice(totalTax));
				ChatLib.chat("§7[BA] Pure Profit: §6+" + AuctionUtils.formatPrice(profit));
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
