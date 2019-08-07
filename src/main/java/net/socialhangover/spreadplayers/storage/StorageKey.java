package net.socialhangover.spreadplayers.storage;

public interface StorageKey<T> {
    T get();
    void set(T value);
}
