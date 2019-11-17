package net.socialhangover.spreadplayers.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
public class CachedValue<T> {

    private final long timeoutMs;

    @Getter
    private final T defaultValue;

    private long lastUpdate = 0;

    private T value;

    public T get() {
        return hasExpired() || value == null ? defaultValue : value;
    }

    public void set(T value) {
        this.value = value;
        this.lastUpdate = System.currentTimeMillis();
    }

    public void reset() {
        this.lastUpdate = 0;
    }

    public boolean hasExpired() {
        return System.currentTimeMillis() - lastUpdate > timeoutMs;
    }

    public void computeIfExpired(Function<T, T> delegate) {
        if (hasExpired()) set(delegate.apply(defaultValue));
    }

}
