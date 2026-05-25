package io.github.anjoismysign.bloblib.entities.loot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LootTableIO {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Nullable
    public static LootTable read(@NotNull File file,
                                 @NotNull String identifier) {
        Objects.requireNonNull(file);
        Objects.requireNonNull(identifier);
        if (!file.isFile())
            return null;
        try (FileReader reader = new FileReader(file)) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            if (root == null)
                return null;
            JsonArray poolsArray = root.getAsJsonArray("pools");
            if (poolsArray == null)
                return null;
            List<LootPool> pools = new ArrayList<>();
            for (JsonElement poolElement : poolsArray) {
                JsonObject poolObject = poolElement.getAsJsonObject();
                int rolls = poolObject.get("rolls").getAsInt();
                LootPool pool = new LootPool(rolls, readEntries(poolObject));
                pools.add(pool);
            }
            return new LootTable(pools, identifier);
        } catch (IOException | IllegalStateException exception) {
            return null;
        }
    }

    private static List<LootEntry> readEntries(JsonObject poolObject) {
        JsonArray entriesArray = poolObject.getAsJsonArray("entries");
        if (entriesArray == null)
            return List.of();
        List<LootEntry> entries = new ArrayList<>();
        for (JsonElement entryElement : entriesArray) {
            JsonObject entryObject = entryElement.getAsJsonObject();
            LootEntryType type = LootEntryType.byKey(entryObject.get("type").getAsString());
            if (type == null)
                continue;
            String name = entryObject.has("name") ? entryObject.get("name").getAsString() : null;
            int weight = entryObject.has("weight") ? entryObject.get("weight").getAsInt() : 1;
            List<LootFunction> functions = readFunctions(entryObject);
            entries.add(new LootEntry(type, name, weight, functions.isEmpty() ? null : functions));
        }
        return entries;
    }

    private static List<LootFunction> readFunctions(JsonObject entryObject) {
        if (!entryObject.has("functions"))
            return List.of();
        JsonArray functionsArray = entryObject.getAsJsonArray("functions");
        List<LootFunction> functions = new ArrayList<>();
        for (JsonElement funcElement : functionsArray) {
            JsonObject funcObject = funcElement.getAsJsonObject();
            LootFunctionType functionType = LootFunctionType.byKey(funcObject.get("function").getAsString());
            if (functionType == null)
                continue;
            Map<String, Object> data = new HashMap<>();
            switch (functionType) {
                case SET_COUNT -> {
                    JsonElement countElement = funcObject.get("count");
                    if (countElement != null && countElement.isJsonObject()) {
                        JsonObject countObject = countElement.getAsJsonObject();
                        data.put("min", countObject.get("min").getAsInt());
                        data.put("max", countObject.get("max").getAsInt());
                    }
                }
            }
            functions.add(new LootFunction(functionType, data));
        }
        return functions;
    }

    public static void write(@NotNull File file,
                             @NotNull LootTable lootTable) throws IOException {
        Objects.requireNonNull(file);
        Objects.requireNonNull(lootTable);
        JsonObject root = new JsonObject();
        JsonArray poolsArray = new JsonArray();
        for (LootPool pool : lootTable.pools()) {
            poolsArray.add(toJson(pool));
        }
        root.add("pools", poolsArray);
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(root, writer);
        }
    }

    private static JsonObject toJson(LootPool pool) {
        JsonObject object = new JsonObject();
        object.addProperty("rolls", pool.rolls());
        JsonArray entriesArray = new JsonArray();
        for (LootEntry entry : pool.entries()) {
            entriesArray.add(toJson(entry));
        }
        object.add("entries", entriesArray);
        return object;
    }

    private static JsonObject toJson(LootEntry entry) {
        JsonObject object = new JsonObject();
        object.addProperty("type", entry.type().getKey());
        if (entry.name() != null)
            object.addProperty("name", entry.name());
        object.addProperty("weight", entry.weight());
        if (entry.functions() != null && !entry.functions().isEmpty()) {
            JsonArray functionsArray = new JsonArray();
            for (LootFunction function : entry.functions()) {
                functionsArray.add(toJson(function));
            }
            object.add("functions", functionsArray);
        }
        return object;
    }

    private static JsonObject toJson(LootFunction function) {
        JsonObject object = new JsonObject();
        object.addProperty("function", function.type().getKey());
        switch (function.type()) {
            case SET_COUNT -> {
                JsonObject countObject = new JsonObject();
                countObject.addProperty("min", (Integer) function.data().get("min"));
                countObject.addProperty("max", (Integer) function.data().get("max"));
                object.add("count", countObject);
            }
        }
        return object;
    }
}
