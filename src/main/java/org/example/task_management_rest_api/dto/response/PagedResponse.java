package org.example.task_management_rest_api.dto.response;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

import lombok.Getter;

@Getter
public class PagedResponse<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean last;

    public PagedResponse(Page<?> page, List<T> content) {
        this.content = content;
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.last = page.isLast();
    }

    public static <T, E> PagedResponse<T> from(Page<E> page, Function<E, T> mapper) {
        List<T> content = page.getContent().stream().map(mapper).toList();
        return new PagedResponse<>(page, content);
    }
}
