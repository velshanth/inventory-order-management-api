package com.guvi.service;

import com.guvi.dto.CreateCategoryRequest;
import com.guvi.error.BadRequestException;
import com.guvi.error.DuplicateResourceException;
import com.guvi.error.ResourceNotFoundException;
import com.guvi.model.CategoryModel;
import com.guvi.repo.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryModel create(CreateCategoryRequest req){
        String name = normalize(req.getName());
        boolean active =  req.isActive() != null ? req.isActive() : true;
        if(isBlank(name)){
            throw new BadRequestException("Category Name is Required");
        }
        if(categoryRepository.existsByName(name)){
            throw new DuplicateResourceException("Category '" + name + "' already exists.");
        }

        CategoryModel category = new CategoryModel(null,name,active);
        return categoryRepository.save(category);
    }

    public List<CategoryModel> getAll(){
        return categoryRepository.findAll();
    }
    public List<CategoryModel> getActive(){
        return categoryRepository.findByActiveTrue();
    }

    public CategoryModel update(String id, CreateCategoryRequest req){
        CategoryModel existing = getById(id);
        String name = normalize(req.getName());
        if(isBlank(name)){
            throw new BadRequestException("Category Name cannot be empty");
        }
        if(!name.equals(existing.getName()) && categoryRepository.existsByName(name)){
            throw new DuplicateResourceException("Category '" + name + "' already exists.");
        }

        if(req.isActive() !=null){
            existing.setActive(req.isActive());
        }

        return categoryRepository.save(existing);
    }

    public void delete(String id){
        CategoryModel existing = getById(id);
        existing.setActive(false);
        categoryRepository.save(existing);
    }

    public CategoryModel getById(String id){
        return categoryRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Category ot found for id: "+id));
    }

    private String normalize(String s) {
        return s == null ? null : s.trim();
    }
    private  boolean isBlank(String s){
        return s==null || s.trim().isEmpty();
    }
}
