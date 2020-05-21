package com.irtimaled.bbor.client.gui;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ListHelper {
    public static <T> boolean findNextMatch(List<? extends T> list, int index, boolean forward, Predicate<T> match, Consumer<T> consumer) {
        ListIterator<? extends T> iterator = list.listIterator(index);
        Supplier<Boolean> hasMore = forward ? iterator::hasNext : iterator::hasPrevious;
        Supplier<T> more = forward ? iterator::next : iterator::previous;

        while (hasMore.get()) {
            T item = more.get();
            if (match.test(item)) {
                consumer.accept(item);
                return true;
            }
        }
        return false;
    }
}
