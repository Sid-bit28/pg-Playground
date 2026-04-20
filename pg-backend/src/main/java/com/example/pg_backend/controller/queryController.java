package com.example.pg_backend.controller;

import com.example.pg_backend.dto.QueryRequest;
import com.example.pg_backend.dto.QueryResult;
import com.example.pg_backend.service.QueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sql")
@CrossOrigin(origins = "*")
public class queryController {
    private final QueryService queryService;

    public queryController(QueryService queryService) {
        this.queryService = queryService;
    }

    @PostMapping("/execute")
    public ResponseEntity<QueryResult> executeQuery(@RequestBody QueryRequest queryRequest) {
        QueryResult queryResult = queryService.execute(queryRequest.getQuery());
        return ResponseEntity.ok(queryResult);
    }
}
