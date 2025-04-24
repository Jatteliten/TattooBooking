package com.example.demo.services;

import com.example.demo.dtos.productdtos.ProductWithNameDescriptionPriceImageUrlDto;
import com.example.demo.model.Product;
import com.example.demo.model.ProductCategory;
import com.example.demo.repos.ProductRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepo productRepo;
    private final S3ImageService s3ImageService;

    public ProductService(ProductRepo productRepo, S3ImageService s3ImageService) {
        this.productRepo = productRepo;
        this.s3ImageService = s3ImageService;
    }

    public List<Product> getAll(){
        return productRepo.findAll();
    }

    public void saveProduct(Product product){
        productRepo.save(product);
    }

    @Transactional
    public void deleteProduct(Product product){
        s3ImageService.deleteImage(product.getImageUrl());
        System.out.println(product.getImageUrl());
        productRepo.delete(product);
    }

    public Product getProductById(UUID id){
        return productRepo.findById(id).orElse(null);
    }

    @Transactional
    public Product createAndSaveProduct(ProductCategory category, String name, String description,
                                        double price, MultipartFile file){
        if(category == null || name == null || description == null || price == 0 || file == null){
            return null;
        }

        Product product;
        String imageUrl;

        try {
            imageUrl = s3ImageService.uploadImage(file, "Products");
            product = Product.builder()
                .category(category)
                .name(name)
                .description(description)
                .price(price)
                .imageUrl(imageUrl)
                .build();

            return productRepo.save(product);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ProductWithNameDescriptionPriceImageUrlDto convertProductToProductWithNameDescriptionPriceImageUrlDto(
            Product product){
        return ProductWithNameDescriptionPriceImageUrlDto.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .build();
    }

    public List <ProductWithNameDescriptionPriceImageUrlDto> convertProductListToProductWithNameDescriptionPriceImageUrlDtoList(
            List<Product> products){
        return products.stream().map(this::convertProductToProductWithNameDescriptionPriceImageUrlDto).toList();
    }
}
