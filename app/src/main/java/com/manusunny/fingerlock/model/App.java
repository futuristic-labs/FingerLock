package com.manusunny.fingerlock.model;

public class App {
    private int id;
    private String packageName;
    private String lockMethod;

    public App(int id, String packageName, String lockMethod) {
        this.id = id;
        this.packageName = packageName;
        this.lockMethod = lockMethod;
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

    public String getLockMethod() {
        return lockMethod;
    }

    public App setLockMethod(String lockMethod) {
        this.lockMethod = lockMethod;
        return this;
    }
}