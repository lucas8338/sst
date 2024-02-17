package sst.util;

import java.util.*;

public class BasicUtil {
    public static <T> List<Integer> order(List<T> items, Comparator<T> comparator) {
        int itemsSize = items.size();
        List<Integer> indexes = new ArrayList<>(itemsSize);
        for (int i = 0; i < itemsSize; i++) {
            indexes.add(i);
        }
        Collections.sort(indexes, (a, b) -> comparator.compare(items.get(a), items.get(b)));
        return indexes;
    }

    public static <T> List<Integer> duplicated(List<T> list) {
        int listSize = list.size();
        if (listSize <= 1) {
            return List.of();
        }
        Set<Integer> result = new LinkedHashSet<>(listSize);
        List<T> listCopy = new ArrayList<>(list);
        for (int i = listSize - 1; i >= 0; i--) {
            T value = listCopy.get(i);
            listCopy.remove(i);
            i--;
            if (listCopy.contains(value)) {
                result.add(i + 1);
            }
        }
        return result.stream()
                .sorted(Integer::compare)
                .toList();
    }


}
