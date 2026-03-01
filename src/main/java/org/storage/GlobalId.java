package org.storage;

public final class GlobalId {
    private static final Object LOCK = new Object();
    private static int value = 1;

    private GlobalId() {
    }

    public static int next() {
        synchronized (LOCK) {
            return value++;
        }
    }

    public static int current() {
        synchronized (LOCK) {
            return value;
        }
    }

    public static void reset(int startValue) {
        synchronized (LOCK) {
            value = startValue;
        }
    }
}
