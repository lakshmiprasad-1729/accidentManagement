package com.vsp.accidentManagement.services;

import com.vsp.accidentManagement.Entities.ApiResponse;
import com.vsp.accidentManagement.Repo.AdminRepository;
import com.vsp.accidentManagement.models.*;
import com.vsp.accidentManagement.utilities.JWTutil;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vsp.accidentManagement.Repo.userRepo;

import java.util.List;
import java.util.Optional;

@Service
public class userServices {

        @Autowired
        private AdminRepository adminrepo;

        @Autowired
        private userRepo  userrepo;

        @Autowired
        private JWTutil jwtutil;

        @Autowired
        AuthenticationManager authenticationManager;

        @Autowired
        private PasswordEncoder encoder;

    public ResponseEntity<ApiResponse<userDetails>> saveUser(RegisterRequest user){

        Optional<User> checkuser = userrepo.findByEmail(user.getEmail());
        ApiResponse<userDetails> res = new ApiResponse<>();
        res.setData(null);

        if(checkuser.isPresent()){
            System.out.println(checkuser.get().getName());
            res.setMessage("User with  same email already exists");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(res);
        }

        String role = "guest";
        Admin admin =  adminrepo.findByAdminEmail(user.getEmail());

        if(admin!=null && admin.getAdminEmail().equals(user.getEmail())){
            role = "admin";
        }


        if(user.getName() == null || user.getEmail() == null  || user.getEmail().isEmpty()) {

            System.out.println("User details are incomplete.");
            return null;
        }
         String hashedPassword = encoder.encode(user.getPassword());

        User userdetails =  new User(user.getName(),user.getEmail(),hashedPassword,role);
        User savedUser =  userrepo.save(userdetails);

        if(savedUser==null){
            res.setMessage("error while saving User");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(res);
        }

        String id =  savedUser.getId().toString();

        userDetails savedUserDetails = new userDetails(id,user.getName(),user.getEmail(),role);
        res.setMessage("successfully saved user");
        res.setData(savedUserDetails);
        res.setStatus(true);
         return ResponseEntity.status(HttpStatus.OK).body(res);
    }


    public userDetails getUserById(ObjectId userId) {
        User byId;
        byId =  userrepo.findById(userId)
                .orElseThrow(()-> new RuntimeException(("user not found" + userId)));


        String id =  byId.getId().toString();

        return new userDetails(id,byId.getName(),byId.getEmail(),byId.getRole());
    }

    public String verifyUser(LoginRequest userLogin, HttpServletResponse response){

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword()));

        if(authentication.isAuthenticated()) {

        String jwttoken = jwtutil.generateToken(userLogin.getEmail());

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


            String id =  user.getId().toString();

            return new userDetails(id,user.getName(),user.getEmail(),user.getRole());

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

             return new userDetails(user.get().getId().toString(),user.get().getName(),user.get().getEmail(),user.get().getRole());
         }
         catch (RuntimeException e){
             System.out.println(e);
             return null;
         }
    }

    public ResponseEntity<ApiResponse<userDetails>> updateProfile(UpdateProfileRequest profileRequest) {
        ApiResponse<userDetails> res = new ApiResponse<>();
        res.setData(null);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            res.setMessage("unauthorized");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        }

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userrepo.findByEmail(principal.getUsername()).orElse(null);

        if (user == null) {
            res.setMessage("user not found");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }

        if (profileRequest.getName() == null || profileRequest.getName().trim().isEmpty()) {
            res.setMessage("name cannot be empty");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        user.setName(profileRequest.getName().trim());
        User savedUser = userrepo.save(user);

        userDetails details = new userDetails(
                savedUser.getId().toString(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );

        res.setMessage("profile updated successfully");
        res.setStatus(true);
        res.setData(details);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    public ResponseEntity<ApiResponse<String>> changePassword(ChangePasswordRequest passwordRequest) {
        ApiResponse<String> res = new ApiResponse<>();
        res.setData(null);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            res.setMessage("unauthorized");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        }

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userrepo.findByEmail(principal.getUsername()).orElse(null);

        if (user == null) {
            res.setMessage("user not found");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }

        // Verify old password
        if (!encoder.matches(passwordRequest.getOldPassword(), user.getPassword())) {
            res.setMessage("current password is incorrect");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        if (passwordRequest.getNewPassword() == null || passwordRequest.getNewPassword().length() < 6) {
            res.setMessage("new password must be at least 6 characters");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        user.setPassword(encoder.encode(passwordRequest.getNewPassword()));
        userrepo.save(user);

        res.setMessage("password changed successfully");
        res.setStatus(true);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    public ResponseEntity<ApiResponse<String>> deleteUser(ObjectId userId) {
        ApiResponse<String> res = new ApiResponse<>();
        res.setData(null);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            res.setMessage("unauthorized");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        }

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User currentUser = userrepo.findByEmail(principal.getUsername()).orElse(null);

        if (currentUser == null || !currentUser.getRole().equals("admin")) {
            res.setMessage("only admins can delete users");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
        }

        User targetUser = userrepo.findById(userId).orElse(null);
        if (targetUser == null) {
            res.setMessage("user not found");
            res.setStatus(false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }

        userrepo.deleteById(userId);
        res.setMessage("user deleted successfully");
        res.setStatus(true);
        res.setData(userId.toString());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
