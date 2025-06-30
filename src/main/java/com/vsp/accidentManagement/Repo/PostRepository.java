package com.vsp.accidentManagement.Repo;

import com.vsp.accidentManagement.models.Post;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends MongoRepository<Post, ObjectId> {




   List<Post> findByOwnerId(ObjectId id);


}