package com.javarush.task.task19.task1918;

public class Tag implements Locker<Tag> {
    private int headStart = -1;
    private int headEnd = -1;
    private int tailStart = -1;
    private int tailEnd = -1;
    private final boolean isLock;

    public Tag(int start, int end, boolean isLock) {
        this.isLock = isLock;
        if (isLock) {
            tailStart = start;
            tailEnd = end;
        } else {
            headStart = start;
            headEnd = end;
        }
    }

    public void print(String content) {
        if (headStart != -1 && tailStart != -1) System.out.println(content.substring(headStart, tailEnd));
        else if (headStart != -1) System.out.println(content.substring(headStart, headEnd));
        else System.out.println(content.substring(tailStart, tailEnd));
    }

    @Override
    public boolean lock(Tag chest) {
        chest.tailStart = tailStart;
        chest.tailEnd = tailEnd;
        return true;
    }

    @Override
    public boolean isLock() {
            return isLock;
        }
}
