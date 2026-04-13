package com.guvi.repo;

import com.guvi.model.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository  extends MongoRepository<UserModel,String> {
    boolean existsByEmailIgnoreCase(String email);

    Optional<UserModel> findByEmailIgnoreCase(String email);
}
