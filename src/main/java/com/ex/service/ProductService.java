package com.ex.service;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ex.entity.Product;
import com.ex.repository.ProductRepository;

@Service
public class ProductService {

    private final Path rootLocation = Paths.get("product-images");

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product saveProduct(Product product, MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            // Ensure directory exists before saving the file
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }
            
            String imageName = saveImage(image);
            product.setImagePath(imageName);
        }
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails, MultipartFile image) throws IOException {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id " + id));

        product.setName(productDetails.getName());
        product.setPrice(productDetails.getPrice());

        if (image != null && !image.isEmpty()) {
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }
            
            String imageName = saveImage(image);
            product.setImagePath(imageName);
        }

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    private String saveImage(MultipartFile image) throws IOException {
        String imageName = image.getOriginalFilename();
        Files.copy(image.getInputStream(), this.rootLocation.resolve(imageName));
        return imageName;
    }
    
    public List<Product> findProductsByName(String name) {
        return productRepository.findByName(name);
    }
    
//    public List<Product> findProductsByName(String name) throws IOException {
//        List<Product> products = productRepository.findByName(name);
//        for (Product product : products) {
//            product.setImageBase64(loadImageAsBase64(product.getImagePath()));
//        }
//        return products;
//    }
//
//    private String loadImageAsBase64(String imagePath) throws IOException {
//        Path path = rootLocation.resolve(imagePath);
//        if (Files.exists(path)) {
//            byte[] imageBytes = Files.readAllBytes(path);
//            return Base64.getEncoder().encodeToString(imageBytes);
//        }
//        return null;
//    }
}
