package com.vsp.accidentManagement.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsp.accidentManagement.Entities.ApiResponse;
import com.vsp.accidentManagement.Entities.updateContent;
import com.vsp.accidentManagement.models.Post;
import com.vsp.accidentManagement.models.locationStructure;
import com.vsp.accidentManagement.services.postServices;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class postRequests {

    @Autowired
    postServices postservices;


    @GetMapping("/all-posts")
    public List<Post> AllPosts() {
        return postservices.getAllPosts();
    }

    @PostMapping("/upload-post")
    public ResponseEntity<ApiResponse<Post>> uploadPost(@RequestParam("image") MultipartFile image, @RequestParam("content") String content, @RequestParam("location") String location,String title,
                                                        String address,String category,String priorityLevel,String name) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        locationStructure loc = null;
        try {
            loc = objectMapper.readValue(location, locationStructure.class);
        } catch (JsonProcessingException e) {
            // Handle parsing error
            System.out.println(e.getMessage());
        }
       return  postservices.createANewPost( image ,  content,  loc, title, address, category, priorityLevel,name);
    }

    @PutMapping("/user-post/update-content/{id}")
    public ResponseEntity<ApiResponse<Post>> updateContent(@PathVariable String id,@RequestBody updateContent content) throws IOException {
        return  postservices.updateAPost(content.getContent(),id);
    }

    @PutMapping("/user-post/update-type/{id}/{type}")
    public ResponseEntity<ApiResponse<Post>> updateType(@PathVariable String id,@PathVariable String type ) throws IOException {
        return  postservices.updateTypeByAdmin(type,id);
    }

//    @PutMapping("/user-post/update-statusbyemployee/{id}/{status}")
//    public ResponseEntity<ApiResponse<Post>> updateStatusByEmployee(@PathVariable ObjectId id,@PathVariable String status ) throws IOException {
//        return  postservices.updateStatusByaFieldEmployee(status,id);
//    }

    @GetMapping("/user-posts")
    public  ResponseEntity<ApiResponse<List<Post>>> getUserPosts(){
        return postservices.getUsersPost();
    }


    @GetMapping("/check-auth")
    public  String checkAuth(){
        return postservices.getCurrentUsername();
    }
}
