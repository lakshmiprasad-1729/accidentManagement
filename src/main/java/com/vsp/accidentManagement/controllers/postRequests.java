package com.vsp.accidentManagement.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vsp.accidentManagement.Entities.ApiResponse;
import com.vsp.accidentManagement.Entities.updateContent;
import com.vsp.accidentManagement.models.Post;
import com.vsp.accidentManagement.models.LocationStructure;
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
    public ResponseEntity<ApiResponse<Post>> uploadPost(@RequestParam("image") MultipartFile image, @RequestParam("content") String content, @RequestParam("location") String location, @RequestParam("title") String title,
                                                        @RequestParam("address") String address, @RequestParam("category") String category, @RequestParam("priorityLevel") String priorityLevel, @RequestParam("name") String name) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        LocationStructure loc = null;
        try {
            loc = objectMapper.readValue(location, LocationStructure.class);
        } catch (JsonProcessingException e) {
            // Handle parsing error
            System.out.println(e.getMessage());
        }
       return  postservices.createANewPost( image ,  content,  loc, title, address, category, priorityLevel,name);
    }

    @PutMapping("/user-post/update-content/{id}")
    public ResponseEntity<ApiResponse<Post>> updateContent(@PathVariable String id,@RequestBody updateContent content) throws IOException {
        ObjectId newid = new ObjectId(id);
        return  postservices.updateAPost(content.getContent(),newid);
    }

    @PutMapping("/user-post/update-status/{id}")
    public ResponseEntity<ApiResponse<Post>> updateStatus(@PathVariable String id ) throws IOException {
        ObjectId newid = new ObjectId(id);
        return  postservices.updateTypeByAdmin(true,newid);
    }

    @GetMapping("/user-post/getall-userspost")
    public  ResponseEntity<ApiResponse<List<Post>>> getUserPosts(){
        System.out.println("entry1");
        return postservices.getUsersPost();
    }

    @GetMapping("/user-post/{id}")
    public Post findPostByuserId(@PathVariable String id){
        return postservices.getPostByUserId(id);
    }

    @DeleteMapping("/user-post/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deletePost(@PathVariable String id) {
        ObjectId newid = new ObjectId(id);
        return postservices.deletePost(newid);
    }

    @GetMapping("/approved")
    public ResponseEntity<ApiResponse<List<Post>>> getApprovedPosts() {
        return postservices.getApprovedPosts();
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<Post>>> getPendingPosts() {
        return postservices.getPendingPosts();
    }

    @GetMapping("/check-auth")
    public  String checkAuth(){
        return postservices.getCurrentUsername();
    }
}
