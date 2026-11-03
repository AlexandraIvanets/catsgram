package ru.yandex.practicum.catsgram.model;

import java.util.Locale;

public enum SortOrder {
    ASCENDING, DESCENDING;

    public static SortOrder from(String order) {
        if (order == null) {
            throw new IllegalArgumentException("Sort order must not be null");
        }
        return switch (order.trim().toLowerCase(Locale.ROOT)) {
            case "asc", "ascending" -> ASCENDING;
            case "desc", "descending" -> DESCENDING;
            default -> throw new IllegalArgumentException("Unknown sort order: " + order);
        };
    }
}
