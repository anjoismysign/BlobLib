package us.mytheria.bloblib.entities.currency;

import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wallet extends HashMap<String, Double> {
    public void serializeInDocument(Document document) {
        Map<String, String> map = new HashMap<>();
        forEach((k, v) -> map.put(k, v.toString()));
        List<String> list = new ArrayList<>();
        map.forEach((k, v) -> list.add(k + ":" + v));
        document.put("Wallet", list);
    }

    public void add(String key, double amount) {
        compute(key, (k, v) -> v == null ? amount : v + amount);
    }

    public void subtract(String key, double amount) {
        if (containsKey(key))
            put(key, get(key) - amount);
    }

    public boolean has(String key, double amount) {
        Double result = get(key);
        return result != null && result.compareTo(amount) >= 0;
    }

    public double balance(String key) {
        Double result = get(key);
        return result == null ? 0 : result;
    }
}
