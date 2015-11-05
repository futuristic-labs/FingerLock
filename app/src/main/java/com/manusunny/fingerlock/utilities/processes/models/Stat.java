package com.manusunny.fingerlock.utilities.processes.models;

import java.io.IOException;

public final class Stat extends ProcFile {
    private final String[] fields;

    private Stat(String path) throws IOException {
        super(path);
        fields = content.split("\\s+");
    }

    public static Stat get(int pid) throws IOException {
        return new Stat(String.format("/proc/%d/stat", pid));
    }

    public String getComm() {
        return fields[1].replace("(", "").replace(")", "");
    }
}
