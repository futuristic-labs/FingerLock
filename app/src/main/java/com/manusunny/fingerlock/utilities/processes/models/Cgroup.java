package com.manusunny.fingerlock.utilities.processes.models;

import java.io.IOException;
import java.util.ArrayList;

public final class Cgroup extends ProcFile {

    public final ArrayList<ControlGroup> groups;

    private Cgroup(String path) throws IOException {
        super(path);
        String[] lines = content.split("\n");
        groups = new ArrayList<>();
        for (String line : lines) {
            try {
                groups.add(new ControlGroup(line));
            } catch (Exception ignored) {
            }
        }
    }

    public static Cgroup get(int pid) throws IOException {
        return new Cgroup(String.format("/proc/%d/cgroup", pid));
    }

    public ControlGroup getGroup(String subsystem) {
        for (ControlGroup group : groups) {
            String[] systems = group.subsystems.split(",");
            for (String name : systems) {
                if (name.equals(subsystem)) {
                    return group;
                }
            }
        }
        return null;
    }
}
