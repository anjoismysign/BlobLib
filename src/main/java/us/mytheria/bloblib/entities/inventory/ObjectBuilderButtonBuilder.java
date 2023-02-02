package us.mytheria.bloblib.entities.inventory;

import me.anjoismysign.anjo.entities.NamingConventions;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import us.mytheria.bloblib.BlobLibAPI;
import us.mytheria.bloblib.BlobLibAssetAPI;
import us.mytheria.bloblib.entities.message.ReferenceBlobMessage;
import us.mytheria.bloblib.utilities.BukkitUtil;
import us.mytheria.bloblib.utilities.ItemStackUtil;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class ObjectBuilderButtonBuilder {

    /**
     * An ObjectBuilderButton builder for String's.
     *
     * @param buttonKey         The key of the button
     * @param timeout           The timeout of the chat listener
     * @param timeoutMessageKey The key of the timeout message
     * @param timerMessageKey   The key of the timer message
     * @param function          The function that is called when the chat listener is finished
     * @return The ObjectBuilderButton
     */
    public static ObjectBuilderButton<String> STRING(String buttonKey, long timeout,
                                                     String timeoutMessageKey,
                                                     String timerMessageKey,
                                                     Function<String, Boolean> function) {
        ObjectBuilderButton<String> objectBuilder = new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibAPI.addChatListener(player, timeout,
                        string -> {
                            if (string.equalsIgnoreCase("null")) {
                                button.set(null);
                                return;
                            }
                            if (function.apply(string)) {
                                button.set(string);
                            }
                        },
                        timeoutMessageKey,
                        timerMessageKey), function) {
        };
        function.apply(null);
        return objectBuilder;
    }

    /**
     * A simple ObjectBuilderButton for String's.
     *
     * @param buttonKey         The key of the button
     * @param timeout           The timeout of the chat listener
     * @param timeoutMessageKey The key of the timeout message
     * @param timerMessageKey   The key of the timer message
     * @return The button
     */
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
     *     ObjectBuilderButtonBuilder.QUICK_STRING("Password", 300, objectBuilder);
     *     //if value is null, it will be replaced with "N/A"
     * </pre>
     *
     * @param buttonKey The key of the button
     * @param timeout   The timeout of the chat listener
     * @return The button
     */
    public static ObjectBuilderButton<String> QUICK_STRING(String buttonKey, long timeout,
                                                           ObjectBuilder<?> objectBuilder) {
        String placeholderRegex = NamingConventions.toCamelCase(buttonKey);
        return STRING(buttonKey, timeout, "Builder." + buttonKey
                        + "-Timeout", "Builder." + buttonKey,
                value -> {
                    objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                            value == null ? "N/A" : value);
                    objectBuilder.openInventory();
                    return true;
                });

    }

    /**
     * An ObjectBuilderButton builder for Byte's.
     *
     * @param buttonKey         The key of the button
     * @param timeout           The timeout of the chat listener
     * @param timeoutMessageKey The key of the timeout message
     * @param timerMessageKey   The key of the timer message
     * @param function          The function to be executed when the button is clicked
     * @return The button
     */
    public static ObjectBuilderButton<Byte> BYTE(String buttonKey, long timeout,
                                                 String timeoutMessageKey,
                                                 String timerMessageKey,
                                                 Function<Byte, Boolean> function,
                                                 boolean nullable) {
        ObjectBuilderButton<Byte> objectBuilderButton = new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibAPI.addChatListener(player, timeout, string -> {
                            try {
                                if (nullable) {
                                    if (string.equalsIgnoreCase("null")) {
                                        button.set(null);
                                        return;
                                    }
                                }
                                byte input = Byte.parseByte(string);
                                if (function.apply(input))
                                    button.set(input);
                            } catch (NumberFormatException e) {
                                BlobLibAssetAPI.getMessage("Builder.Number-Exception").sendAndPlay(player);
                            }
                        }, timeoutMessageKey,
                        timerMessageKey), function) {
        };
        if (!nullable)
            function.apply((byte) 0);
        else
            function.apply(null);
        return objectBuilderButton;
    }

    /**
     * An simple ObjectBuilderButton for Byte's.
     *
     * @param buttonKey         The key of the button
     * @param timeout           The timeout of the chat listener
     * @param timeoutMessageKey The key of the timeout message
     * @param timerMessageKey   The key of the timer message
     * @return The button
     */
    public static ObjectBuilderButton<Byte> SIMPLE_BYTE(String buttonKey, long timeout,
                                                        String timeoutMessageKey,
                                                        String timerMessageKey) {
        return BYTE(buttonKey, timeout, timeoutMessageKey, timerMessageKey, b -> true,
                true);
    }

    /**
     * A quick ObjectBuilderButton for Byte's.
     * An example of how to use it:
     * <pre>
     *     ObjectBuilder&lt;Person&gt; objectBuilder = someRandomObjectBuilderYouHave;
     *
     *     ObjectBuilderButton&lt;Byte&gt; button =
     *     ObjectBuilderButtonBuilder.QUICK_BYTE("Age", 300, objectBuilder);
     *     //if button's value is null, it will display "N/A"
     *     //otherwise it will display the value
     *                </pre>
     *
     * @param buttonKey     The key of the button
     * @param timeout       The timeout of the chat listener
     * @param objectBuilder The object builder
     * @return The button
     */
    public static ObjectBuilderButton<Byte> QUICK_BYTE(String buttonKey, long timeout,
                                                       ObjectBuilder<?> objectBuilder) {

        String placeholderRegex = NamingConventions.toCamelCase(buttonKey);
        return BYTE(buttonKey, timeout, "Builder." + buttonKey
                        + "-Timeout", "Builder." + buttonKey,
                value -> {
                    objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                            value == null ? "N/A" : "" + value);
                    objectBuilder.openInventory();
                    return true;
                }, true);

    }

    /**
     * A quick optional ObjectBuilderButton for Byte's
     * An example of how to use it:
     *
     * <pre>
     *     ObjectBuilder&lt;Person&gt; objectBuilder = someRandomObjectBuilderYouHave;
     *
     *     ObjectBuilderButton&lt;Byte&gt; button =
     *     ObjectBuilderButtonBuilder.QUICK_OPTIONAL_BYTE("Age", 300, objectBuilder);
     *     //if button's value is less than 0 will display 'N/A',
     *     //otherwise will display the value
     *                         </pre>
     *
     * @param buttonKey     The key of the button
     * @param timeout       The timeout of the chat listener
     * @param objectBuilder The object builder
     * @return The button
     */
    public static ObjectBuilderButton<Byte> POSITIVE_BYTE(String buttonKey, long timeout,
                                                          ObjectBuilder<?> objectBuilder) {
        String placeholderRegex = NamingConventions.toCamelCase(buttonKey);
        Function<Byte, Boolean> function = value -> {
            objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                    value < 0 ? "N/A" : "" + value);
            objectBuilder.openInventory();
            return true;
        };
        ObjectBuilderButton<Byte> objectBuilderButton = new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibAPI.addChatListener(player, timeout, string -> {
                            try {
                                byte input = Byte.parseByte(string);
                                if (input < 0) {
                                    button.set(null);
                                } else {
                                    button.set(input);
                                }
                            } catch (NumberFormatException e) {
                                BlobLibAssetAPI.getMessage("Builder.Number-Exception").sendAndPlay(player);
                            }
                        }, "Builder." + buttonKey + "-Timeout",
                        "Builder." + buttonKey), function) {
        };
        function.apply((byte) -1);
        return objectBuilderButton;
    }

    /**
     * An ObjectBuilderButton builder for Short's
     *
     * @param buttonKey         The key of the button
     * @param timeout           The timeout of the chat listener
     * @param timeoutMessageKey The key of the timeout message
     * @param timerMessageKey   The key of the timer message
     * @param function          The function to execute when the button is clicked
     * @return The button
     */
    public static ObjectBuilderButton<Short> SHORT(String buttonKey, long timeout,
                                                   String timeoutMessageKey,
                                                   String timerMessageKey,
                                                   Function<Short, Boolean> function,
                                                   boolean nullable) {
        ObjectBuilderButton<Short> objectBuilderButton = new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibAPI.addChatListener(player, timeout, string -> {
                    try {
                        if (nullable) {
                            if (string.equalsIgnoreCase("null")) {
                                button.set(null);
                                return;
                            }
                        }
                        short input = Short.parseShort(string);
                        if (function.apply(input))
                            button.set(input);
                    } catch (NumberFormatException e) {
                        BlobLibAssetAPI.getMessage("Builder.Number-Exception").sendAndPlay(player);
                    }
                }, timeoutMessageKey, timerMessageKey), function) {
        };
        if (!nullable)
            function.apply((short) 0);
        else
            function.apply(null);
        return objectBuilderButton;
    }

    /**
     * A simple ObjectBuilderButton for Short's.
     *
     * @param buttonKey         The key of the button
     * @param timeout           The timeout of the chat listener
     * @param timeoutMessageKey The key of the timeout message
     * @param timerMessageKey   The key of the timer message
     * @return The button
     */
    public static ObjectBuilderButton<Short> SIMPLE_SHORT(String buttonKey, long timeout,
                                                          String timeoutMessageKey,
                                                          String timerMessageKey) {
        return SHORT(buttonKey, timeout, timeoutMessageKey, timerMessageKey, s -> true
                , true);
    }

    /**
     * A quick ObjectBuilderButton for Short's.
     * An example of how to use it:
     * <pre>
     *     ObjectBuilder&lt;Person&gt; objectBuilder = someRandomObjectBuilderYouHave;
     *
     *     ObjectBuilderButton&lt;Short&gt; button =
     *     ObjectBuilderButtonBuilder.QUICK_SHORT("Level", 300, objectBuilder);
     *     //if button's value is null will display 'N/A',
     *     //otherwise it will display the value
     *           </pre>
     *
     * @param buttonKey     The key of the button
     * @param timeout       The timeout of the chat listener
     * @param objectBuilder The object builder
     * @return The button
     */
    public static ObjectBuilderButton<Short> QUICK_SHORT(String buttonKey, long timeout,
                                                         ObjectBuilder<?> objectBuilder) {

        String placeholderRegex = NamingConventions.toCamelCase(buttonKey);
        return SHORT(buttonKey, timeout, "Builder." + buttonKey
                        + "-Timeout", "Builder." + buttonKey,
                value -> {
                    objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                            value == null ? "N/A" : "" + value);
                    objectBuilder.openInventory();
                    return true;
                }, true);

    }

    /**
     * A quick optional ObjectBuilderButton for Short's
     * An example of how to use it:
     *
     * <pre>
     *     ObjectBuilder&lt;Person&gt; objectBuilder = someRandomObjectBuilderYouHave;
     *
     *     ObjectBuilderButton&lt;Short&gt; button =
     *     ObjectBuilderButtonBuilder.QUICK_OPTIONAL_SHORT("Level", 300, objectBuilder);
     *     //if button's value is less than 0, default button will display "N/A"
     *            </pre>
     *
     * @param buttonKey     The key of the button
     * @param timeout       The timeout of the chat listener
     * @param objectBuilder The object builder
     * @return The button
     */
    public static ObjectBuilderButton<Short> POSITIVE_SHORT(String buttonKey, long timeout,
                                                            ObjectBuilder<?> objectBuilder) {
        String placeholderRegex = NamingConventions.toCamelCase(buttonKey);
        Function<Short, Boolean> function = value -> {
            objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                    value < 0 ? "N/A" : "" + value);
            objectBuilder.openInventory();
            return true;
        };
        ObjectBuilderButton<Short> objectBuilderButton = new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibAPI.addChatListener(player, timeout, string -> {
                            try {
                                short input = Short.parseShort(string);
                                if (input < 0) {
                                    button.set(null);
                                } else {
                                    button.set(input);
                                }
                            } catch (NumberFormatException e) {
                                BlobLibAssetAPI.getMessage("Builder.Number-Exception").sendAndPlay(player);
                            }
                        }, "Builder." + buttonKey + "-Timeout",
                        "Builder." + buttonKey), function) {
        };
        function.apply((short) -1);
        return objectBuilderButton;
    }

    /**
     * An ObjectBuilderButton builder for Integer's
     *
     * @param buttonKey         The key of the button
     * @param timeout           The timeout of the chat listener
     * @param timeoutMessageKey The key of the timeout message
     * @param timerMessageKey   The key of the timer message
     * @param function          The function to be executed when the button is clicked
     * @return The button
     */
    public static ObjectBuilderButton<Integer> INTEGER(String buttonKey, long timeout,
                                                       String timeoutMessageKey,
                                                       String timerMessageKey,
                                                       Function<Integer, Boolean> function,
                                                       boolean nullable) {
        ObjectBuilderButton<Integer> objectBuilder = new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibAPI.addChatListener(player, timeout, string -> {
                    try {
                        if (nullable) {
                            if (string.equalsIgnoreCase("null")) {
                                button.set(null);
                                return;
                            }
                        }
                        int input = Integer.parseInt(string);
                        if (function.apply(input))
                            button.set(input);
                    } catch (NumberFormatException ignored) {
                        BlobLibAssetAPI.getMessage("Builder.Number-Exception").sendAndPlay(player);
                    }
                }, timeoutMessageKey, timerMessageKey), function) {
        };
        if (!nullable)
            function.apply(0);
        else
            function.apply(null);
        return objectBuilder;
    }

    /**
     * A simple ObjectBuilderButton for Integer's.
     *
     * @param buttonKey         The key of the button
     * @param timeout           The timeout of the chat listener
     * @param timeoutMessageKey The key of the timeout message
     * @param timerMessageKey   The key of the timer message
     * @return The button
     */
    public static ObjectBuilderButton<Integer> SIMPLE_INTEGER(String buttonKey, long timeout,
                                                              String timeoutMessageKey,
                                                              String timerMessageKey) {
        return INTEGER(buttonKey, timeout, timeoutMessageKey, timerMessageKey, i -> true
                , true);
    }

    /**
     * A quick ObjectBuilderButton for Integer's.
     * An example of how to use it:
     * <pre>
     *     ObjectBuilder&lt;Person&gt; objectBuilder = someRandomObjectBuilderYouHave;
     *
     *     ObjectBuilderButton&lt;Integer&gt; button =
     *     ObjectBuilderButtonBuilder.QUICK_INTEGER("Damage", 300, objectBuilder);
     *     //if button's value is null will display 'N/A',
     *     //otherwise it will display the value
     *      </pre>
     *
     * @param buttonKey     The key of the button
     * @param timeout       The timeout of the chat listener
     * @param objectBuilder The object builder
     * @return The button
     */
    public static ObjectBuilderButton<Integer> QUICK_INTEGER(String buttonKey, long timeout,
                                                             ObjectBuilder<?> objectBuilder) {

        String placeholderRegex = NamingConventions.toCamelCase(buttonKey);
        return INTEGER(buttonKey, timeout, "Builder." + buttonKey
                        + "-Timeout", "Builder." + buttonKey,
                value -> {
                    objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                            value == null ? "N/A" : "" + value);
                    objectBuilder.openInventory();
                    return true;
                }, true);

    }

    /**
     * A quick optional ObjectBuilderButton for Integer's
     * An example of how to use it:
     *
     * <pre>
     *     ObjectBuilder&lt;Person&gt; objectBuilder = someRandomObjectBuilderYouHave;
     *
     *     ObjectBuilderButton&lt;Integer&gt; button =
     *     ObjectBuilderButtonBuilder.QUICK_OPTIONAL_INTEGER("Damage", 300, objectBuilder);
     *     //if button's value is lower than 0, default button will display "N/A"
     *       </pre>
     *
     * @param buttonKey     The key of the button
     * @param timeout       The timeout of the chat listener
     * @param objectBuilder The object builder
     * @return The button
     */
    public static ObjectBuilderButton<Integer> POSITIVE_INTEGER(String buttonKey, long timeout,
                                                                ObjectBuilder<?> objectBuilder) {

        String placeholderRegex = NamingConventions.toCamelCase(buttonKey);
        Function<Integer, Boolean> function = value -> {
            objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                    value < 0 ? "N/A" : "" + value);
            objectBuilder.openInventory();
            return true;
        };
        ObjectBuilderButton<Integer> objectBuilderButton = new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibAPI.addChatListener(player, timeout, string -> {
                            try {
                                int input = Integer.parseInt(string);
                                if (input < 0) {
                                    button.set(null);
                                } else {
                                    button.set(input);
                                }
                            } catch (NumberFormatException e) {
                                BlobLibAssetAPI.getMessage("Builder.Number-Exception").sendAndPlay(player);
                            }
                        }, "Builder." + buttonKey + "-Timeout",
                        "Builder." + buttonKey), function) {
        };
        function.apply(-1);
        return objectBuilderButton;
    }

    /**
     * An ObjectBuilderButton builder for Long's.
     *
     * @param buttonKey         The key of the button
     * @param timeout           The timeout of the chat listener
     * @param timeoutMessageKey The key of the timeout message
     * @param timerMessageKey   The key of the timer message
     * @param function          The function to be executed when the button is clicked
     * @return The button
     */
    public static ObjectBuilderButton<Long> LONG(String buttonKey, long timeout,
                                                 String timeoutMessageKey,
                                                 String timerMessageKey,
                                                 Function<Long, Boolean> function,
                                                 boolean nullable) {
        ObjectBuilderButton<Long> objectBuilder = new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibAPI.addChatListener(player, timeout, string -> {
                    try {
                        if (nullable) {
                            if (string.equalsIgnoreCase("null")) {
                                button.set(null);
                                return;
                            }
                        }
                        long input = Long.parseLong(string);
                        if (function.apply(input))
                            button.set(input);
                    } catch (NumberFormatException ignored) {
                        BlobLibAssetAPI.getMessage("Builder.Number-Exception").sendAndPlay(player);
                    }
                }, timeoutMessageKey, timerMessageKey), function) {
        };
        if (!nullable)
            function.apply(0L);
        else
            function.apply(null);
        return objectBuilder;
    }

    /**
     * A simple ObjectBuilderButton for Long's.
     *
     * @param buttonKey         The key of the button
     * @param timeout           The timeout of the chat listener
     * @param timeoutMessageKey The key of the timeout message
     * @param timerMessageKey   The key of the timer message
     * @return The button
     */
    public static ObjectBuilderButton<Long> SIMPLE_LONG(String buttonKey, long timeout,
                                                        String timeoutMessageKey,
                                                        String timerMessageKey) {
        return LONG(buttonKey, timeout, timeoutMessageKey, timerMessageKey, l -> true
                , true);
    }

    /**
     * A quick ObjectBuilderButton for Long's.
     * An example of how to use it:
     * <pre>
     *     ObjectBuilder&lt;Person&gt; objectBuilder = someRandomObjectBuilderYouHave;
     *
     *     ObjectBuilderButton&lt;Long&gt; button =
     *     ObjectBuilderButtonBuilder.QUICK_LONG("Coins", 300, objectBuilder);
     *     //if button's value is null will display 'N/A',
     *     //otherwise it will display the value
     *     </pre>
     *
     * @param buttonKey     The key of the button
     * @param timeout       The timeout of the chat listener
     * @param objectBuilder The object builder
     * @return The button
     */
    public static ObjectBuilderButton<Long> QUICK_LONG(String buttonKey, long timeout,
                                                       ObjectBuilder<?> objectBuilder) {

        String placeholderRegex = NamingConventions.toCamelCase(buttonKey);
        return LONG(buttonKey, timeout, "Builder." + buttonKey
                        + "-Timeout", "Builder." + buttonKey,
                value -> {
                    objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                            value == null ? "N/A" : "" + value);
                    objectBuilder.openInventory();
                    return true;
                }, true);

    }

    /**
     * A quick optional ObjectBuilderButton for Long's
     * An example of how to use it:
     *
     * <pre>
     *     ObjectBuilder&lt;Person&gt; objectBuilder = someRandomObjectBuilderYouHave;
     *
     *     ObjectBuilderButton&lt;Long&gt; button =
     *     ObjectBuilderButtonBuilder.QUICK_OPTIONAL_LONG("Coins", 300, objectBuilder);
     *     //if button's value is lower than 0, default button will display "N/A"
     *     </pre>
     *
     * @param buttonKey     The key of the button
     * @param timeout       The timeout of the chat listener
     * @param objectBuilder The object builder
     * @return The button
     */
    public static ObjectBuilderButton<Long> POSITIVE_LONG(String buttonKey, long timeout,
                                                          ObjectBuilder<?> objectBuilder) {

        String placeholderRegex = NamingConventions.toCamelCase(buttonKey);
        Function<Long, Boolean> function = value -> {
            objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                    value < 0 ? "N/A" : "" + value);
            objectBuilder.openInventory();
            return true;
        };
        ObjectBuilderButton<Long> objectBuilderButton = new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibAPI.addChatListener(player, timeout, string -> {
                            try {
                                long input = Long.parseLong(string);
                                if (input < 0) {
                                    button.set(null);
                                } else {
                                    button.set(input);
                                }
                            } catch (NumberFormatException e) {
                                BlobLibAssetAPI.getMessage("Builder.Number-Exception").sendAndPlay(player);
                            }
                        }, "Builder." + buttonKey + "-Timeout",
                        "Builder." + buttonKey), function) {
        };
        function.apply(-1L);
        return objectBuilderButton;
    }

    /**
     * An ObjectBuilderButton builder for Float's
     *
     * @param buttonKey         The key of the button
     * @param timeout           The timeout of the chat listener
     * @param timeoutMessageKey The key of the timeout message
     * @param timerMessageKey   The key of the timer message
     * @param function          The function to be executed when the button is clicked
     * @return The button
     */
    public static ObjectBuilderButton<Float> FLOAT(String buttonKey, long timeout,
                                                   String timeoutMessageKey,
                                                   String timerMessageKey,
                                                   Function<Float, Boolean> function,
                                                   boolean nullable) {
        ObjectBuilderButton<Float> objectBuilder = new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibAPI.addChatListener(player, timeout, string -> {
                    try {
                        if (nullable) {
                            if (string.equalsIgnoreCase("null")) {
                                button.set(null);
                                return;
                            }
                        }
                        float input = Float.parseFloat(string);
                        if (function.apply(input))
                            button.set(input);
                    } catch (NumberFormatException ignored) {
                        BlobLibAssetAPI.getMessage("Builder.Number-Exception").sendAndPlay(player);
                    }
                }, timeoutMessageKey, timerMessageKey), function) {
        };
        if (!nullable)
            function.apply(0F);
        else
            function.apply(null);
        return objectBuilder;
    }

    /**
     * A simple ObjectBuilderButton for Float's.
     *
     * @param buttonKey         The key of the button
     * @param timeout           The timeout of the chat listener
     * @param timeoutMessageKey The key of the timeout message
     * @param timerMessageKey   The key of the timer message
     * @return The button
     */
    public static ObjectBuilderButton<Float> SIMPLE_FLOAT(String buttonKey, long timeout,
                                                          String timeoutMessageKey,
                                                          String timerMessageKey) {
        return FLOAT(buttonKey, timeout, timeoutMessageKey, timerMessageKey, f -> true
                , true);
    }

    /**
     * A quick ObjectBuilderButton for Float's.
     * An example of how to use it:
     * <pre>
     *     ObjectBuilder&lt;Person&gt; objectBuilder = someRandomObjectBuilderYouHave;
     *
     *     ObjectBuilderButton&lt;Float&gt; button =
     *     ObjectBuilderButtonBuilder.QUICK_FLOAT("Health", 300, objectBuilder);
     *     //if button's value is null will display 'N/A',
     *     //otherwise it will display the value
     *     </pre>
     *
     * @param buttonKey     The key of the button
     * @param timeout       The timeout of the chat listener
     * @param objectBuilder The object builder
     * @return The button
     */
    public static ObjectBuilderButton<Float> QUICK_FLOAT(String buttonKey, long timeout,
                                                         ObjectBuilder<?> objectBuilder) {

        String placeholderRegex = NamingConventions.toCamelCase(buttonKey);
        return FLOAT(buttonKey, timeout, "Builder." + buttonKey
                        + "-Timeout", "Builder." + buttonKey,
                value -> {
                    objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                            value == null ? "N/A" : "" + value);
                    objectBuilder.openInventory();
                    return true;
                }, true);

    }

    /**
     * A quick optional ObjectBuilderButton for Float's
     * An example of how to use it:
     *
     * <pre>
     *     ObjectBuilder&lt;Person&gt; objectBuilder = someRandomObjectBuilderYouHave;
     *
     *     ObjectBuilderButton&lt;Float&gt; button =
     *     ObjectBuilderButtonBuilder.QUICK_OPTIONAL_FLOAT("Health", 300, objectBuilder);
     *     //if button's value is lower than 0, default button will display "N/A"
     *     </pre>
     *
     * @param buttonKey     The key of the button
     * @param timeout       The timeout of the chat listener
     * @param objectBuilder The object builder
     * @return The button
     */
    public static ObjectBuilderButton<Float> POSITIVE_FLOAT(String buttonKey, long timeout,
                                                            ObjectBuilder<?> objectBuilder) {

        String placeholderRegex = NamingConventions.toCamelCase(buttonKey);
        Function<Float, Boolean> function = value -> {
            objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                    value < 0 ? "N/A" : "" + value);
            objectBuilder.openInventory();
            return true;
        };
        ObjectBuilderButton<Float> objectBuilderButton = new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibAPI.addChatListener(player, timeout, string -> {
                            try {
                                float input = Float.parseFloat(string);
                                if (input < 0) {
                                    button.set(null);
                                } else {
                                    button.set(input);
                                }
                            } catch (NumberFormatException e) {
                                BlobLibAssetAPI.getMessage("Builder.Number-Exception").sendAndPlay(player);
                            }
                        }, "Builder." + buttonKey + "-Timeout",
                        "Builder." + buttonKey), function) {
        };
        function.apply(-1F);
        return objectBuilderButton;
    }

    /**
     * An ObjectBuilderButton builder for Double's
     *
     * @param buttonKey         The key of the button
     * @param timeout           The timeout of the chat listener
     * @param timeoutMessageKey The key of the timeout message
     * @param timerMessageKey   The key of the timer message
     * @param function          The function to be executed when the button is clicked
     * @return The button
     */
    public static ObjectBuilderButton<Double> DOUBLE(String buttonKey, long timeout,
                                                     String timeoutMessageKey,
                                                     String timerMessageKey,
                                                     Function<Double, Boolean> function,
                                                     boolean nullable) {
        ObjectBuilderButton<Double> objectBuilder = new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibAPI.addChatListener(player, timeout, string -> {
                    try {
                        if (nullable) {
                            if (string.equalsIgnoreCase("null")) {
                                button.set(null);
                                return;
                            }
                        }
                        double input = Double.parseDouble(string);
                        if (function.apply(input))
                            button.set(input);
                    } catch (NumberFormatException ignored) {
                        BlobLibAssetAPI.getMessage("Builder.Number-Exception").sendAndPlay(player);
                    }
                }, timeoutMessageKey, timerMessageKey), function) {
        };
        if (!nullable)
            function.apply(0D);
        else
            function.apply(null);
        return objectBuilder;
    }

    /**
     * A simple ObjectBuilderButton for Double's.
     *
     * @param buttonKey         The key of the button
     * @param timeout           The timeout of the chat listener
     * @param timeoutMessageKey The timeout message key
     * @param timerMessageKey   The timer message key
     * @return The button
     */
    public static ObjectBuilderButton<Double> SIMPLE_DOUBLE(String buttonKey, long timeout,
                                                            String timeoutMessageKey,
                                                            String timerMessageKey) {
        return DOUBLE(buttonKey, timeout, timeoutMessageKey, timerMessageKey, d -> true
                , true);
    }

    /**
     * A quick ObjectBuilderButton for Double's.
     * An example of how to use it:
     * <pre>
     *     ObjectBuilder&lt;Person&gt; objectBuilder = someRandomObjectBuilderYouHave;
     *
     *     ObjectBuilderButton&lt;Double&gt; button =
     *     ObjectBuilderButtonBuilder.QUICK_DOUBLE("Dogecoin", 300, objectBuilder);
     *     //if button's value is null will display 'N/A',
     *     //otherwise it will display the value
     *     </pre>
     *
     * @param buttonKey     The key of the button
     * @param timeout       The timeout of the chat listener
     * @param objectBuilder The object builder
     * @return The button
     */
    public static ObjectBuilderButton<Double> QUICK_DOUBLE(String buttonKey, long timeout,
                                                           ObjectBuilder<?> objectBuilder) {

        String placeholderRegex = NamingConventions.toCamelCase(buttonKey);
        return DOUBLE(buttonKey, timeout, "Builder." + buttonKey
                        + "-Timeout", "Builder." + buttonKey,
                value -> {
                    objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                            value == null ? "N/A" : "" + value);
                    objectBuilder.openInventory();
                    return true;
                }, true);

    }

    /**
     * A quick optional ObjectBuilderButton for Double's
     * An example of how to use it:
     *
     * <pre>
     *     ObjectBuilder&lt;Person&gt; objectBuilder = someRandomObjectBuilderYouHave;
     *
     *     ObjectBuilderButton&lt;Double&gt; button =
     *     ObjectBuilderButtonBuilder.QUICK_OPTIONAL_DOUBLE("Dogecoin", 300, objectBuilder);
     *     //if button's value is lower than 0, default button will display "N/A"
     *     </pre>
     *
     * @param buttonKey     The key of the button
     * @param timeout       The timeout of the chat listener
     * @param objectBuilder The object builder
     * @return The button
     */
    public static ObjectBuilderButton<Double> POSITIVE_DOUBLE(String buttonKey, long timeout,
                                                              ObjectBuilder<?> objectBuilder) {
        String placeholderRegex = NamingConventions.toCamelCase(buttonKey);
        Function<Double, Boolean> function = value -> {
            objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                    value < 0 ? "N/A" : "" + value);
            objectBuilder.openInventory();
            return true;
        };
        ObjectBuilderButton<Double> objectBuilderButton = new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibAPI.addChatListener(player, timeout, string -> {
                            try {
                                double input = Double.parseDouble(string);
                                if (input < 0) {
                                    button.set(null);
                                } else {
                                    button.set(input);
                                }
                            } catch (NumberFormatException e) {
                                BlobLibAssetAPI.getMessage("Builder.Number-Exception").sendAndPlay(player);
                            }
                        }, "Builder." + buttonKey + "-Timeout",
                        "Builder." + buttonKey), function) {
        };
        function.apply(-1D);
        return objectBuilderButton;
    }

    /**
     * An ObjectBuilderButton builder for Block's.
     *
     * @param buttonKey         The key of the button
     * @param timeout           The timeout of the chat listener
     * @param timeoutMessageKey The key of the timeout message
     * @param timerMessageKey   The key of the timer message
     * @param function          The function to be executed when the button is clicked
     * @return The button
     */
    public static ObjectBuilderButton<Block> BLOCK(String buttonKey, long timeout,
                                                   String timeoutMessageKey,
                                                   String timerMessageKey,
                                                   Function<Block, Boolean> function) {
        ObjectBuilderButton<Block> objectBuilderButton = new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibAPI.addPositionListener(player, timeout,
                        button::set, timeoutMessageKey, timerMessageKey), function) {
        };
        function.apply(null);
        return objectBuilderButton;
    }

    /**
     * A simple ObjectBuilderButton for Block's.
     *
     * @param buttonKey         The key of the button
     * @param timeout           The timeout of the chat listener
     * @param timeoutMessageKey The key of the timeout message
     * @param timerMessageKey   The key of the timer message
     * @return The button
     */
    public static ObjectBuilderButton<Block> SIMPLE_BLOCK(String buttonKey, long timeout,
                                                          String timeoutMessageKey,
                                                          String timerMessageKey) {
        return BLOCK(buttonKey, timeout, timeoutMessageKey, timerMessageKey, b -> true);
    }

    /**
     * A quick ObjectBuilderButton for Block's.
     * An example of how to use it:
     * <pre>
     *     ObjectBuilder&lt;Person&gt; objectBuilder = someRandomObjectBuilderYouHave;
     *
     *     ObjectBuilderButton&lt;Block&gt; button =
     *     ObjectBuilderButtonBuilder.QUICK_BLOCK("Spawn", 300, objectBuilder);
     *     //if block is null, default button will display "N/A"
     *     </pre>
     *
     * @param buttonKey     The key of the button
     *                      The timeoutmessagekey is "Builder." + buttonKey + "-Timeout"
     *                      The timermessagekey is "Builder." + buttonKey
     * @param timeout       The timeout of the chat listener
     * @param objectBuilder The object builder
     * @return The button
     */
    public static ObjectBuilderButton<Block> QUICK_BLOCK(String buttonKey, long timeout,
                                                         ObjectBuilder<?> objectBuilder) {

        String placeholderRegex = NamingConventions.toCamelCase(buttonKey);
        return BLOCK(buttonKey, timeout, "Builder." + buttonKey
                        + "-Timeout", "Builder." + buttonKey,
                block -> {
                    objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                            BukkitUtil.printLocation(block.getLocation()));
                    objectBuilder.openInventory();
                    return true;
                });

    }

    /**
     * A quick ObjectBuilderButton for Block's that accepts
     * a consumer when input is given.
     *
     * @param buttonKey     The key of the button
     * @param timeout       The timeout of the chat listener
     * @param objectBuilder The object builder
     * @param consumer      The consumer
     * @return The button
     */
    public static ObjectBuilderButton<Block> QUICK_ACTION_BLOCK(String buttonKey,
                                                                long timeout,
                                                                ObjectBuilder<?> objectBuilder,
                                                                Consumer<Block> consumer) {
        String placeholderRegex = NamingConventions.toCamelCase(buttonKey);
        return BLOCK(buttonKey, timeout, "Builder." + buttonKey
                        + "-Timeout", "Builder." + buttonKey,
                block -> {
                    consumer.accept(block);
                    objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                            BukkitUtil.printLocation(block.getLocation()));
                    objectBuilder.openInventory();
                    return true;
                });
    }

    /**
     * An ObjectBuilderButton builder for ItemStack's.
     *
     * @param buttonKey       The key of the button
     * @param timerMessageKey The timer message key
     * @param function        The function
     * @return The button
     */
    public static ObjectBuilderButton<ItemStack> ITEM(String buttonKey,
                                                      String timerMessageKey,
                                                      Function<ItemStack, Boolean> function) {
        ObjectBuilderButton<ItemStack> objectBuilderButton = new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibAPI.addDropListener(player,
                        button::set, timerMessageKey), function) {
        };
        function.apply(null);
        return objectBuilderButton;
    }

    /**
     * A simple ObjectBuilderButton for ItemStack's.
     *
     * @param buttonKey       The key of the button
     * @param timerMessageKey The timer message key
     * @return The button
     */
    public static ObjectBuilderButton<ItemStack> SIMPLE_ITEM(String buttonKey,
                                                             String timerMessageKey) {
        return ITEM(buttonKey, timerMessageKey, i -> true);
    }

    /**
     * A quick ObjectBuilderButton for ItemStack's.
     * An example of how to use it:
     * <pre>
     *     ObjectBuilder&lt;Person&gt; objectBuilder = someRandomObjectBuilderYouHave;
     *
     *     ObjectBuilderButton&lt;ItemStack&gt; button =
     *     ObjectBuilderButtonBuilder.QUICK_ITEM("Icon", objectBuilder);
     *     //if itemStack is null, default button will display "N/A"
     *     </pre>
     *
     * @param buttonKey     The key of the button
     * @param objectBuilder The object builder
     * @return The button
     */
    public static ObjectBuilderButton<ItemStack> QUICK_ITEM(String buttonKey,
                                                            ObjectBuilder<?> objectBuilder) {
        String placeholderRegex = NamingConventions.toCamelCase(buttonKey);
        return ITEM(buttonKey, "Builder.ItemStack", itemStack -> {
            objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                    itemStack == null ? "N/A" : ItemStackUtil.display(itemStack));
            objectBuilder.openInventory();
            return true;
        });
    }

    /**
     * A quick ObjectBuilderButton for ItemStack's that accepts
     * a consumer when input is given.
     *
     * @param buttonKey     The key of the button
     * @param objectBuilder The object builder
     * @param consumer      The consumer (which is an ItemStack)
     * @return The button
     */
    public static ObjectBuilderButton<ItemStack> QUICK_ACTION_ITEM(String buttonKey,
                                                                   ObjectBuilder<?> objectBuilder,
                                                                   Consumer<ItemStack> consumer) {
        String placeholderRegex = NamingConventions.toCamelCase(buttonKey);
        return ITEM(buttonKey, "Builder.ItemStack", itemStack -> {
            consumer.accept(itemStack);
            objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                    itemStack == null ? "N/A" : ItemStackUtil.display(itemStack));
            objectBuilder.openInventory();
            return true;
        });
    }

    /**
     * An ObjectBuilderButton builder for Selectors.
     *
     * @param buttonKey       The key of the button
     * @param timerMessageKey The message key of the timer
     * @param function        The function to be executed when the selector is done
     * @param selector        The selector
     * @param <T>             The type of the selector
     * @return The button
     */
    public static <T> ObjectBuilderButton<T> SELECTOR(String buttonKey,
                                                      String timerMessageKey,
                                                      Function<T, Boolean> function,
                                                      VariableSelector<T> selector) {
        ObjectBuilderButton<T> objectBuilderButton = new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibAPI.addSelectorListener(player,
                        button::set, timerMessageKey, selector), function) {
        };
        function.apply(null);
        return objectBuilderButton;
    }

    /**
     * A simple ObjectBuilderButton for Selectors.
     *
     * @param buttonKey       The key of the button
     * @param timerMessageKey The message key of the timer
     * @param selector        The selector
     * @param <T>             The type of the selector
     * @return The button
     */
    public static <T> ObjectBuilderButton<T> SIMPLE_SELECTOR(String buttonKey,
                                                             String timerMessageKey,
                                                             VariableSelector<T> selector) {
        return SELECTOR(buttonKey, timerMessageKey, t -> true, selector);
    }

    /**
     * A quick ObjectBuilderButton for Selectors.
     * An example of how to use it:
     * <pre>
     *     ObjectBuilder&lt;Person&gt; objectBuilder = someRandomObjectBuilderYouHave;
     *     VariableSelector&lt;Gender&gt; selector = someRandomVariableSelectorYouHave;
     *
     *     ObjectBuilderButton&lt;Person&gt; button =
     *     ObjectBuilderButtonBuilder.QUICK_SELECTOR("Gender", selector,
     *     gender -> gender.name(), objectBuilder);
     *     //if button's value is null, it will display "N/A"
     *                 </pre>
     *
     * @param buttonKey     The key of the button
     * @param selector      The selector
     * @param ifAvailable   The function to apply if the selector is available
     * @param objectBuilder The object builder
     * @param <T>           The type of the selector
     * @return The button
     */
    public static <T> ObjectBuilderButton<T> QUICK_SELECTOR(String buttonKey,
                                                            VariableSelector<T> selector,
                                                            Function<T, String> ifAvailable,
                                                            ObjectBuilder<?> objectBuilder) {
        String placeholderRegex = NamingConventions.toCamelCase(buttonKey);
        return SELECTOR(buttonKey, "Builder." + buttonKey, t -> {
            objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                    t == null ? "N/A" : ifAvailable.apply(t));
            objectBuilder.openInventory();
            return true;
        }, selector);
    }

    /**
     * A quick ObjectBuilderButton for Selectors that accepts
     * a consumer when input is given.
     *
     * @param buttonKey     The key of the button
     * @param selector      The selector
     * @param ifAvailable   The function to apply if the selector is available
     * @param objectBuilder The object builder
     * @param consumer      The consumer (which is of T type)
     * @param <T>           The type of the selector
     * @return The button
     */
    public static <T> ObjectBuilderButton<T> QUICK_ACTION_SELECTOR(String buttonKey,
                                                                   VariableSelector<T> selector,
                                                                   Function<T, String> ifAvailable,
                                                                   ObjectBuilder<?> objectBuilder,
                                                                   Consumer<T> consumer) {
        String placeholderRegex = NamingConventions.toCamelCase(buttonKey);
        return SELECTOR(buttonKey, "Builder." + buttonKey, t -> {
            consumer.accept(t);
            objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                    t == null ? "N/A" : ifAvailable.apply(t));
            objectBuilder.openInventory();
            return true;
        }, selector);
    }

    /**
     * An ObjectBuilderButton builder for ReferenceBlobMessages.
     *
     * @param buttonKey         The key of the button
     * @param timeout           The timeout of the message
     * @param timeoutMessageKey The key of the timeout message
     * @param timerMessageKey   The key of the timer message
     * @param function          The function to apply
     * @return The button
     */
    public static ObjectBuilderButton<ReferenceBlobMessage> MESSAGE(String buttonKey, long timeout,
                                                                    String timeoutMessageKey,
                                                                    String timerMessageKey,
                                                                    Function<ReferenceBlobMessage, Boolean> function) {
        ObjectBuilderButton<ReferenceBlobMessage> objectBuilderButton = new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibAPI.addChatListener(player, timeout,
                        string -> {
                            ReferenceBlobMessage message = BlobLibAssetAPI.getMessage(string);
                            button.set(message);
                        },
                        timeoutMessageKey,
                        timerMessageKey), function) {
        };
        function.apply(null);
        return objectBuilderButton;
    }

    /**
     * A simple ObjectBuilderButton for ReferenceBlobMessages.
     *
     * @param buttonKey         The key of the button
     * @param timeout           The timeout
     * @param timeoutMessageKey The key of the timeout message
     * @param timerMessageKey   The key of the timer message
     * @return The button
     */
    public static ObjectBuilderButton<ReferenceBlobMessage> SIMPLE_MESSAGE(String buttonKey, long timeout,
                                                                           String timeoutMessageKey,
                                                                           String timerMessageKey) {
        return MESSAGE(buttonKey, timeout, timeoutMessageKey, timerMessageKey, s -> true);
    }

    /**
     * A quick ObjectBuilderButton for ReferenceBlobMessages.
     * An example of how to use it:
     * <pre>
     *     ObjectBuilder&lt;Person&gt; objectBuilder = someRandomObjectBuilderYouHave;
     *
     *     ObjectBuilderButton&lt;Person&gt; button =
     *     ObjectBuilderButtonBuilder.QUICK_MESSAGE("LoginMessage", 60, objectBuilder);
     *     //if message is null, it will display "N/A"
     *           </pre>
     *
     * @param buttonKey     The key of the button
     * @param timeout       The timeout
     * @param objectBuilder The object builder
     * @return The button
     */
    public static ObjectBuilderButton<ReferenceBlobMessage> QUICK_MESSAGE(String buttonKey,
                                                                          long timeout,
                                                                          ObjectBuilder<?> objectBuilder) {
        String placeholderRegex = NamingConventions.toCamelCase(buttonKey);
        return MESSAGE(buttonKey, timeout, "Builder." + buttonKey + "-Timeout",
                "Builder." + buttonKey, message -> {
                    objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                            message == null ? "N/A" : message.getReference());
                    objectBuilder.openInventory();
                    return true;
                });
    }

    /**
     * A quick ObjectBuilderButton for ReferenceBlobMessages that accepts
     * a consumer when input is given.
     *
     * @param buttonKey     The key of the button
     * @param timeout       The timeout
     * @param objectBuilder The object builder
     * @param consumer      The consumer (which is a ReferenceBlobMessage)
     * @return The button
     */
    public static ObjectBuilderButton<ReferenceBlobMessage> QUICK_ACTION_MESSAGE(String buttonKey,
                                                                                 long timeout,
                                                                                 ObjectBuilder<?> objectBuilder,
                                                                                 Consumer<ReferenceBlobMessage> consumer) {
        String placeholderRegex = NamingConventions.toCamelCase(buttonKey);
        return MESSAGE(buttonKey, timeout, "Builder." + buttonKey + "-Timeout",
                "Builder." + buttonKey, message -> {
                    consumer.accept(message);
                    objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                            message == null ? "N/A" : message.getReference());
                    objectBuilder.openInventory();
                    return true;
                });
    }

    /**
     * An ObjectBuilderButton builder for Worlds.
     *
     * @param buttonKey         The key of the button
     * @param timeout           The timeout
     * @param timeoutMessageKey The timeout message key
     * @param timerMessageKey   The timer message key
     * @param function          The function to apply
     * @return The button
     */
    public static ObjectBuilderButton<World> WORLD(String buttonKey, long timeout,
                                                   String timeoutMessageKey,
                                                   String timerMessageKey,
                                                   Function<World, Boolean> function) {
        ObjectBuilderButton<World> objectBuilderButton = new ObjectBuilderButton<>(buttonKey, Optional.empty(),
                (button, player) -> BlobLibAPI.addChatListener(player, timeout,
                        string -> {
                            World world = Bukkit.getWorld(string);
                            button.set(world);
                        },
                        timeoutMessageKey,
                        timerMessageKey), function) {
        };
        function.apply(null);
        return objectBuilderButton;
    }

    /**
     * A simple ObjectBuilderButton for Worlds.
     *
     * @param buttonKey         The key of the button
     * @param timeout           The timeout
     * @param timeoutMessageKey The timeout message key
     * @param timerMessageKey   The timer message key
     * @return The button
     */
    public static ObjectBuilderButton<World> SIMPLE_WORLD(String buttonKey, long timeout,
                                                          String timeoutMessageKey,
                                                          String timerMessageKey) {
        return WORLD(buttonKey, timeout, timeoutMessageKey, timerMessageKey, s -> true);
    }

    /**
     * A quick ObjectBuilderButton for Worlds.
     * An example of how to use it:
     * <pre>
     *     ObjectBuilder&lt;Person&gt; objectBuilder = someRandomObjectBuilderYouHave;
     *
     *     ObjectBuilderButton&lt;Person&gt; button =
     *     ObjectBuilderButtonBuilder.QUICK_WORLD("PlotsWorld", 60, objectBuilder);
     *     //if world is null, it will display "N/A"
     *      </pre>
     *
     * @param buttonKey     The key of the button
     * @param timeout       The timeout
     * @param objectBuilder The object builder
     * @return The button
     */
    public static ObjectBuilderButton<World> QUICK_WORLD(String buttonKey,
                                                         long timeout,
                                                         ObjectBuilder<?> objectBuilder) {
        String placeholderRegex = NamingConventions.toCamelCase(buttonKey);
        return WORLD(buttonKey, timeout, "Builder." + buttonKey + "-Timeout",
                "Builder." + buttonKey, world -> {
                    objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                            world == null ? "N/A" : world.getName());
                    objectBuilder.openInventory();
                    return true;
                });
    }

    /**
     * A quick ObjectBuilderButton for Worlds that accepts
     * a consumer when input is given.
     *
     * @param buttonKey     The key of the button
     * @param timeout       The timeout
     * @param objectBuilder The object builder
     * @param consumer      The consumer (which is a World)
     * @return The button
     */
    public static ObjectBuilderButton<World> QUICK_ACTION_WORLD(String buttonKey,
                                                                long timeout,
                                                                ObjectBuilder<?> objectBuilder,
                                                                Consumer<World> consumer) {
        String placeholderRegex = NamingConventions.toCamelCase(buttonKey);
        return WORLD(buttonKey, timeout, "Builder." + buttonKey + "-Timeout",
                "Builder." + buttonKey, world -> {
                    consumer.accept(world);
                    objectBuilder.updateDefaultButton(buttonKey, "%" + placeholderRegex + "%",
                            world == null ? "N/A" : world.getName());
                    objectBuilder.openInventory();
                    return true;
                });
    }
}
