package mehdi.bad.addons.Features.General;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Config.Config;
import mehdi.bad.addons.Config.Configs;
import mehdi.bad.addons.Events.TickEndEvent;
import mehdi.bad.addons.Objects.NotificationManager;
import mehdi.bad.addons.utils.AuctionUtils;
import mehdi.bad.addons.utils.ChatLib;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeirdFlipper {
	private boolean flipperEta = false;
	public static final ConcurrentHashMap<String, String> skyblockItems = new ConcurrentHashMap<>();
	private long auctionLastUpdateInMillis = 0;
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
	private final Set<String> pastFlips = Collections.synchronizedSet(new HashSet<>());

	@SubscribeEvent
	public void onTick(TickEndEvent e) {
		if (Configs.BedsFlipper) {
			if (!flipperEta) {
				if (executor.isShutdown()) executor = Executors.newScheduledThreadPool(2);
				flipperEta = true;
				NotificationManager.pushNotification("BedsFlipper", "§aEnabled", 2000);
				executor.submit(AuctionUtils::initItemsPrices);
				initFlippingStuff();
			}
		} else if (flipperEta) {
			NotificationManager.pushNotification("BedsFlipper", "§cDisabled", 2000);
			pastFlips.clear();
			auctionLastUpdateInMillis = 0;
			flipperEta = false;
			executor.shutdown();
		}
	}

	private void initFlippingStuff() {
		ChatLib.chat("§7Initiating flipper data...");
		if (skyblockItems.isEmpty()) {
			executor.submit(() -> {
				JsonArray jsonArray = AuctionUtils.getJson("https://api.hypixel.net/resources/skyblock/items").getAsJsonObject().get("items").getAsJsonArray();
				for (JsonElement jsonElement : jsonArray) {
					JsonObject item = jsonElement.getAsJsonObject();
					if (!item.has("category")) {
						continue;
					}
					String name = item.get("name").getAsString();
					String id = item.get("id").getAsString();
					this.skyblockItems.put(name, id);
				}
			});
		}
		executor.submit(() -> {
			JsonObject auctionObject = AuctionUtils.getJson("https://api.hypixel.net/skyblock/auctions").getAsJsonObject();
			auctionLastUpdateInMillis = auctionObject.get("lastUpdated").getAsLong();
			ChatLib.chat("§7Auction api synced!");
			long delayUntilNextUpdate = ((auctionLastUpdateInMillis + 60000) - System.currentTimeMillis());
			if (delayUntilNextUpdate < 0) {
				Configs.BedsFlipper = false;
				Config.save();
				ChatLib.chat("§c[BD) An Error Occurred while syncing! Please re-enable the module.");
				return;
			}
			ChatLib.chat("§7Flips in: §b" + delayUntilNextUpdate / 1000 + "s");

			executor.scheduleAtFixedRate(() -> {
				if (Configs.debugShowScanning) ChatLib.chat("Scanning!");
				bedFlipper(Configs.FlipperType);
			}, delayUntilNextUpdate, 10000, TimeUnit.MILLISECONDS);
		});
	}

	private JsonArray getActiveAuctions() {
		return AuctionUtils.getJson("https://api.hypixel.net/v2/skyblock/auctions").getAsJsonObject().get("auctions").getAsJsonArray();
	}

	private void bedFlipper(int type) {
		switch (type) {
			case 0:
				executor.submit(this::processAllBedsFlips);
				break;
			case 1:
				executor.submit(this::processOnlyProfitableBedsFlips);
				break;
			case 2:
				executor.submit(this::processWhitelistedBeds);
				break;
			case 3:
				executor.submit(this::processWhitelistedBedsWithProfit);
				break;
		}
	}

	private void processWhitelistedBeds() {
		getActiveAuctions().forEach(auctionElement -> {
			final JsonObject auction = auctionElement.getAsJsonObject();
			String uuid = auction.get("uuid").getAsString();
			if (pastFlips.contains(uuid)) {
				return;
			}
			if (auction.get("start").getAsLong() + (1000L * Configs.FlipperMinTime) > System.currentTimeMillis() && auction.get("bin").getAsBoolean()) {
				String name = StringUtils.stripControlCodes(auction.get("item_name").getAsString());
				WhitelistedBedsConfig.whitelistedBeds.entrySet().forEach(key -> {
					if (name.contains(key.getKey())) {
						String lore = auction.get("item_lore").getAsString();
						if (Configs.BedsFlipperLoreCheck && lore.contains(key.getValue())) {
							String seller = AuctionUtils.getPlayerNameFromUUID(auction.get("auctioneer").getAsString());
							double price = auction.get("starting_bid").getAsDouble();
							ding();
							ChatLib.sendHoverMessageExtra("§6[LBED] §e" + name + " §6[" + AuctionUtils.formatPrice(price) + "]", "/viewauction " + uuid, lore, "§b" + seller + " §7/ KEYWORD: §b" + key.getValue(), "/ah " + seller);
							pastFlips.add(uuid);
							return;
						}
						String seller = AuctionUtils.getPlayerNameFromUUID(auction.get("auctioneer").getAsString());
						double price = auction.get("starting_bid").getAsDouble();
						ding();
						ChatLib.sendHoverMessageExtra("[W-BED] §e" + name + " §6[" + AuctionUtils.formatPrice(price) + "]", "/viewauction " + uuid, lore, "§b" + seller, "/ah " + seller);
						pastFlips.add(uuid);
					}
				});
			}
		});
	}

	private void processWhitelistedBedsWithProfit() {
		getActiveAuctions().forEach(auctionElement -> {
			final JsonObject auction = auctionElement.getAsJsonObject();
			String uuid = auction.get("uuid").getAsString();
			if (pastFlips.contains(uuid)) {
				return;
			}
			if (auction.get("start").getAsLong() + (1000L * Configs.FlipperMinTime) > System.currentTimeMillis() && auction.get("bin").getAsBoolean()) {
				String name = StringUtils.stripControlCodes(auction.get("item_name").getAsString());
				WhitelistedBedsConfig.whitelistedBeds.keySet().forEach(key -> {
					if (name.contains(key)) {
						String seller = AuctionUtils.getPlayerNameFromUUID(auction.get("auctioneer").getAsString());
						double price = auction.get("starting_bid").getAsDouble();
						String id = this.skyblockItems.get(name);
						String lore = auction.get("item_lore").getAsString();
						double binPrice = AuctionUtils.getPrice(id, price);
						if (price < binPrice - (500000*Configs.FlipperMinProfit)) {
							ChatLib.sendHoverMessageExtra("§4[BED] §e" + name + " §6[+" + AuctionUtils.formatPrice(binPrice-price) + "]", "/viewauction " + uuid, lore, "§b" + seller, "/ah " + seller);
							ding();
							pastFlips.add(uuid);
						}
					}
				});
			}
		});
	}

	private void processAllBedsFlips() {
		getActiveAuctions().forEach(auctionElement -> {
			final JsonObject auction = auctionElement.getAsJsonObject();
			String uuid = auction.get("uuid").getAsString();
			if (this.pastFlips.contains(uuid)) {
				return;
			}
			if (auction.get("start").getAsLong() + (1000L*Configs.FlipperMinTime) > System.currentTimeMillis() && auction.get("bin").getAsBoolean()) {
				String seller = AuctionUtils.getPlayerNameFromUUID(auction.get("auctioneer").getAsString());
				String name = auction.get("item_name").getAsString();
				String lore = auction.get("item_lore").getAsString();
				double price = auction.get("starting_bid").getAsDouble();
				ChatLib.sendHoverMessageExtra("[BED] §e" + name + " §6[" + AuctionUtils.formatPrice(price) + "]", "/viewauction " + uuid, lore, "§b" + seller, "/ah " + seller);
				ding();
				this.pastFlips.add(uuid);
			}
		});
	}

	private void processOnlyProfitableBedsFlips() {
		getActiveAuctions().forEach(auctionElement -> {
			final JsonObject auction = auctionElement.getAsJsonObject();
			String uuid = auction.get("uuid").getAsString();
			if (this.pastFlips.contains(uuid)) {
				return;
			}
			if (auction.get("start").getAsLong() + (1000L*Configs.FlipperMinTime) > System.currentTimeMillis() && auction.get("bin").getAsBoolean()) {
				String seller = AuctionUtils.getPlayerNameFromUUID(auction.get("auctioneer").getAsString());
				String lore = auction.get("item_lore").getAsString();
				String name = auction.get("item_name").getAsString();
				double price = auction.get("starting_bid").getAsDouble();
				String id = this.skyblockItems.get(name);
				double binPrice = AuctionUtils.getPrice(id, price);
				if (price < binPrice - (500000*Configs.FlipperMinProfit)) {
					ChatLib.sendHoverMessageExtra("[PBED] §e" + name + " §6[+" + AuctionUtils.formatPrice(binPrice-price) + "]", "/viewauction " + uuid, lore, "§b" + seller, "/ah " + seller);
					ding();
					this.pastFlips.add(uuid);
				}
			}
		});
	}

	private void ding() {
		BadAddons.mc.thePlayer.playSound("random.fizz", 0.8F, 1.1F);
	}

}