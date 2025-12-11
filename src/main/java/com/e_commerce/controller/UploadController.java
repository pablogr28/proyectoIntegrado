package com.e_commerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.e_commerce.service.CloudinaryService;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping
    public ResponseEntity<Map> upload(@RequestParam("file") MultipartFile file) throws IOException {
        Map result = cloudinaryService.upload(file);
        return ResponseEntity.ok(result);
    }
}