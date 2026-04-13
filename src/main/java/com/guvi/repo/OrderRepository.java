package com.guvi.repo;

import com.guvi.model.OrderModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<OrderModel,String> {
    boolean existsById(String id);
    List<OrderModel> findByUserId(String userId);
}
