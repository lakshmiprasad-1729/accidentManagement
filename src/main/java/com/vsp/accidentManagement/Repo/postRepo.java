package com.vsp.accidentManagement.Repo;


import com.vsp.accidentManagement.models.Post;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface postRepo extends MongoRepository<Post, ObjectId> {
}
