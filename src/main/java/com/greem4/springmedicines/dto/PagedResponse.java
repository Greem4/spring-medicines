package com.greem4.springmedicines.dto;

import java.util.List;

// fixme: спринг из коробки умеет дружить пейджу и MVC/web-MVC. Всякие @PageableDefault тому подтверждение
public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
        ) {
}
