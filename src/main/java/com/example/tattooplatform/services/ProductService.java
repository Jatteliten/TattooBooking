package com.example.tattooplatform.services;

import com.example.tattooplatform.dto.product.ProductCustomerViewDto;
import com.example.tattooplatform.model.Product;
import com.example.tattooplatform.model.ProductCategory;
import com.example.tattooplatform.repos.ProductRepo;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = "products")
public class ProductService {
    private final ProductRepo productRepo;
    private final S3ImageService s3ImageService;

    public ProductService(ProductRepo productRepo, S3ImageService s3ImageService) {
        this.productRepo = productRepo;
        this.s3ImageService = s3ImageService;
    }

    @CacheEvict(allEntries = true)
    public void saveProduct(Product product){
        productRepo.save(product);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void deleteProduct(Product product){
        String imageUrl = product.getImageUrl();
        if(imageUrl != null){
            s3ImageService.deleteImage(imageUrl);
        }
        productRepo.delete(product);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public Product createAndSaveProductWithAllAttributes(ProductCategory category, String name, String description,
                                                         double price, MultipartFile file){
        if(category == null || name == null || description == null || price <= 0 || file == null){
            return null;
        }

        try {
            String imageUrl = s3ImageService.uploadImage(file, "Products");
            Product product = Product.builder()
                .category(category)
                .name(name)
                .description(description)
                .price(price)
                .imageUrl(imageUrl)
                .build();

            saveProduct(product);
            return product;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Cacheable
    public Product getProductById(UUID id){
        return productRepo.findById(id).orElse(null);
    }

    public ProductCustomerViewDto convertProductToProductCustomerViewDto(
            Product product){
        return ProductCustomerViewDto.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .build();
    }

    public List <ProductCustomerViewDto> convertProductListToProductCustomerViewDtoList(
            List<Product> products){
        return products.stream().map(this::convertProductToProductCustomerViewDto).toList();
    }
}
