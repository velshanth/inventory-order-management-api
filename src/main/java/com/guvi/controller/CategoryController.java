package com.guvi.controller;

import com.guvi.dto.CreateCategoryRequest;
import com.guvi.model.CategoryModel;
import com.guvi.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryModel> create(@Valid @RequestBody CreateCategoryRequest req){
        CategoryModel category = categoryService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
}

    @GetMapping
    public List<CategoryModel> getAll(){
        return categoryService.getAll();
    }

    @GetMapping("/active")
    public List<CategoryModel> getActive(){
        return categoryService.getActive();
    }


    @PutMapping("/{id}")
    public CategoryModel update(@PathVariable String id,@Valid @RequestBody CreateCategoryRequest req){
        return categoryService.update(id,req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id){
        categoryService.delete(id);
        return  ResponseEntity.noContent().build();
    }
}
