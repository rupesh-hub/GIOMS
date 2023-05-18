package com.gerp.dartachalani.dto.kasamu;

import lombok.Data;

import java.util.List;

@Data
public class KasamuResponsePaginationPojo {
    private int current;
    private int pages;
    private int size;
    private int total;
    private List<KasamuResponsePojo> records;
}
