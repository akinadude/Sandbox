package ru.rudedude.sandbox;

import java.util.List;

import ru.rudedude.sandbox.model.AppInfo;

public class ApplicationsList {

    private static ApplicationsList instance = new ApplicationsList();

    private List<AppInfo> list;

    public static ApplicationsList getInstance() {
        return instance;
    }

    public void setList(List<AppInfo> ai) {
        list = ai;
    }

    public List<AppInfo> getList() {
        return list;
    }
}
