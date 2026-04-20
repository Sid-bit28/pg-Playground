package com.example.pg_backend.service;

import com.example.pg_backend.dto.QueryResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class QueryService {
    private final JdbcTemplate jdbcTemplate;

    public QueryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public QueryResult execute(String sql) {
        QueryResult queryResult = new QueryResult();
        long startTime = System.currentTimeMillis();

        try {
            jdbcTemplate.query(sql, (ResultSetExtractor<Void>) rs -> {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Extracting Column Names
                List<String> columns = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    columns.add(metaData.getColumnName(i));
                }
                queryResult.setColumns(columns);

                // Extracting Row Data mapped to Column Names
                List<Map<String, Object>> rows = new ArrayList<>();
                while(rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for(String col: columns) {
                        row.put(col, rs.getObject(col));
                    }
                    rows.add(row);
                }
                queryResult.setRows(rows);
                return null;
            });
        } catch (Exception e) {
            queryResult.setError(e.getMessage());
        }
        queryResult.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        return queryResult;
    }
}
