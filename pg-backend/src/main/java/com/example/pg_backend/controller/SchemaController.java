package com.example.pg_backend.controller;

import com.example.pg_backend.dto.TableMetaData;
import com.example.pg_backend.service.SchemaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sql/schema")
@CrossOrigin(origins = "*")
public class SchemaController {
    private final SchemaService schemaService;

    public SchemaController(SchemaService schemaService) {
        this.schemaService = schemaService;
    }

    @GetMapping
    public ResponseEntity<List<TableMetaData>> getSchema(){
        return ResponseEntity.ok(schemaService.getDatabaseSchema());
    }
}
