package com.zyn.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {

    private List<T> content;

    private Integer page;

    private Integer size;

    private Integer totalPages;

    private Long totalElements;

    private Boolean first;

    private Boolean last;
}
