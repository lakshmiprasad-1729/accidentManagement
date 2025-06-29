package com.vsp.accidentManagement.Repo;


import com.vsp.accidentManagement.models.Post;
import com.vsp.accidentManagement.models.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//import java.lang.ScopedValue;

@Repository
public interface postRepo extends MongoRepository<Post, ObjectId> {
    Optional<Post> findById(String id);
    Optional<List<Post>> findByOwnerEmail(String email);
}
