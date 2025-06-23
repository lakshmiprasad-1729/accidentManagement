package com.vsp.accidentManagement.Repo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.vsp.accidentManagement.models.User;

import java.util.Optional;

@Repository
    public interface userRepo extends MongoRepository<User, ObjectId> {
        // You can add custom query methods here if needed


    Optional<User> findByEmail(String email);
}