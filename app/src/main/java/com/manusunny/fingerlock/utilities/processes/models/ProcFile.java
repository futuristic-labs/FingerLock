package com.manusunny.fingerlock.utilities.processes.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ProcFile extends File {

    public final String content;

    protected ProcFile(String path) throws IOException {
        super(path);
        content = readFile(path);
    }

    protected static String readFile(String path) throws IOException {
        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        output.append(reader.readLine());
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            output.append('\n').append(line);
        }
        reader.close();
        return output.toString();
    }

    @Override
    public long length() {
        return content.length();
    }
}
