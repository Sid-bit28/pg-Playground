package com.example.pg_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class TableMetaData {
    private String tableName;
    private List<String> columns;
}
