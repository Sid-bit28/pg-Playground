package com.example.pg_backend.service;

import com.example.pg_backend.dto.TableMetaData;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SchemaService {
    private final JdbcTemplate jdbcTemplate;

    public SchemaService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TableMetaData> getDatabaseSchema() {
        // Querying the internal PostgreSQL dictionary for public tables
        String sql = """
                SELECT table_name, column_name
                FROM information_schema.columns
                WHERE table_schema = 'public'
                ORDER BY table_name, ordinal_position;
                """;

        return jdbcTemplate.query(sql, rs -> {
            Map<String, List<String>> tableMap = new LinkedHashMap<>();

            while(rs.next()) {
                String tableName = rs.getString("table_name");
                String columnName = rs.getString("column_name");
                tableMap.putIfAbsent(tableName, new ArrayList<>());
                tableMap.get(tableName).add(columnName);
            }

            // Converting the Map to our DTO list
            List<TableMetaData> schemaList = new ArrayList<>();
            for(Map.Entry<String, List<String>> entry : tableMap.entrySet()) {
                TableMetaData tableMetaData = new TableMetaData();
                tableMetaData.setTableName(entry.getKey());
                tableMetaData.setColumns(entry.getValue());
                schemaList.add(tableMetaData);
            }
            return schemaList;
        });
    }
}
