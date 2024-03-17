package ru.practicum.shareit.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class Paginator {
    public static PageRequest createPageRequestWithSort(int from, int size, Sort sort) {
        return PageRequest.of(from / size, size, sort);
    }

    public static PageRequest createSimplePageRequest(int from, int size) {
        return PageRequest.of(from / size, size);
    }
}
