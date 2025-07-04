package com.vsp.accidentManagement.controllers;

import com.vsp.accidentManagement.Entities.ApiResponse;
import com.vsp.accidentManagement.models.*;
import com.vsp.accidentManagement.services.userServices;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class userRequests {

    @Autowired
    userServices userservice;

    @PostMapping("/register")
    ResponseEntity<ApiResponse<userDetails>> Register(@RequestBody RegisterRequest user){
      return userservice.saveUser(user);
    }

    @PostMapping("/login")
    String login(@RequestBody LoginRequest userLogin, HttpServletResponse response){
        return userservice.verifyUser(userLogin,response);
    }
    @GetMapping
    String greeting(){
        return "welcome users page";
    }

    @GetMapping("/user-details/userid/{userId}")
    userDetails  loginUserDetails(@PathVariable ObjectId userId){
        return userservice.getUserById(userId);
    }

    @GetMapping("/user-details/email")
    userDetails  userDetailsByEmail(@RequestBody emailDetails email){
        return userservice.getUserByEmail(email.getEmail());
    }

    @GetMapping("/user-details/getUser")
    userDetails getUserDetails(){
        return userservice.getUser();
    }

    @GetMapping("/user-details/allUsers")
    List<User> allUsers(){
        return userservice.getAllUsers();
    }
    
}

