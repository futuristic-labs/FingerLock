package com.manusunny.fingerlock.utilities.processes.models;

import java.io.IOException;

public final class Status extends ProcFile {

    private Status(String path) throws IOException {
        super(path);
    }

    public static Status get(int pid) throws IOException {
        return new Status(String.format("/proc/%d/status", pid));
    }

    public String getValue(String fieldName) {
        String[] lines = content.split("\n");
        for (String line : lines) {
            if (line.startsWith(fieldName + ":")) {
                return line.split(fieldName + ":")[1].trim();
            }
        }
        return null;
    }

    public int getUid() {
        try {
            return Integer.parseInt(getValue("Uid").split("\\s+")[0]);
        } catch (Exception e) {
            return -1;
        }
    }
}
