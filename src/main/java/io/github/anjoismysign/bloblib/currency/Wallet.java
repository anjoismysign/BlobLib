package io.github.anjoismysign.bloblib.currency;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Wallet extends HashMap<String, Double> implements Serializable {

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
