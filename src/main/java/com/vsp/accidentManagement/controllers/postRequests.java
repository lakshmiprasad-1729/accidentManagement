package com.vsp.accidentManagement.controllers;


import com.vsp.accidentManagement.models.Post;
import com.vsp.accidentManagement.services.postServices;
import org.springframework.beans.factory.annotation.Autowired;
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
    public void uploadPost(@RequestParam("image") MultipartFile image) throws IOException {
        System.out.println(image);
         postservices.createANewPost(image);
    }

    @GetMapping("/check-check")
    public  void checkcheck(){
        postservices.runAuth();
    }
}
