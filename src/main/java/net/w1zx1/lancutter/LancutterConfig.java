package net.w1zx1.lancutter;

public class LancutterConfig {
    public static int maxLanServers = 30;

    public static void setMaxLanServers(int value) {
        if (value > 9999) {
            maxLanServers = 9999;
        } else if (value < 0) {
            maxLanServers = 0;
        } else {
            maxLanServers = value;
        }
    }

    public static void load() {
        // TODO: load from config file
    }
}
