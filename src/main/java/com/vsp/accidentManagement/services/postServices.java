package com.vsp.accidentManagement.services;

import com.cloudinary.Api;
import com.vsp.accidentManagement.Entities.ApiResponse;
import com.vsp.accidentManagement.Repo.postRepo;
import com.vsp.accidentManagement.Repo.userRepo;
import com.vsp.accidentManagement.models.Post;
import com.vsp.accidentManagement.models.User;
import com.vsp.accidentManagement.models.locationStructure;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.vsp.accidentManagement.services.UserPrincipal;

import javax.net.ssl.HttpsURLConnection;

@RestController
public class postServices {

    @Autowired
    private postRepo postrepo;

    @Autowired
    private userRepo userrepo;


    @Autowired
    private cloudinaryServices cloudinaryservice;

    public  List<Post> getAllPosts() {

        List<Post> posts = postrepo.findAll();
        return posts;
    }

    public ResponseEntity<ApiResponse<Post>> createANewPost(MultipartFile file , String content, locationStructure location,String title,String address,String category,String priorityLevel,String name) throws IOException {

        ApiResponse<Post> res = new ApiResponse<>();
        res.setData(null);

        if(file.isEmpty()) {
            res.setMessage("File is empty");
            res.setStatus(false);
            return ResponseEntity.status(HttpsURLConnection.HTTP_BAD_REQUEST).body(res);
        }

        String fileName = file.getOriginalFilename();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();


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


        if(imageUrl == null){
            res.setMessage("error while saving image");
            res.setStatus(false);
            return ResponseEntity.status(HttpsURLConnection.HTTP_BAD_REQUEST).body(res);
        }

        if(imageUrl != null){
            Files.delete(filePath);
        }
        else {
            System.out.println("Failed to upload file to Cloudinary");
        }
//        String email, String content, String imageUrl, locationStructure location, String status,String category,String title,String priorityLevel,String address
        Post post = new Post(principal.getUsername(),content,imageUrl,location,"pending",category,title,priorityLevel,address,name);

        if(post == null){
            res.setMessage("error while creating post");
            res.setStatus(false);
            return ResponseEntity.status(HttpsURLConnection.HTTP_BAD_REQUEST).body(res);
        }

        Post savedPost = postrepo.save(post);

        if(savedPost == null){
            res.setMessage("error while saving post");
            res.setStatus(false);
            return ResponseEntity.status(HttpsURLConnection.HTTP_BAD_REQUEST).body(res);
        }

        res.setData(savedPost);
        res.setStatus(true);
        res.setMessage("saved post");

        System.out.println(post.getImageUrl());

        return   ResponseEntity.status(HttpsURLConnection.HTTP_OK)
                .header("Content-Type", "application/json")
                .body(res);

    }

    public ResponseEntity<ApiResponse<Post>> updateAPost(String content, String id) throws IOException {
        ApiResponse<Post> res = new ApiResponse<>();
        res.setData(null);

       Post post = postrepo.findById(id).orElse(null);

       if(post == null){
           res.setMessage("invalid post");
           res.setStatus(false);
           return ResponseEntity.status(HttpsURLConnection.HTTP_BAD_REQUEST).body(res);
       }

       User user = userrepo.findByEmail(post.ownerEmail()).orElse(null);
       System.out.println();

       if(user == null){
           res.setMessage("unable to fetch userdetails server error");
           res.setStatus(false);
           return ResponseEntity.status(HttpsURLConnection.HTTP_BAD_REQUEST).body(res);
       }

       if(!Objects.equals(user.getRole(), "admin")){
           res.setMessage("user should be admin to set a type of problem");
           res.setStatus(false);
           return ResponseEntity.status(HttpsURLConnection.HTTP_BAD_REQUEST).body(res);
       }
           post.setContent(content);

         Post savedPost = postrepo.save(post);

         if(savedPost == null){
             res.setMessage("error while saving post");
             res.setStatus(false);
             return ResponseEntity.status(HttpsURLConnection.HTTP_SERVER_ERROR).body(res);
         }

        res.setMessage("updated type successfully");
        res.setStatus(true);
        res.setData(savedPost);
        return ResponseEntity.status(HttpsURLConnection.HTTP_OK).body(res);

    }

    public ResponseEntity<ApiResponse<Post>> updateTypeByAdmin(String status,String id){
        ApiResponse<Post> res = new ApiResponse<>();
        res.setData(null);

        Post post = postrepo.findById(id).orElse(null);

        if(post == null){
            res.setMessage("invalid post");
            res.setStatus(false);
            return ResponseEntity.status(HttpsURLConnection.HTTP_BAD_REQUEST).body(res);
        }

        User user = userrepo.findById(id).orElse(null);

        if(user == null){
            res.setMessage("unable to fetch userdetails server error");
            res.setStatus(false);
            return ResponseEntity.status(HttpsURLConnection.HTTP_BAD_REQUEST).body(res);
        }

        if(user.getRole() != "admin"){
            res.setMessage("user should be admin to set a type of problem");
            res.setStatus(false);
            return ResponseEntity.status(HttpsURLConnection.HTTP_BAD_REQUEST).body(res);
        }
        post.setStatus(status);

        Post savedPost = postrepo.save(post);

        if(savedPost == null){
            res.setMessage("error while saving post");
            res.setStatus(false);
            return ResponseEntity.status(HttpsURLConnection.HTTP_SERVER_ERROR).body(res);
        }

        res.setMessage("updated type successfully");
        res.setStatus(true);
        res.setData(savedPost);
        return ResponseEntity.status(HttpsURLConnection.HTTP_OK).body(res);
    }
//
//    public ResponseEntity<ApiResponse<Post>> updateStatusByaFieldEmployee (String status,ObjectId id){
//        ApiResponse<Post> res = new ApiResponse<>();
//        res.setData(null);
//
//        Post post = postrepo.findById(id).orElse(null);
//
//        if(post == null){
//            res.setMessage("invalid post");
//            res.setStatus(false);
//            return ResponseEntity.status(HttpsURLConnection.HTTP_BAD_REQUEST).body(res);
//        }
//
//        User user = userrepo.findById(id).orElse(null);
//
//        if(user == null){
//            res.setMessage("unable to fetch userdetails server error");
//            res.setStatus(false);
//            return ResponseEntity.status(HttpsURLConnection.HTTP_BAD_REQUEST).body(res);
//        }
//
//        if(user.getRole() != "Employee"){
//            res.setMessage("user should be admin to set a type of problem");
//            res.setStatus(false);
//            return ResponseEntity.status(HttpsURLConnection.HTTP_BAD_REQUEST).body(res);
//        }
//        post.setStatusbyfieldemployee(status);
//
//        Post savedPost = postrepo.save(post);
//
//        if(savedPost == null){
//            res.setMessage("error while saving post");
//            res.setStatus(false);
//            return ResponseEntity.status(HttpsURLConnection.HTTP_SERVER_ERROR).body(res);
//        }
//
//        res.setMessage("updated type successfully");
//        res.setStatus(true);
//        res.setData(savedPost);
//        return ResponseEntity.status(HttpsURLConnection.HTTP_BAD_REQUEST).body(res);
//    }
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();



        return principal.getUsername();
    }

    public  ResponseEntity<ApiResponse<List<Post>>> getUsersPost(){
        ApiResponse<List<Post>> res = new ApiResponse<>();
        res.setData(null);

        String email = getCurrentUsername();

      List<Post> posts = postrepo.findByOwnerEmail(email).orElse(null);

        if(posts == null || posts.isEmpty()){
            res.setMessage("error while getting post");
            res.setStatus(false);
            return ResponseEntity.status(HttpsURLConnection.HTTP_SERVER_ERROR).body(res);
        }

        res.setMessage("recieved posts");
        res.setStatus(true);
        res.setData(posts);
        return ResponseEntity.status(HttpsURLConnection.HTTP_BAD_REQUEST).body(res);
    }
}
