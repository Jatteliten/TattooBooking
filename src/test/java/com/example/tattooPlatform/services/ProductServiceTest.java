package com.example.tattooPlatform.services;

import com.example.tattooPlatform.dtos.productdtos.ProductWithNameDescriptionPriceImageUrlDto;
import com.example.tattooPlatform.model.Product;
import com.example.tattooPlatform.model.ProductCategory;
import com.example.tattooPlatform.repos.ProductRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
class ProductServiceTest {
    @Autowired
    ProductRepo productRepo;
    @Autowired
    ProductService productService;
    @MockBean
    S3ImageService s3ImageService;
    private static final MockMultipartFile MOCK_FILE = new MockMultipartFile(
            "file", "test-image.jpg", "image/jpeg", "dummy image content".getBytes());
    private static final ProductCategory CATEGORY = new ProductCategory();
    private static final String NAME = "test name";
    private static final String DESCRIPTION = "test description";
    private static final double PRICE = 50;

    @AfterEach
    void deleteAll(){
        productRepo.deleteAll();
    }

    @Test
    void saveProduct_shouldSaveProduct() {
        productService.saveProduct(new Product());

        assertEquals(1, productRepo.findAll().size());
    }

    @Test
    void deleteProduct_shouldDeleteProduct() {
        Product product = new Product();
        productRepo.save(product);
        productService.deleteProduct(product);

        assertTrue(productRepo.findAll().isEmpty());
    }

    @Test
    void deleteProduct_shouldAttemptToDeleteAssociatedImage() {
        Product product = Product.builder()
                .imageUrl("test")
                .build();
        productService.deleteProduct(product);

        verify(s3ImageService).deleteImage(product.getImageUrl());
    }

    @Test
    void getProductById_shouldGetCorrectProduct() {
        Product product = new Product();
        productRepo.save(product);

        assertEquals(product.getId(), productService.getProductById(product.getId()).getId());
    }

    @Test
    void createAndSaveProductWithAllAttributes_shouldReturnNull_IfAnyAttributeIsMissing() {
        assertNull(productService.createAndSaveProductWithAllAttributes(
                null, NAME, DESCRIPTION, PRICE, MOCK_FILE));
        assertNull(productService.createAndSaveProductWithAllAttributes(
                CATEGORY, null, DESCRIPTION, PRICE, MOCK_FILE));
        assertNull(productService.createAndSaveProductWithAllAttributes(
                CATEGORY, NAME, null, PRICE, MOCK_FILE));
        assertNull(productService.createAndSaveProductWithAllAttributes(
                CATEGORY, NAME, DESCRIPTION, PRICE, null));
    }

    @Test
    void createAndSaveProductWithAllAttributes_shouldReturnNull_IfPriceIsEqualToOrLessThanZero() {
        assertNull(productService.createAndSaveProductWithAllAttributes(
                CATEGORY, NAME, DESCRIPTION, 0, MOCK_FILE));
        assertNull(productService.createAndSaveProductWithAllAttributes(
                CATEGORY, NAME, DESCRIPTION, -50, MOCK_FILE));
    }

    @Test
    void createAndSaveProductWithAllAttributes_shouldAttemptToSaveImage() {
        productService.createAndSaveProductWithAllAttributes(CATEGORY, NAME, DESCRIPTION, PRICE, MOCK_FILE);

        try {
            verify(s3ImageService).uploadImage(MOCK_FILE, "Products");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void convertProductToProductWithNameDescriptionPriceImageUrlDto_shouldConvertCorrectly() {
        Product product = Product.builder()
                .name(NAME)
                .description(DESCRIPTION)
                .price(PRICE)
                .imageUrl("test")
                .build();

        ProductWithNameDescriptionPriceImageUrlDto productDto =
                productService.convertProductToProductWithNameDescriptionPriceImageUrlDto(product);

        assertEquals(NAME, productDto.getName());
        assertEquals(DESCRIPTION, productDto.getDescription());
        assertEquals(PRICE, productDto.getPrice());
        assertEquals("test", productDto.getImageUrl());
    }

    @Test
    void convertProductListToProductWithNameDescriptionPriceImageUrlDtoList_shouldConvertListCorrectly() {
        Product productOne = Product.builder()
                .name(NAME + 1)
                .description(DESCRIPTION + 1)
                .price(PRICE + 1)
                .imageUrl("test1")
                .build();
        Product productTwo = Product.builder()
                .name(NAME + 2)
                .description(DESCRIPTION + 2)
                .price(PRICE + 2)
                .imageUrl("test2")
                .build();

        List<Product> products = List.of(productOne, productTwo);

        List<ProductWithNameDescriptionPriceImageUrlDto> productDtoList =
                productService.convertProductListToProductWithNameDescriptionPriceImageUrlDtoList(products);

        for(int i = 0; i < products.size(); i++){
            assertEquals(products.get(i).getName(), productDtoList.get(i).getName());
            assertEquals(products.get(i).getDescription(), productDtoList.get(i).getDescription());
            assertEquals(products.get(i).getPrice(), productDtoList.get(i).getPrice());
            assertEquals(products.get(i).getImageUrl(), productDtoList.get(i).getImageUrl());
        }
    }
}