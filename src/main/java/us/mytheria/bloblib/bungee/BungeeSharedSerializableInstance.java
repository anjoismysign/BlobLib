package us.mytheria.bloblib.bungee;

public class BungeeSharedSerializableInstance {
    private int count;
    private final int maxCount;

    public BungeeSharedSerializableInstance(int maxCount) {
        this.maxCount = maxCount;
        count = 0;
    }

    public int getCount() {
        return count;
    }

    public boolean increment() {
        if (count >= maxCount)
            return false;
        count++;
        return true;
    }

    public void decrement() {
        count--;
    }
}
