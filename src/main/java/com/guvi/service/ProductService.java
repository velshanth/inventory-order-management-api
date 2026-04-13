package com.guvi.service;


import com.guvi.dto.CreateProductRequest;
import com.guvi.error.BadRequestException;
import com.guvi.error.ResourceNotFoundException;
import com.guvi.model.CategoryModel;
import com.guvi.model.ProductModel;;
import com.guvi.model.ProductStatus;
import com.guvi.repo.CategoryRepository;
import com.guvi.repo.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final MongoTemplate mongoTemplate;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, MongoTemplate mongoTemplate) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public ProductModel create(CreateProductRequest req){
        String name = normalize(req.getName());
        Double price = req.getPrice();
        Double stockQuantity = req.getStockQuantity();
        Set<String> categoryFromDto = req.getCategories();
        if(isBlank(name)){
            throw new BadRequestException("Product Name is Required");
        }
        if(productRepository.existsByName(name)){
            throw new BadRequestException("Product '"+ name +"' already exists");
        }
        if(price == null || price <= 0){
            throw new BadRequestException("Enter a valid price");
        }
        if(stockQuantity == null || stockQuantity <= 0){
            throw new BadRequestException("Enter a valid stock quantity");
        }
        if (categoryFromDto == null || categoryFromDto.isEmpty()) {
            throw new BadRequestException("At least one category is required");
        }
        List<CategoryModel> categoriesFromDB = categoryRepository.findByNameIn(categoryFromDto);
        if(categoryFromDto.size() != categoriesFromDB.size()){
            throw new BadRequestException("One or more category names are invalid");
        }
        ProductModel product = new ProductModel(null, req.getName(), req.getDescription(), req.getPrice(), req.getStockQuantity(), categoryFromDto, req.getStatus());
        return productRepository.save(product);
    }

    public ProductModel update(String id,CreateProductRequest req){
        String name = normalize(req.getName());
        Double price = req.getPrice();
        Double stockQuantity = req.getStockQuantity();
        ProductModel existing = getById(id);
        Set<String> categoryFromDto = req.getCategories();
        if(isBlank(name)){
            throw new BadRequestException("Product Name is Required");
        }
        if(!name.equals(existing.getName()) && productRepository.existsByName(name)){
            throw new BadRequestException("Product '"+ name +"' already exists");
        }
        if(price == null || price <= 0){
            throw new BadRequestException("Enter a valid price");
        }
        if(stockQuantity == null || stockQuantity < 0){
            throw new BadRequestException("Enter a valid stock quantity");
        }
        List<CategoryModel> categoriesFromDB = categoryRepository.findByNameIn(categoryFromDto);
        if(categoryFromDto.size() != categoriesFromDB.size()){
            throw new BadRequestException("One or more category names are invalid");
        }
        existing.setName(name);
        existing.setPrice(price);
        existing.setStockQuantity(stockQuantity);
        existing.setDescription(req.getDescription());
        existing.setStatus(req.getStatus());
        existing.setCategories(categoryFromDto);
        return productRepository.save(existing);
    }

    public List<ProductModel> getAll(String name, Set<String> categories,String sortDir, Double lowStock, Integer page, Integer size){
        Query query = new Query();
        if(!isBlank(name)){
            query.addCriteria(Criteria.where("name").regex(name,"i"));
        }
        if(categories != null && !categories.isEmpty()){
            query.addCriteria(Criteria.where("categories").in(categories));
        }
        if(lowStock != null){
            query.addCriteria(Criteria.where("stockQuantity").lt(lowStock));
        }
        if("desc".equalsIgnoreCase(sortDir)){
            query.with(Sort.by(Sort.Direction.DESC,"price"));
        } else if ("asc".equalsIgnoreCase(sortDir)) {
            query.with(Sort.by(Sort.Direction.ASC,"price"));
        }
        if (page != null) {

            if (page < 0) {
                throw new BadRequestException("Page cannot be negative");
            }

            if (size == null) {
                size = 10; // 🔥 default size
            }

            if (size <= 0) {
                throw new BadRequestException("Size must be greater than 0");
            }
            query.with(PageRequest.of(page,size));
        }
        return mongoTemplate.find(query, ProductModel.class);
    }

    public String normalize(String s){
        return s == null ? null : s.trim();
    }

    public boolean isBlank(String s){
        return s == null || s.trim().isEmpty();
    }

    public ProductModel getById(String id){
        return productRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Category ot found for id: "+id));
    }

    public List<ProductModel>  getActive(){
        return productRepository.findByStatus(ProductStatus.ACTIVE);
    }
}
