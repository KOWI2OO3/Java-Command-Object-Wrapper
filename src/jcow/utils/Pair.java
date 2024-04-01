package jcow.utils;

public record Pair<T, K>(T first, K second) {

    Pair() {
        this(null, null);
    }
    
    public T left() {
        return first;
    }

    public K right() {
        return second;
    }

    public boolean isEmpty() {
        return first == null && second == null;
    }
    
    public static <U, V> Pair<U, V> empty() {
        return of();
    }

    public static <U, V> Pair<U, V> of() {
        return new Pair<>();
    }

    public static <U, V> Pair<U, V> of(U first, V second) {
        return new Pair<>(first, second);
    }
}
