package com.manusunny.fingerlock.model;

public class App {
    private int id;
    private String packageName;
    private String passLockEnabled;
    private String patternLockEnabled;
    private String fingerLockEnabled;

    public App(int id, String packageName, String password, String passLockEnabled, String patternLockEnabled, String fingerLockEnabled) {
        this.id = id;
        this.packageName = packageName;
        this.passLockEnabled = passLockEnabled;
        this.patternLockEnabled = patternLockEnabled;
        this.fingerLockEnabled = fingerLockEnabled;
    }

    public App() {
    }

    @Override
    public String toString() {
        return packageName;
    }

    public int getId() {
        return id;
    }

    public App setId(int id) {
        this.id = id;
        return this;
    }

    public String getPackageName() {
        return packageName;
    }

    public App setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public String getPassLockEnabled() {
        return passLockEnabled;
    }

    public App setPassLockEnabled(String passLockEnabled) {
        this.passLockEnabled = passLockEnabled;
        return this;
    }

    public String getPatternLockEnabled() {
        return patternLockEnabled;
    }

    public App setPatternLockEnabled(String patternLockEnabled) {
        this.patternLockEnabled = patternLockEnabled;
        return this;
    }

    public String getFingerLockEnabled() {
        return fingerLockEnabled;
    }

    public App setFingerLockEnabled(String fingerLockEnabled) {
        this.fingerLockEnabled = fingerLockEnabled;
        return this;
    }
}
