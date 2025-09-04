package com.beyond.match.common.model.dto;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@ToString
public class ItemsResponseDto<T> extends BaseResponseDto<T> {
    private final int page;

    private final int numOfRows;

    private final int totalCount;

    public ItemsResponseDto(HttpStatus status, List<T> items, int page, int totalCount) {
        super(status, items);
        this.page = page;
        this.numOfRows = items.size();
        this.totalCount = totalCount;
    }
}
