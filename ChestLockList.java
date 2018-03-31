package com.javarush.task.task19.task1918;

public class ChestLockList<T extends Locker<T>> {
    private Element last;
    private int size = -1;

    public void add(T elem) {
        Element element = new Element(elem);
        if (last == null) {
            last = element;
            if (element.isLocked) element.index = ++size;
        } else last.add(element);
    }

    public T get(int index) {
        if (index > size || index < 0) throw new ArrayIndexOutOfBoundsException();
        return last.getElement(index);
    }

    public int size() {
        return size + 1;
    }

    private class Element {
        private T value;
        private Element previous;
        private Element inner;
        private boolean isLocked;
        private int index;

        private Element(T value) {
            this.value = value;
            isLocked = value.isLock();
            if (!isLocked) index = ++size;
        }

        private void add(Element elem) {
            if (isLocked) {
                elem.previous = this;
                if (this == last) last = elem;
                if (last.isLocked) last.index = ++size;
            } else if (previous != null && !previous.isLocked) {
                previous.add(elem);
            } else if (inner != null && !inner.isLocked) {
                inner.add(elem);
            } else if (elem.value.isLock()) {
                elem.lock(this);
            } else {
                elem.previous = inner;
                inner = elem;
            }
        }

        private T getElement(int index) {
            if (index == this.index) return this.value;
            if (index > this.index) return inner.getElement(index);
            return previous.getElement(index);
        }

        private boolean lock(Element chest) {
            chest.isLocked = this.value.lock(chest.value);
            return chest.isLocked;
        }
    }
}
