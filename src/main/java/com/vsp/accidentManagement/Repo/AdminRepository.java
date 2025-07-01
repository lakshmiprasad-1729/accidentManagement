package com.vsp.accidentManagement.Repo;

import com.vsp.accidentManagement.models.Admin;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AdminRepository extends MongoRepository<Admin, ObjectId> {

    Admin findByAdminEmail(String adminEmail);

    @Query(value="{}", fields="{ 'adminEmail' : 1, '_id' : 0 }")
    List<Admin> findAllEmails();

}