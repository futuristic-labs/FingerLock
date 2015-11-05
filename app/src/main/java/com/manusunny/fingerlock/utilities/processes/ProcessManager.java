package com.manusunny.fingerlock.utilities.processes;

import android.content.Context;
import android.content.pm.PackageManager;

import com.manusunny.fingerlock.utilities.processes.models.AndroidAppProcess;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashSet;

public class ProcessManager {

    private ProcessManager() {
        throw new AssertionError("no instances");
    }

    public static HashSet<String> getRunningForegroundApps(Context ctx) {
        HashSet<String> processes = new HashSet<>();
        File[] files = new File("/proc").listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        PackageManager pm = ctx.getPackageManager();
        for (File file : files) {
            int pid;
            try {
                pid = Integer.parseInt(file.getName());
            } catch (NumberFormatException e) {
                continue;
            }
            try {
                AndroidAppProcess process = new AndroidAppProcess(pid);
                if (!process.foreground) {
                    continue;
                } else if (process.uid >= 1000 && process.uid <= 9999) {
                    continue;
                } else if (process.name.contains(":")) {
                    continue;
                } else if (pm.getLaunchIntentForPackage(process.getPackageName()) == null) {
                    continue;
                }
                processes.add(process.getPackageName());
            } catch (IOException e) {
            }
        }
        return processes;
    }
}
