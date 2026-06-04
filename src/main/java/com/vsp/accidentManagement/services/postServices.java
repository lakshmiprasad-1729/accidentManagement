package com.vsp.accidentManagement.services;

import com.vsp.accidentManagement.Entities.ApiResponse;
import com.vsp.accidentManagement.Repo.PostRepository;
import com.vsp.accidentManagement.Repo.userRepo;
import com.vsp.accidentManagement.models.Post;
import com.vsp.accidentManagement.models.User;
import com.vsp.accidentManagement.models.LocationStructure;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

@Service
public class postServices {

    @Autowired
    private PostRepository postrepo;

    @Autowired
    private userRepo userrepo;


    @Autowired
    private cloudinaryServices cloudinaryservice;

    public  List<Post> getAllPosts() {

        List<Post> posts = postrepo.findAll();
        return posts;
    }

    public ResponseEntity<ApiResponse<Post>> createANewPost(MultipartFile file , String content, LocationStructure location, String title, String address, String category, String priorityLevel, String name) throws IOException {

        ApiResponse<Post> res = new ApiResponse<>();
        res.setData(null);

        if(file.isEmpty()) {
            res.setMessage("File is empty");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
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




        if(imageUrl != null){
            Files.delete(filePath);
        }
        else {
            System.out.println("Failed to upload file to Cloudinary");
        }

        User user = userrepo.findByEmail(principal.getUsername()).orElse(null);

        if(user == null){
            res.setMessage("internal server error");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }

        Post post = new Post( name,  title,  content,
                 imageUrl,  location,  address,
                 false,  category,  priorityLevel,user.getId());

        if(post == null){
            res.setMessage("error while creating post");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        Post savedPost = postrepo.save(post);

        if(savedPost == null){
            res.setMessage("error while saving post");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        res.setData(savedPost);
        res.setStatus(true);
        res.setMessage("saved post");

        System.out.println(post.getImageUrl());

        return   ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(res);

    }

    public ResponseEntity<ApiResponse<Post>> updateAPost(String content, ObjectId id) throws IOException {
        ApiResponse<Post> res = new ApiResponse<>();
        res.setData(null);

       Post post = postrepo.findById(id).orElse(null);

       if(post == null){
           res.setMessage("invalid post");
           res.setStatus(false);
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
       }

       User user = userrepo.findById(post.getOwnerId()).orElse(null);

       if(user == null){
           res.setMessage("unable to fetch userdetails server error");
           res.setStatus(false);
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
       }

       if(!user.getRole().equals("admin")){
           res.setMessage("user should be admin to set a type of problem");
           res.setStatus(false);
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
       }
           post.setContent(content);

         Post savedPost = postrepo.save(post);

         if(savedPost == null){
             res.setMessage("error while saving post");
             res.setStatus(false);
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
         }

        res.setMessage("updated type successfully");
        res.setStatus(true);
        res.setData(savedPost);
        return ResponseEntity.status(HttpStatus.OK).body(res);

    }

    public ResponseEntity<ApiResponse<Post>> updateTypeByAdmin(Boolean status,ObjectId id){
        ApiResponse<Post> res = new ApiResponse<>();
        res.setData(null);

        Post post = postrepo.findById(id).orElse(null);

        if(post == null){
            res.setMessage("invalid post");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            res.setMessage("unauthorized");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        }

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        User user = userrepo.findByEmail(principal.getUsername()).orElse(null);

        if(user == null){
            res.setMessage("unable to fetch userdetails server error");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }

        if(!user.getRole().equals("admin")){
            res.setMessage("user should be admin to set a type of problem");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }
        post.setStatus(status);

        Post savedPost = postrepo.save(post);

        if(savedPost == null){
            res.setMessage("error while saving post");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }

        res.setMessage("updated type successfully");
        res.setStatus(true);
        res.setData(savedPost);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

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

        User user = userrepo.findByEmail(email).orElse(null);

        if(user == null){
            res.setMessage("error while fetching userdetails in post");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }

      List<Post> posts = postrepo.findByOwnerId(user.getId());

        if(posts == null ){
            res.setMessage("error while getting post");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }

        res.setMessage("recieved posts");
        res.setStatus(true);
        res.setData(posts);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    public Post getPostByUserId(String id){

        if(id == null){
            return null;
        }

        ObjectId postId = new ObjectId(id);
        Post userPost = postrepo.findById(postId).orElse(null);

        return userPost;

    }

    public ResponseEntity<ApiResponse<String>> deletePost(ObjectId id) {
        ApiResponse<String> res = new ApiResponse<>();
        res.setData(null);

        Post post = postrepo.findById(id).orElse(null);

        if (post == null) {
            res.setMessage("post not found");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            res.setMessage("unauthorized");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        }

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userrepo.findByEmail(principal.getUsername()).orElse(null);

        if (user == null) {
            res.setMessage("unable to fetch user details");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }

        // Allow deletion if user is the owner or an admin
        boolean isOwner = post.getOwnerId().equals(user.getId());
        boolean isAdmin = user.getRole().equals("admin");

        if (!isOwner && !isAdmin) {
            res.setMessage("you can only delete your own posts");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
        }

        postrepo.deleteById(id);
        res.setMessage("post deleted successfully");
        res.setStatus(true);
        res.setData(id.toString());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    public ResponseEntity<ApiResponse<List<Post>>> getApprovedPosts() {
        ApiResponse<List<Post>> res = new ApiResponse<>();

        List<Post> posts = postrepo.findByStatus(true);
        res.setMessage("approved posts retrieved successfully");
        res.setStatus(true);
        res.setData(posts);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    public ResponseEntity<ApiResponse<List<Post>>> getPendingPosts() {
        ApiResponse<List<Post>> res = new ApiResponse<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            res.setMessage("unauthorized");
            res.setStatus(false);
            res.setData(null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        }

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userrepo.findByEmail(principal.getUsername()).orElse(null);

        if (user == null || !user.getRole().equals("admin")) {
            res.setMessage("only admins can view pending posts");
            res.setStatus(false);
            res.setData(null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
        }

        List<Post> posts = postrepo.findByStatus(false);
        res.setMessage("pending posts retrieved successfully");
        res.setStatus(true);
        res.setData(posts);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
