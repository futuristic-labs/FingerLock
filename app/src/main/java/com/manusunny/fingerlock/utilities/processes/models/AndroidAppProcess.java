package com.manusunny.fingerlock.utilities.processes.models;

import android.text.TextUtils;

import java.io.IOException;

public class AndroidAppProcess {

    private final Cgroup cgroup;
    public boolean foreground;
    public int uid;
    public int pid;
    public String name;

    public AndroidAppProcess(int pid) throws IOException {
        this.pid = pid;
        this.name = getProcessName(pid);
        cgroup = cgroup();
        ControlGroup cpuacct = cgroup.getGroup("cpuacct");
        ControlGroup cpu = cgroup.getGroup("cpu");
        if (cpu == null || cpuacct == null || !cpuacct.group.contains("pid_")) {
            //System.out.println(String.format("The process %d does not belong to any application", pid));
        }
        foreground = !cpu.group.contains("bg_non_interactive");
        try {
            uid = Integer.parseInt(cpuacct.group.split("/")[1].replace("uid_", ""));
        } catch (Exception e) {
            uid = status().getUid();
        }
    }

    public String getPackageName() {
        return name.split(":")[0];
    }

    private String getProcessName(int pid) throws IOException {
        String cmdline = null;
        try {
            cmdline = ProcFile.readFile(String.format("/proc/%d/cmdline", pid)).trim();
        } catch (IOException ignored) {
        }
        if (TextUtils.isEmpty(cmdline) || "null".equals(cmdline)) {
            return Stat.get(pid).getComm();
        }
        return cmdline;
    }

    public Cgroup cgroup() throws IOException {
        return Cgroup.get(pid);
    }

    public Status status() throws IOException {
        return Status.get(pid);
    }
}
