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

    /**
     * A quick ObjectBuilderButton for String's.
     * An example of how to use it:
     * <pre>
     *     ObjectBuilder&lt;Person&gt; objectBuilder = someRandomObjectBuilderYouHave;
     *
     *     ObjectBuilderButton&lt;String&gt; button =
     *     ObjectBuilderButtonBuilder.QUICK_STRING("Password", 300, "password");
     *
     *     assert button.equals(ObjectBuilderButtonBuilder.STRING("Password", 300,
     *     "Builder.Password-Timeout", "Builder.Password", value -> {
     *                     objectBuilder.updateDefaultButton("Password", "%password%",
     *                             value == null ? "N/A" : value + value);
     *                     objectBuilder.openInventory();
     *                     return true;
     *                 }));
     * </pre>
     *
     * @param buttonKey        The key of the button
     * @param timeout          The timeout of the chat listener
     * @param placeholderRegex The regex that the string must match
     * @return The button
     */
    public static ObjectBuilderButton<String> QUICK_STRING(String buttonKey, long timeout,
                                                           String placeholderRegex,
                                                           ObjectBuilder<?> objectBuilder) {
        return STRING(buttonKey, timeout, "Builder." + buttonKey
                        + "-Timeout", "Builder." + buttonKey,
                value -> {
                    objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                            value == null ? "N/A" : value + value);
                    objectBuilder.openInventory();
                    return true;
                });

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

    /**
     * A quick ObjectBuilderButton for Byte's.
     * An example of how to use it:
     * <pre>
     *     ObjectBuilder&lt;Person&gt; objectBuilder = someRandomObjectBuilderYouHave;
     *
     *     ObjectBuilderButton&lt;Byte&gt; button =
     *     ObjectBuilderButtonBuilder.QUICK_BYTE("Age", 300, "age", objectBuilder);
     *
     *     assert button.equals(ObjectBuilderButtonBuilder.BYTE("Age", 300,
     *     "Builder.Age-Timeout", "Builder.Age", value -> {
     *                objectBuilder.updateDefaultButton("Age", "%age%",
     *                "" + value);
     *                objectBuilder.openInventory();
     *                return true;
     *                }));
     *                </pre>
     *
     * @param buttonKey        The key of the button
     * @param timeout          The timeout of the chat listener
     * @param placeholderRegex The regex that the string must match
     * @param objectBuilder    The object builder
     * @return The button
     */
    public static ObjectBuilderButton<Byte> QUICK_BYTE(String buttonKey, long timeout,
                                                       String placeholderRegex,
                                                       ObjectBuilder<?> objectBuilder) {
        return BYTE(buttonKey, timeout, "Builder." + buttonKey
                        + "-Timeout", "Builder." + buttonKey,
                value -> {
                    objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                            "" + value);
                    objectBuilder.openInventory();
                    return true;
                });

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

    /**
     * A quick ObjectBuilderButton for Short's.
     * An example of how to use it:
     * <pre>
     *     ObjectBuilder&lt;Person&gt; objectBuilder = someRandomObjectBuilderYouHave;
     *
     *     ObjectBuilderButton&lt;Short&gt; button =
     *     ObjectBuilderButtonBuilder.QUICK_SHORT("Level", 300, "level", objectBuilder);
     *
     *     assert button.equals(ObjectBuilderButtonBuilder.SHORT("Level", 300,
     *     "Builder.Level-Timeout", "Builder.Level", value -> {
     *           objectBuilder.updateDefaultButton("Level", "%level%",
     *           "" + value);
     *           objectBuilder.openInventory();
     *           return true;
     *           }));
     *           </pre>
     *
     * @param buttonKey        The key of the button
     * @param timeout          The timeout of the chat listener
     * @param placeholderRegex The regex that the string must match
     * @param objectBuilder    The object builder
     * @return The button
     */
    public static ObjectBuilderButton<Short> QUICK_SHORT(String buttonKey, long timeout,
                                                         String placeholderRegex,
                                                         ObjectBuilder<?> objectBuilder) {
        return SHORT(buttonKey, timeout, "Builder." + buttonKey
                        + "-Timeout", "Builder." + buttonKey,
                value -> {
                    objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                            "" + value);
                    objectBuilder.openInventory();
                    return true;
                });

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

    /**
     * A quick ObjectBuilderButton for Integer's.
     * An example of how to use it:
     * <pre>
     *     ObjectBuilder&lt;Person&gt; objectBuilder = someRandomObjectBuilderYouHave;
     *
     *     ObjectBuilderButton&lt;Integer&gt; button =
     *     ObjectBuilderButtonBuilder.QUICK_INTEGER("Damage", 300, "damage", objectBuilder);
     *
     *     assert button.equals(ObjectBuilderButtonBuilder.INTEGER("Damage", 300,
     *     "Builder.Damage-Timeout", "Builder.Damage", value -> {
     *      objectBuilder.updateDefaultButton("Damage", "%damage%",
     *      "" + value);
     *      objectBuilder.openInventory();
     *      return true;
     *      }));
     *      </pre>
     *
     * @param buttonKey        The key of the button
     * @param timeout          The timeout of the chat listener
     * @param placeholderRegex The regex that the string must match
     * @param objectBuilder    The object builder
     * @return The button
     */
    public static ObjectBuilderButton<Integer> QUICK_INTEGER(String buttonKey, long timeout,
                                                             String placeholderRegex,
                                                             ObjectBuilder<?> objectBuilder) {
        return INTEGER(buttonKey, timeout, "Builder." + buttonKey
                        + "-Timeout", "Builder." + buttonKey,
                value -> {
                    objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                            "" + value);
                    objectBuilder.openInventory();
                    return true;
                });

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

    /**
     * A quick ObjectBuilderButton for Long's.
     * An example of how to use it:
     * <pre>
     *     ObjectBuilder&lt;Person&gt; objectBuilder = someRandomObjectBuilderYouHave;
     *
     *     ObjectBuilderButton&lt;Long&gt; button =
     *     ObjectBuilderButtonBuilder.QUICK_LONG("Coins", 300, "coins", objectBuilder);
     *
     *     assert button.equals(ObjectBuilderButtonBuilder.LONG("Coins", 300,
     *     "Builder.Coins-Timeout", "Builder.Coins", value -> {
     *     objectBuilder.updateDefaultButton("Coins", "%coins%",
     *     "" + value);
     *     objectBuilder.openInventory();
     *     return true;
     *     }));
     *     </pre>
     *
     * @param buttonKey        The key of the button
     * @param timeout          The timeout of the chat listener
     * @param placeholderRegex The regex that the string must match
     * @param objectBuilder    The object builder
     * @return The button
     */
    public static ObjectBuilderButton<Long> QUICK_LONG(String buttonKey, long timeout,
                                                       String placeholderRegex,
                                                       ObjectBuilder<?> objectBuilder) {
        return LONG(buttonKey, timeout, "Builder." + buttonKey
                        + "-Timeout", "Builder." + buttonKey,
                value -> {
                    objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                            "" + value);
                    objectBuilder.openInventory();
                    return true;
                });

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

    /**
     * A quick ObjectBuilderButton for Float's.
     * An example of how to use it:
     * <pre>
     *     ObjectBuilder&lt;Person&gt; objectBuilder = someRandomObjectBuilderYouHave;
     *
     *     ObjectBuilderButton&lt;Float&gt; button =
     *     ObjectBuilderButtonBuilder.QUICK_FLOAT("Health", 300, "health", objectBuilder);
     *
     *     assert button.equals(ObjectBuilderButtonBuilder.FLOAT("Health", 300,
     *     "Builder.Health-Timeout", "Builder.Health", value -> {
     *     objectBuilder.updateDefaultButton("Health", "%health%",
     *     "" + value);
     *     objectBuilder.openInventory();
     *     return true;
     *     }));
     *     </pre>
     *
     * @param buttonKey        The key of the button
     * @param timeout          The timeout of the chat listener
     * @param placeholderRegex The regex that the string must match
     * @param objectBuilder    The object builder
     * @return The button
     */
    public static ObjectBuilderButton<Float> QUICK_FLOAT(String buttonKey, long timeout,
                                                         String placeholderRegex,
                                                         ObjectBuilder<?> objectBuilder) {
        return FLOAT(buttonKey, timeout, "Builder." + buttonKey
                        + "-Timeout", "Builder." + buttonKey,
                value -> {
                    objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                            "" + value);
                    objectBuilder.openInventory();
                    return true;
                });

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

    /**
     * A quick ObjectBuilderButton for Double's.
     * An example of how to use it:
     * <pre>
     *     ObjectBuilder&lt;Person&gt; objectBuilder = someRandomObjectBuilderYouHave;
     *
     *     ObjectBuilderButton&lt;Double&gt; button =
     *     ObjectBuilderButtonBuilder.QUICK_DOUBLE("Dogecoin", 300, "dogecoin", objectBuilder);
     *
     *     assert button.equals(ObjectBuilderButtonBuilder.DOUBLE("Dogecoin", 300,
     *     "Builder.Dogecoin-Timeout", "Builder.Dogecoin", value -> {
     *     objectBuilder.updateDefaultButton("Dogecoin", "%dogecoin%",
     *     "" + value);
     *     objectBuilder.openInventory();
     *     return true;
     *     }));
     *     </pre>
     *
     * @param buttonKey        The key of the button
     * @param timeout          The timeout of the chat listener
     * @param placeholderRegex The regex that the string must match
     * @param objectBuilder    The object builder
     * @return The button
     */
    public static ObjectBuilderButton<Double> QUICK_DOUBLE(String buttonKey, long timeout,
                                                           String placeholderRegex,
                                                           ObjectBuilder<?> objectBuilder) {
        return DOUBLE(buttonKey, timeout, "Builder." + buttonKey
                        + "-Timeout", "Builder." + buttonKey,
                value -> {
                    objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                            "" + value);
                    objectBuilder.openInventory();
                    return true;
                });

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
