package com.guvi.service;

import com.guvi.dto.CreateProductRequest;
import com.guvi.error.BadRequestException;
import com.guvi.error.ResourceNotFoundException;
import com.guvi.model.CategoryModel;
import com.guvi.model.ProductModel;
import com.guvi.model.ProductStatus;
import com.guvi.repo.CategoryRepository;
import com.guvi.repo.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private ProductService productService;

    @Test
    void create_ShouldCreateProductSuccessfully() {

        CreateProductRequest request = new CreateProductRequest();
        request.setName("Laptop");
        request.setDescription("Gaming Laptop");
        request.setPrice(50000.0);
        request.setStockQuantity(10.0);
        request.setCategories(Set.of("Electronics"));
        request.setStatus(ProductStatus.ACTIVE);

        when(productRepository.existsByName("Laptop"))
                .thenReturn(false);

        when(categoryRepository.findByNameIn(Set.of("Electronics")))
                .thenReturn(List.of(
                        new CategoryModel("1", "Electronics", true)
                ));

        ProductModel saved = new ProductModel(
                "1",
                "Laptop",
                "Gaming Laptop",
                50000.0,
                10.0,
                Set.of("Electronics"),
                ProductStatus.ACTIVE
        );

        when(productRepository.save(any(ProductModel.class)))
                .thenReturn(saved);

        ProductModel result = productService.create(request);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());

        verify(productRepository).save(any(ProductModel.class));
    }

    @Test
    void create_ShouldThrow_WhenNameBlank() {

        CreateProductRequest request = new CreateProductRequest();
        request.setName(" ");

        assertThrows(
                BadRequestException.class,
                () -> productService.create(request)
        );
    }

    @Test
    void create_ShouldThrow_WhenProductAlreadyExists() {

        CreateProductRequest request = new CreateProductRequest();
        request.setName("Laptop");

        when(productRepository.existsByName("Laptop"))
                .thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> productService.create(request)
        );
    }

    @Test
    void create_ShouldThrow_WhenPriceInvalid() {

        CreateProductRequest request = new CreateProductRequest();
        request.setName("Laptop");
        request.setPrice(0.0);

        when(productRepository.existsByName("Laptop"))
                .thenReturn(false);

        assertThrows(
                BadRequestException.class,
                () -> productService.create(request)
        );
    }

    @Test
    void create_ShouldThrow_WhenStockInvalid() {

        CreateProductRequest request = new CreateProductRequest();
        request.setName("Laptop");
        request.setPrice(100.0);
        request.setStockQuantity(0.0);

        when(productRepository.existsByName("Laptop"))
                .thenReturn(false);

        assertThrows(
                BadRequestException.class,
                () -> productService.create(request)
        );
    }

    @Test
    void create_ShouldThrow_WhenCategoriesMissing() {

        CreateProductRequest request = new CreateProductRequest();
        request.setName("Laptop");
        request.setPrice(100.0);
        request.setStockQuantity(10.0);
        request.setCategories(Set.of());

        when(productRepository.existsByName("Laptop"))
                .thenReturn(false);

        assertThrows(
                BadRequestException.class,
                () -> productService.create(request)
        );
    }

    @Test
    void create_ShouldThrow_WhenCategoryInvalid() {

        CreateProductRequest request = new CreateProductRequest();
        request.setName("Laptop");
        request.setPrice(100.0);
        request.setStockQuantity(10.0);
        request.setCategories(Set.of("Electronics"));

        when(productRepository.existsByName("Laptop"))
                .thenReturn(false);

        when(categoryRepository.findByNameIn(Set.of("Electronics")))
                .thenReturn(List.of());

        assertThrows(
                BadRequestException.class,
                () -> productService.create(request)
        );
    }

    @Test
    void update_ShouldUpdateProductSuccessfully() {

        ProductModel existing = new ProductModel(
                "1",
                "Laptop",
                "Old",
                100.0,
                10.0,
                Set.of("Electronics"),
                ProductStatus.ACTIVE
        );

        CreateProductRequest request = new CreateProductRequest();
        request.setName("Laptop");
        request.setDescription("Updated");
        request.setPrice(200.0);
        request.setStockQuantity(20.0);
        request.setCategories(Set.of("Electronics"));
        request.setStatus(ProductStatus.ACTIVE);

        when(productRepository.findById("1"))
                .thenReturn(Optional.of(existing));

        when(categoryRepository.findByNameIn(Set.of("Electronics")))
                .thenReturn(List.of(
                        new CategoryModel("1", "Electronics", true)
                ));

        when(productRepository.save(any(ProductModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductModel result = productService.update("1", request);

        assertEquals(200.0, result.getPrice());
        assertEquals(20.0, result.getStockQuantity());
        assertEquals("Updated", result.getDescription());
    }

    @Test
    void update_ShouldThrow_WhenProductNotFound() {

        CreateProductRequest request = new CreateProductRequest();

        when(productRepository.findById("1"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.update("1", request)
        );
    }

    @Test
    void update_ShouldThrow_WhenDuplicateNameExists() {

        ProductModel existing = new ProductModel();
        existing.setId("1");
        existing.setName("Laptop");

        CreateProductRequest request = new CreateProductRequest();
        request.setName("Phone");

        when(productRepository.findById("1"))
                .thenReturn(Optional.of(existing));

        when(productRepository.existsByName("Phone"))
                .thenReturn(true);

        assertThrows(
                BadRequestException.class,
                () -> productService.update("1", request)
        );
    }

    @Test
    void getById_ShouldReturnProduct() {

        ProductModel product = new ProductModel();
        product.setId("1");

        when(productRepository.findById("1"))
                .thenReturn(Optional.of(product));

        ProductModel result = productService.getById("1");

        assertEquals("1", result.getId());
    }

    @Test
    void getById_ShouldThrowNotFound() {

        when(productRepository.findById("1"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.getById("1")
        );
    }

    @Test
    void getActive_ShouldReturnActiveProducts() {

        List<ProductModel> products = List.of(
                new ProductModel()
        );

        when(productRepository.findByStatus(ProductStatus.ACTIVE))
                .thenReturn(products);

        List<ProductModel> result = productService.getActive();

        assertEquals(1, result.size());
    }

    @Test
    void getAll_ShouldReturnProducts() {

        List<ProductModel> products = List.of(
                new ProductModel()
        );

        when(mongoTemplate.find(any(Query.class), eq(ProductModel.class)))
                .thenReturn(products);

        List<ProductModel> result = productService.getAll(
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertEquals(1, result.size());

        verify(mongoTemplate)
                .find(any(Query.class), eq(ProductModel.class));
    }

    @Test
    void getAll_ShouldThrow_WhenPageNegative() {

        assertThrows(
                BadRequestException.class,
                () -> productService.getAll(
                        null,
                        null,
                        null,
                        null,
                        -1,
                        10
                )
        );
    }

    @Test
    void getAll_ShouldThrow_WhenSizeInvalid() {

        assertThrows(
                BadRequestException.class,
                () -> productService.getAll(
                        null,
                        null,
                        null,
                        null,
                        0,
                        0
                )
        );
    }
}