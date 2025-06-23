package com.vsp.accidentManagement.services;

import com.vsp.accidentManagement.Repo.postRepo;
import com.vsp.accidentManagement.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class postServices {

    @Autowired
    private postRepo postrepo;

    @Autowired
    private cloudinaryServices cloudinaryservice;

    public  List<Post> getAllPosts() {

        List<Post> posts = postrepo.findAll();
        return posts;
    }

    public void createANewPost(MultipartFile file) throws IOException {

        if(file.isEmpty()) throw new IllegalArgumentException("File cannot be empty");

        String fileName = file.getOriginalFilename();

        String uploadDir = "uploads/";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);


        Map<String,Object> map = cloudinaryservice.uploadFile(file);
        String imageUrl = map.get("secure_url") != null ?
                (String) map.get("secure_url") :
                (String) map.get("url");

        if(imageUrl != null){
            Files.delete(filePath);
        }
        else {
            throw new RuntimeException("Failed to upload file to Cloudinary");
        }

        Post post = new Post();

    }
}
