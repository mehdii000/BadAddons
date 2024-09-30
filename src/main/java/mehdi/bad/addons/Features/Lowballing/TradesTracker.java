package mehdi.bad.addons.Features.Lowballing;

import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.Config.Configs;
import mehdi.bad.addons.Features.General.WeirdFlipper;
import mehdi.bad.addons.utils.AuctionUtils;
import mehdi.bad.addons.utils.GuiUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class TradesTracker {

    public static String currentTraderUnformmated = "NaN";
    public static List<ItemStack> currentSentItems = new ArrayList<>();
    public static List<ItemStack> currentReceivedItems = new ArrayList<>();

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onInventoryRendering(GuiScreenEvent.DrawScreenEvent.Post post) {
        if (!Configs.TrackTrades) return;
        if (guiIsNotTradeMenu(post.gui)) return;
        if (!(post.gui instanceof GuiChest)) return;

        ContainerChest chest = (ContainerChest) ((GuiChest) post.gui).inventorySlots;
        BadAddons.mc.fontRendererObj.drawStringWithShadow(
                "§aYou are in a trade menu! with? §3" +
                        currentTraderUnformmated,
                16,
                16,
                0xFFFFFF // Assuming white color, adjust as needed
        );

        // Draw sent and received items
        currentSentItems = GuiUtils.getSentItemsFromTrade(chest);
        currentReceivedItems = GuiUtils.getReceivedItemsFromTrade(chest);

        GuiUtils.drawListString(16, 32, "§7Sent Items:", currentSentItems);
        GuiUtils.drawListString(16, 32 + 16 + (currentSentItems.size() * 20), "§7Received Items:", currentReceivedItems);

    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!Configs.TrackTrades) return;
        if (guiIsNotTradeMenu(BadAddons.mc.currentScreen)) return;
        ContainerChest chest = (ContainerChest) ((GuiChest) BadAddons.mc.currentScreen).inventorySlots;
        currentTraderUnformmated = chest.getLowerChestInventory().getName().split("You")[1].replace(" ", "");
    }

    public final String TRADE_COMPLETED_REGEX = "Trade completed with ";
    @SubscribeEvent
    public void onChatStuff(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();
        if (message.contains(TRADE_COMPLETED_REGEX) && Configs.TrackTrades) {
            sendTradeToDatabase(BadAddons.mc.getSession().getUsername(), currentTraderUnformmated, currentSentItems, currentReceivedItems);
        }
    }

    private boolean guiIsNotTradeMenu(GuiScreen gui) {
        if (gui instanceof GuiChest) {
            ContainerChest chest = (ContainerChest) ((GuiChest) gui).inventorySlots;
            if (chest != null) {
                String title = chest.getLowerChestInventory().getName();
                return !title.contains("You") || chest.inventorySlots.size() != 81;
            }
        }
        return true;
    }

    /*public void sendTradeToDatabase(String buyer, String seller, List<ItemStack> sentItems, List<ItemStack> receivedItems) {

        try {
            final HttpsURLConnection connection = (HttpsURLConnection) new URL(Configs.TradesWebhook).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11");
            connection.setDoOutput(true);
            try (final OutputStream outputStream = connection.getOutputStream()) {

                AtomicReference<String> sent = new AtomicReference<>("");
                AtomicReference<String> received = new AtomicReference<>("");
                sentItems.forEach(i -> sent.set(sent.get() + "- " + i.getDisplayName() + "\\n"));
                receivedItems.forEach(i -> received.set(received.get() + "- " + i.getDisplayName() + "\\n"));

                outputStream.write(("{ \"content\": \"Trade Completed!\", \"embeds\": [ { \"title\": \"" + buyer + "  :arrow_right:  "+ seller + "\", \"description\": \":red_square: **SENT:**\\n" + sent.get() + "\\n\\n:green_square: **RECEIVED:**\\n" + received.get() + "\", \"color\": null, \"fields\": [ { \"name\": \":money_with_wings: Estimated Sent Items:\", \"value\": \"####### coins\" }, { \"name\": \":money_mouth: Estimated Received Items:\", \"value\": \"####### coins\" } ], \"author\": { \"name\": \"At: 8/19/24 | 4:23PM\" } } ], \"username\": \"TadesBot\", \"attachments\": [] }").getBytes(StandardCharsets.UTF_8));
            }
            connection.getInputStream();
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }*/

    private static final ExecutorService executor = Executors.newFixedThreadPool(2);
    public void sendTradeToDatabase(String buyer, String seller, List<ItemStack> sentItems, List<ItemStack> receivedItems) {
        executor.submit(() -> {
            HttpURLConnection connection = null;
            OutputStream outputStream = null;

            try {
                URL url = new URL(Configs.TradesWebhook);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11");
                connection.setDoOutput(true);

                StringBuilder sent = new StringBuilder();
                StringBuilder received = new StringBuilder();

                AtomicInteger sentValue = new AtomicInteger(0);
                AtomicInteger receivedValue = new AtomicInteger(0);

                for (ItemStack item : sentItems) {
                    String name = StringUtils.stripControlCodes(item.getDisplayName());
                    sent.append("- ").append(name).append("\\n");
                    sentValue.getAndAdd(WeirdFlipper.skyblockItems.containsValue(name) ? (int) AuctionUtils.getPrice(WeirdFlipper.skyblockItems.get(name), 1) : 0);
                }
                if (sentItems.isEmpty()) sent.append("- Nothing!").append("\\n");

                for (ItemStack item : receivedItems) {
                    String name = StringUtils.stripControlCodes(item.getDisplayName());
                    received.append("- ").append(name).append("\\n");
                    receivedValue.getAndAdd(WeirdFlipper.skyblockItems.containsValue(name) ? (int) AuctionUtils.getPrice(WeirdFlipper.skyblockItems.get(name), 1) : 0);
                }
                if (receivedItems.isEmpty()) received.append("- Nothing!").append("\\n");


                // Get current timestamp
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy | h:mma");
                String timestamp = LocalDateTime.now().format(formatter);

                String jsonPayload = String.format(
                        "{"
                                + "\"content\": \"Trade Completed!\","
                                + "\"embeds\": ["
                                + "  {"
                                + "    \"title\": \"%s  :arrow_right:  %s\","
                                + "    \"description\": \":red_square: **SENT:**\\n%s\\n\\n:green_square: **RECEIVED:**\\n%s\","
                                + "    \"color\": 15986300,"
                                + "    \"author\": {\"name\": \"At: %s\"}"
                                + "  }"
                                + "],"
                                + "\"username\": \"TradesBot\","
                                + "\"attachments\": []"
                                + "}",
                        buyer, seller, sent.toString(), received.toString(), timestamp
                );

                outputStream = connection.getOutputStream();
                outputStream.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();

                // Read the response (optional)
                connection.getInputStream().close();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
