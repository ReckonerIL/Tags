package com.javarush.task.task19.task1918;

public interface Locker<T> {
    public boolean lock(T chest);
    public boolean isLock();
}
