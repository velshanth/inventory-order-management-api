package com.guvi.service;

import com.guvi.dto.CreateCategoryRequest;
import com.guvi.error.BadRequestException;
import com.guvi.error.DuplicateResourceException;
import com.guvi.error.ResourceNotFoundException;
import com.guvi.model.CategoryModel;
import com.guvi.repo.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void create_ShouldCreateCategorySuccessfully() {

        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName(" Electronics ");
        request.setActive(true);

        when(categoryRepository.existsByName("Electronics"))
                .thenReturn(false);

        CategoryModel savedCategory =
                new CategoryModel("1", "Electronics", true);

        when(categoryRepository.save(any(CategoryModel.class)))
                .thenReturn(savedCategory);

        CategoryModel result = categoryService.create(request);

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Electronics", result.getName());
        assertTrue(result.getActive());

        ArgumentCaptor<CategoryModel> captor =
                ArgumentCaptor.forClass(CategoryModel.class);

        verify(categoryRepository).save(captor.capture());

        CategoryModel categoryToSave = captor.getValue();

        assertEquals("Electronics", categoryToSave.getName());
        assertTrue(categoryToSave.getActive());
    }

    @Test
    void create_ShouldDefaultActiveToTrue_WhenActiveIsNull() {

        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Books");
        request.setActive(null);

        when(categoryRepository.existsByName("Books"))
                .thenReturn(false);

        when(categoryRepository.save(any(CategoryModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CategoryModel result = categoryService.create(request);

        assertTrue(result.getActive());
    }

    @Test
    void create_ShouldThrowBadRequest_WhenNameIsNull() {

        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName(null);

        assertThrows(
                BadRequestException.class,
                () -> categoryService.create(request)
        );

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowBadRequest_WhenNameIsBlank() {

        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("   ");

        assertThrows(
                BadRequestException.class,
                () -> categoryService.create(request)
        );

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowDuplicateResourceException_WhenNameExists() {

        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Electronics");

        when(categoryRepository.existsByName("Electronics"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> categoryService.create(request)
        );

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void getAll_ShouldReturnAllCategories() {

        List<CategoryModel> categories = List.of(
                new CategoryModel("1", "Electronics", true),
                new CategoryModel("2", "Books", true)
        );

        when(categoryRepository.findAll()).thenReturn(categories);

        List<CategoryModel> result = categoryService.getAll();

        assertEquals(2, result.size());
        verify(categoryRepository).findAll();
    }

    @Test
    void getActive_ShouldReturnOnlyActiveCategories() {

        List<CategoryModel> activeCategories = List.of(
                new CategoryModel("1", "Electronics", true)
        );

        when(categoryRepository.findByActiveTrue())
                .thenReturn(activeCategories);

        List<CategoryModel> result = categoryService.getActive();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getActive());

        verify(categoryRepository).findByActiveTrue();
    }

    @Test
    void getById_ShouldReturnCategory_WhenExists() {

        CategoryModel category =
                new CategoryModel("1", "Electronics", true);

        when(categoryRepository.findById("1"))
                .thenReturn(Optional.of(category));

        CategoryModel result = categoryService.getById("1");

        assertEquals("Electronics", result.getName());
    }

    @Test
    void getById_ShouldThrowResourceNotFound_WhenNotExists() {

        when(categoryRepository.findById("1"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.getById("1")
        );
    }

    @Test
    void update_ShouldUpdateActiveStatusSuccessfully() {

        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Electronics");
        request.setActive(false);

        CategoryModel existing =
                new CategoryModel("1", "Electronics", true);

        when(categoryRepository.findById("1"))
                .thenReturn(Optional.of(existing));

        when(categoryRepository.save(any(CategoryModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CategoryModel result =
                categoryService.update("1", request);

        assertFalse(result.getActive());

        verify(categoryRepository).save(existing);
    }

    @Test
    void update_ShouldThrowBadRequest_WhenNameIsBlank() {

        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName(" ");

        CategoryModel existing =
                new CategoryModel("1", "Electronics", true);

        when(categoryRepository.findById("1"))
                .thenReturn(Optional.of(existing));

        assertThrows(
                BadRequestException.class,
                () -> categoryService.update("1", request)
        );

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void update_ShouldThrowDuplicateResourceException_WhenNewNameExists() {

        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Books");

        CategoryModel existing =
                new CategoryModel("1", "Electronics", true);

        when(categoryRepository.findById("1"))
                .thenReturn(Optional.of(existing));

        when(categoryRepository.existsByName("Books"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> categoryService.update("1", request)
        );

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void delete_ShouldDeactivateCategory() {

        CategoryModel existing =
                new CategoryModel("1", "Electronics", true);

        when(categoryRepository.findById("1"))
                .thenReturn(Optional.of(existing));

        categoryService.delete("1");

        assertFalse(existing.getActive());

        verify(categoryRepository).save(existing);
    }

    @Test
    void delete_ShouldThrowResourceNotFound_WhenCategoryMissing() {

        when(categoryRepository.findById("1"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.delete("1")
        );

        verify(categoryRepository, never()).save(any());
    }
}