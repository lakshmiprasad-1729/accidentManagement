package com.vsp.accidentManagement.services;

import com.vsp.accidentManagement.models.LoginRequest;
import com.vsp.accidentManagement.utilities.JWTutil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vsp.accidentManagement.Repo.userRepo;
import com.vsp.accidentManagement.models.User;
import com.vsp.accidentManagement.models.userDetails;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class userServices {

        @Autowired
        private userRepo  userrepo;

        @Autowired
        private JWTutil jwtutil;


        @Autowired
        AuthenticationManager authenticationManager;
//        private Authentication auth;

        private PasswordEncoder encoder = new BCryptPasswordEncoder();

    public  userDetails saveUser(User user){


        if(user.getName() == null || user.getEmail() == null || user.getRole() == null) {

            System.out.println("User details are incomplete.");
            return null; 
        }
         String hashedPassword = encoder.encode(user.getPassword());

        User userdetails =  new User(user.getName(),user.getEmail(),hashedPassword,user.getRole());
        User savedUser =  userrepo.save(userdetails);

        userDetails savedUserDetails = new userDetails(user.getId(),user.getName(),user.getEmail(),user.getName());
         return savedUserDetails;
    }


    public userDetails getUserById(ObjectId userId) {
        User byId;
        byId =  userrepo.findById(userId)
                .orElseThrow(()-> new RuntimeException(("user not found" + userId)));


        return new userDetails(byId.getId(),byId.getName(),byId.getEmail(),byId.getRole());
    }

    public String verifyUser(LoginRequest userLogin, HttpServletResponse response){

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword()));

        if(authentication.isAuthenticated()) {

        String jwttoken = jwtutil.generateToken(userLogin.getEmail());



            ResponseCookie cookie = ResponseCookie.from("auth-token", jwttoken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(Duration.ofHours(24))
                    .sameSite("None")  // Required for cross-site cookies
                    .build();

            response.addHeader("Set-Cookie", cookie.toString() + "; Partitioned");

            return jwttoken;

        }

             return  "false";


    }

    public  List<User> getAllUsers(){

        List<User> users = userrepo.findAll();

        System.out.println(users);

        return  users;
    }

    public userDetails getUserByEmail(String email){

        try {
            User user = userrepo.findByEmail(email)
                    .orElseThrow(()-> new RuntimeException("user not found"));

            return new userDetails(user.getId(),user.getName(),user.getEmail(),user.getRole());

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public userDetails getUser() {
         try{
             Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

             UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

             Optional<User> user = userrepo.findByEmail(principal.getUsername());

             if(!user.isPresent()) {
                 System.out.println("user not found");
             }

             return new userDetails(user.get().getId(),user.get().getName(),user.get().getEmail(),user.get().getRole());
         }
         catch (RuntimeException e){
             System.out.println(e);
             return null;
         }
    }
}
