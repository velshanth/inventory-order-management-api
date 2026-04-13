package com.guvi.repo;

import com.guvi.model.CategoryModel;
import com.guvi.model.ProductModel;
import com.guvi.model.ProductStatus;
import org.springframework.data.mongodb.core.aggregation.BooleanOperators;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<ProductModel,String> {
    Optional<ProductModel> findById(String id);
    boolean existsByName(String name);
    List<ProductModel> findByStatus(ProductStatus status);
}
