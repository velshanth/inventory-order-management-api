package com.guvi.repo;

import com.guvi.model.CategoryModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CategoryRepository extends MongoRepository<CategoryModel,String> {
boolean existsByName(String name);
List<CategoryModel> findByNameIn(Set<String> categoryNames);
List<CategoryModel> findByActiveTrue();
}
