package ru.rudedude.sandbox.model;

public class AppInfo implements Comparable<AppInfo> {

    long lastUpdateTime;
    String name;
    String iconPath;

    public AppInfo(String name, String iconPath, long lastUpdateTime) {
        this.name = name;
        this.iconPath = iconPath;
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getName() {
        return name;
    }

    public String getIconPath() {
        return iconPath;
    }

    @Override
    public int compareTo(AppInfo another) {
        return name.compareTo(another.name);
    }
}
