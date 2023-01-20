package us.mytheria.bloblib.entities.inventory;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import us.mytheria.bloblib.BlobLibAPI;
import us.mytheria.bloblib.BlobLibDevAPI;
import us.mytheria.bloblib.entities.message.ReferenceBlobMessage;

import java.util.Optional;
import java.util.function.Function;

public class ObjectBuilderButtonBuilder {

    public static ObjectBuilderButton<String> STRING(String buttonKey, long timeout,
                                                     String timeoutMessageKey,
                                                     String timerMessageKey,
                                                     Function<String, Boolean> function) {
        return new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibDevAPI.addChatListener(player, timeout,
                        string -> button.set(string, function),
                        timeoutMessageKey,
                        timerMessageKey)) {
        };
    }

    public static ObjectBuilderButton<String> SIMPLE_STRING(String buttonKey, long timeout,
                                                            String timeoutMessageKey,
                                                            String timerMessageKey) {
        return STRING(buttonKey, timeout, timeoutMessageKey, timerMessageKey, s -> true);
    }

    public static ObjectBuilderButton<Byte> BYTE(String buttonKey, long timeout,
                                                 String timeoutMessageKey,
                                                 String timerMessageKey,
                                                 Function<Byte, Boolean> function) {
        return new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibDevAPI.addChatListener(player, timeout, string -> {
                            try {
                                byte input = Byte.parseByte(string);
                                button.set(input, function);
                            } catch (NumberFormatException e) {
                                BlobLibAPI.getMessage("Builder.Number-Exception").sendAndPlay(player);
                            }
                        }, timeoutMessageKey,
                        timerMessageKey)) {
        };
    }

    public static ObjectBuilderButton<Byte> SIMPLE_BYTE(String buttonKey, long timeout,
                                                        String timeoutMessageKey,
                                                        String timerMessageKey) {
        return BYTE(buttonKey, timeout, timeoutMessageKey, timerMessageKey, b -> true);
    }

    public static ObjectBuilderButton<Short> SHORT(String buttonKey, long timeout,
                                                   String timeoutMessageKey,
                                                   String timerMessageKey,
                                                   Function<Short, Boolean> function) {
        return new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibDevAPI.addChatListener(player, timeout, string -> {
                    try {
                        short input = Short.parseShort(string);
                        button.set(input, function);
                    } catch (NumberFormatException e) {
                        BlobLibAPI.getMessage("Builder.Number-Exception").sendAndPlay(player);
                    }
                }, timeoutMessageKey, timerMessageKey)) {
        };
    }

    public static ObjectBuilderButton<Short> SIMPLE_SHORT(String buttonKey, long timeout,
                                                          String timeoutMessageKey,
                                                          String timerMessageKey) {
        return SHORT(buttonKey, timeout, timeoutMessageKey, timerMessageKey, s -> true);
    }

    public static ObjectBuilderButton<Integer> INTEGER(String buttonKey, long timeout,
                                                       String timeoutMessageKey,
                                                       String timerMessageKey,
                                                       Function<Integer, Boolean> function) {
        return new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibDevAPI.addChatListener(player, timeout, string -> {
                    try {
                        int input = Integer.parseInt(string);
                        button.set(input, function);
                    } catch (NumberFormatException ignored) {
                        BlobLibAPI.getMessage("Builder.Number-Exception").sendAndPlay(player);
                    }
                }, timeoutMessageKey, timerMessageKey)) {
        };
    }

    public static ObjectBuilderButton<Integer> SIMPLE_INTEGER(String buttonKey, long timeout,
                                                              String timeoutMessageKey,
                                                              String timerMessageKey) {
        return INTEGER(buttonKey, timeout, timeoutMessageKey, timerMessageKey, i -> true);
    }

    public static ObjectBuilderButton<Long> LONG(String buttonKey, long timeout,
                                                 String timeoutMessageKey,
                                                 String timerMessageKey,
                                                 Function<Long, Boolean> function) {
        return new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibDevAPI.addChatListener(player, timeout, string -> {
                    try {
                        long input = Long.parseLong(string);
                        button.set(input, function);
                    } catch (NumberFormatException ignored) {
                        BlobLibAPI.getMessage("Builder.Number-Exception").sendAndPlay(player);
                    }
                }, timeoutMessageKey, timerMessageKey)) {
        };
    }

    public static ObjectBuilderButton<Long> SIMPLE_LONG(String buttonKey, long timeout,
                                                        String timeoutMessageKey,
                                                        String timerMessageKey) {
        return LONG(buttonKey, timeout, timeoutMessageKey, timerMessageKey, l -> true);
    }

    public static ObjectBuilderButton<Float> FLOAT(String buttonKey, long timeout,
                                                   String timeoutMessageKey,
                                                   String timerMessageKey,
                                                   Function<Float, Boolean> function) {
        return new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibDevAPI.addChatListener(player, timeout, string -> {
                    try {
                        float input = Float.parseFloat(string);
                        button.set(input, function);
                    } catch (NumberFormatException ignored) {
                        BlobLibAPI.getMessage("Builder.Number-Exception").sendAndPlay(player);
                    }
                }, timeoutMessageKey, timerMessageKey)) {
        };
    }

    public static ObjectBuilderButton<Float> SIMPLE_FLOAT(String buttonKey, long timeout,
                                                          String timeoutMessageKey,
                                                          String timerMessageKey) {
        return FLOAT(buttonKey, timeout, timeoutMessageKey, timerMessageKey, f -> true);
    }

    public static ObjectBuilderButton<Double> DOUBLE(String buttonKey, long timeout,
                                                     String timeoutMessageKey,
                                                     String timerMessageKey,
                                                     Function<Double, Boolean> function) {
        return new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibDevAPI.addChatListener(player, timeout, string -> {
                    try {
                        double input = Double.parseDouble(string);
                        button.set(input, function);
                    } catch (NumberFormatException ignored) {
                        BlobLibAPI.getMessage("Builder.Number-Exception").sendAndPlay(player);
                    }
                }, timeoutMessageKey, timerMessageKey)) {
        };
    }

    public static ObjectBuilderButton<Double> SIMPLE_DOUBLE(String buttonKey, long timeout,
                                                            String timeoutMessageKey,
                                                            String timerMessageKey) {
        return DOUBLE(buttonKey, timeout, timeoutMessageKey, timerMessageKey, d -> true);
    }

    public static ObjectBuilderButton<Block> BLOCK(String buttonKey, long timeout,
                                                   String timeoutMessageKey,
                                                   String timerMessageKey,
                                                   Function<Block, Boolean> function) {
        return new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibDevAPI.addPositionListener(player, timeout,
                        input -> {
                            button.set(input, function);
                        }, timeoutMessageKey, timerMessageKey)) {
        };
    }

    public static ObjectBuilderButton<Block> SIMPLE_BLOCK(String buttonKey, long timeout,
                                                          String timeoutMessageKey,
                                                          String timerMessageKey) {
        return BLOCK(buttonKey, timeout, timeoutMessageKey, timerMessageKey, b -> true);
    }

    public static ObjectBuilderButton<ItemStack> ITEM(String buttonKey,
                                                      String timerMessageKey,
                                                      Function<ItemStack, Boolean> function) {
        return new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibDevAPI.addDropListener(player,
                        input -> {
                            button.set(input, function);
                        }, timerMessageKey)) {
        };
    }

    public static ObjectBuilderButton<ItemStack> SIMPLE_ITEM(String buttonKey,
                                                             String timerMessageKey) {
        return ITEM(buttonKey, timerMessageKey, i -> true);
    }

    public static <T> ObjectBuilderButton<T> SELECTOR(String buttonKey,
                                                      String timerMessageKey,
                                                      Function<T, Boolean> function,
                                                      VariableSelector<T> selector) {
        return new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibDevAPI.addSelectorListener(player,
                        input -> {
                            button.set(input, function);
                        }, timerMessageKey, selector)) {
        };
    }

    public static <T> ObjectBuilderButton<T> SIMPLE_SELECTOR(String buttonKey,
                                                             String timerMessageKey,
                                                             VariableSelector<T> selector) {
        return SELECTOR(buttonKey, timerMessageKey, t -> true, selector);
    }

    public static ObjectBuilderButton<ReferenceBlobMessage> MESSAGE(String buttonKey, long timeout,
                                                                    String timeoutMessageKey,
                                                                    String timerMessageKey,
                                                                    Function<ReferenceBlobMessage, Boolean> function) {
        return new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibDevAPI.addChatListener(player, timeout,
                        string -> {
                            ReferenceBlobMessage message = BlobLibAPI.getMessage(string);
                            button.set(message, function);
                        },
                        timeoutMessageKey,
                        timerMessageKey)) {
        };
    }

    public static ObjectBuilderButton<ReferenceBlobMessage> SIMPLE_MESSAGE(String buttonKey, long timeout,
                                                                           String timeoutMessageKey,
                                                                           String timerMessageKey) {
        return MESSAGE(buttonKey, timeout, timeoutMessageKey, timerMessageKey, s -> true);
    }

    public static ObjectBuilderButton<World> WORLD(String buttonKey, long timeout,
                                                   String timeoutMessageKey,
                                                   String timerMessageKey,
                                                   Function<World, Boolean> function) {
        return new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibDevAPI.addChatListener(player, timeout,
                        string -> {
                            World world = Bukkit.getWorld(string);
                            button.set(world, function);
                        },
                        timeoutMessageKey,
                        timerMessageKey)) {
        };
    }

    public static ObjectBuilderButton<World> SIMPLE_WORLD(String buttonKey, long timeout,
                                                          String timeoutMessageKey,
                                                          String timerMessageKey) {
        return WORLD(buttonKey, timeout, timeoutMessageKey, timerMessageKey, s -> true);
    }
}
