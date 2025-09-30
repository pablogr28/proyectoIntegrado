package com.e_commerce.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.e_commerce.model.Category;
import com.e_commerce.model.Product;
import com.e_commerce.model.ProductDTO;
import com.e_commerce.service.CategoryService;
import com.e_commerce.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", 404,
                            "error", "Not Found",
                            "message", "Producto con ID " + id + " no encontrado."
                    )
            );
        }
        return ResponseEntity.ok(convertToDTO(product));
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody @Valid ProductDTO productDTO) {
        Category category = categoryService.getCategoryById(productDTO.getCategoryId());
        if (category == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", 400,
                            "error", "Bad Request",
                            "message", "Categoría con ID " + productDTO.getCategoryId() + " no encontrada."
                    )
            );
        }

        Product product = convertToEntity(productDTO, category);
        Product saved = productService.saveProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductDTO productDTO) {
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

        Category category = categoryService.getCategoryById(productDTO.getCategoryId());
        if (category == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", 400,
                            "error", "Bad Request",
                            "message", "Categoría con ID " + productDTO.getCategoryId() + " no encontrada."
                    )
            );
        }

        existing.setName(productDTO.getName());
        existing.setDescription(productDTO.getDescription());
        existing.setPrice(productDTO.getPrice());
        existing.setStock(productDTO.getStock());
        existing.setAvailable(productDTO.getAvailable() ? "Yes" : "No");
        existing.setCategory(category);

        Product updated = productService.saveProduct(existing);
        return ResponseEntity.ok(convertToDTO(updated));
    }

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
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // --- Métodos auxiliares para mapear entre Product y ProductDTO ---
    private ProductDTO convertToDTO(Product product) {
        return new ProductDTO(
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                "Yes".equalsIgnoreCase(product.getAvailable()),
                product.getCategory() != null ? product.getCategory().getId() : null,
                null // Puedes incluir imageUrl si lo agregas al Product
        );
    }

    private Product convertToEntity(ProductDTO dto, Category category) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock()
        		);
        product.setAvailable(dto.getAvailable() ? "Yes" : "No");
        product.setCategory(category);
        return product;
    }
}
