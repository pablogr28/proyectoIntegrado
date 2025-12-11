package com.e_commerce.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Sube un MultipartFile a Cloudinary y devuelve el mapa de respuesta.
     * Lanza RuntimeException en caso de error.
     */
    public Map upload(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                        "folder", "ecommerce_products", 
                        "resource_type", "auto"
                    ));
            return uploadResult;
        } catch (IOException e) {
            throw new RuntimeException("Error subiendo archivo a Cloudinary: " + e.getMessage(), e);
        }
    }

    /**
     * Opcional: borrar por public_id
     */
    public Map destroy(String publicId) {
        try {
            return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new RuntimeException("Error borrando imagen en Cloudinary: " + e.getMessage(), e);
        }
    }
}
