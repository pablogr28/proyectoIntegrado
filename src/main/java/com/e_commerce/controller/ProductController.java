package com.e_commerce.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.e_commerce.model.Category;
import com.e_commerce.model.Product;
import com.e_commerce.model.ProductDTO;
import com.e_commerce.service.CategoryService;
import com.e_commerce.service.CloudinaryService;
import com.e_commerce.service.ProductService;

import io.jsonwebtoken.io.IOException;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CloudinaryService cloudinaryService;

    // GET ALL
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", 404,
                            "error", "Not Found",
                            "message", "Producto con ID " + id + " no encontrado."
                    ));
        }
        return ResponseEntity.ok(product);
    }

    // CREATE PRODUCT
    @PostMapping("/add")
    public ResponseEntity<?> createProduct(
            @RequestPart("productDTO") ProductDTO productDTO,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException, java.io.IOException {

        Category category = categoryService.getCategoryById(productDTO.getCategoryId());
        if (category == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", 400,
                            "error", "Bad Request",
                            "message", "Categoría con ID " + productDTO.getCategoryId() + " no encontrada."
                    ));
        }

        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Solo se permiten imágenes"));
            }
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Imagen demasiado grande (max 5MB)"));
            }

            Map uploadResult = cloudinaryService.upload(file);
            imageUrl = uploadResult.get("secure_url").toString();
        }

        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());

        // --- BOOLEAN AVAILABLE ---
        product.setAvailable(productDTO.getAvailable() != null ? productDTO.getAvailable() : true);

        product.setCategory(category);
        product.setImage(imageUrl);

        Product saved = productService.saveProduct(product);

        ProductDTO resultDTO = new ProductDTO(
                saved.getName(),
                saved.getDescription(),
                saved.getPrice(),
                saved.getStock(),
                saved.getAvailable(),
                category.getId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(resultDTO);
    }

 // UPDATE PRODUCT
    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestPart("productDTO") ProductDTO productDTO,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        Product existing = productService.getProductById(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Producto con ID " + id + " no encontrado"));
        }

        Category category = categoryService.getCategoryById(productDTO.getCategoryId());
        if (category == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Categoría con ID " + productDTO.getCategoryId() + " no encontrada."));
        }

        // Manejo de imagen
        if (file != null && !file.isEmpty()) {
            if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Solo se permiten imágenes"));
            }
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Imagen demasiado grande (max 5MB)"));
            }

            if (existing.getImagePublicId() != null) {
                cloudinaryService.destroy(existing.getImagePublicId());
            }

            Map upload = cloudinaryService.upload(file);
            existing.setImage(upload.get("secure_url").toString());
            existing.setImagePublicId(upload.get("public_id").toString());
        }

        // Actualización de campos
        existing.setName(productDTO.getName());
        existing.setDescription(productDTO.getDescription());
        existing.setPrice(productDTO.getPrice());
        existing.setStock(productDTO.getStock());
        existing.setAvailable(productDTO.getAvailable()); // ⚡ Boolean directo
        existing.setCategory(category);

        Product updated = productService.saveProduct(existing);

        return ResponseEntity.ok(updated);
    }

    // SEARCH BY NAME
    @GetMapping("/search")
    public ResponseEntity<?> getProductsByName(@RequestParam("name") String name) {
        List<Product> results = productService.findByNameContainingIgnoreCase(name);

        if (results.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", 404,
                            "error", "Not Found",
                            "message", "No se encontraron productos con el nombre: " + name
                    )
            );
        }

        return ResponseEntity.ok(results);
    }

    // DELETE → Soft delete
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        Product existing = productService.getProductById(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", 404,
                            "error", "Not Found",
                            "message", "No se encontró el producto con ID " + id
                    )
            );
        }

        existing.setAvailable(false); // BOOLEAN
        productService.saveProduct(existing);

        return ResponseEntity.ok(Map.of(
                "message", "Producto marcado como no disponible",
                "productId", existing.getId()
        ));
    }

    // PATCH deactivate
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateProduct(@PathVariable Long id) {

        Product existing = productService.getProductById(id);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Producto con ID " + id + " no encontrado"));
        }

        existing.setAvailable(false); // BOOLEAN
        productService.saveProduct(existing);

        return ResponseEntity.ok(Map.of(
                "id", existing.getId(),
                "available", existing.getAvailable()
        ));
    }
}
