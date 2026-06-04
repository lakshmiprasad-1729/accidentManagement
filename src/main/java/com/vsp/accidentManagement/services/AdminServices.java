package com.vsp.accidentManagement.services;

import com.vsp.accidentManagement.Entities.ApiResponse;
import com.vsp.accidentManagement.Entities.DashboardStats;
import com.vsp.accidentManagement.Repo.AdminRepository;
import com.vsp.accidentManagement.Repo.PostRepository;
import com.vsp.accidentManagement.Repo.userRepo;
import com.vsp.accidentManagement.models.Admin;
import com.vsp.accidentManagement.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServices {

        @Autowired
        private AdminRepository adminrepo;

        @Autowired
        private userRepo userrepo;

        @Autowired
        private PostRepository postrepo;

        public Admin addAdmin(String email){

            Admin adminexist = adminrepo.findByAdminEmail(email);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }

            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

            if(adminexist != null){
                return null;
            }

            User user = userrepo.findByEmail(principal.getUsername()).orElse(null);

            if(user == null){
                return null;
            }

            Admin newadmin = adminrepo.save(new Admin(email,user.getId()));

            System.out.println(newadmin.getAdminEmail());

            return newadmin;
        }

        public List<String> getAllAdminsEmail(){
            List<Admin> usersWithEmailOnly = adminrepo.findAllEmails();

            // Use a Java Stream to extract the emails into a List<String>.
            List<String> emailList = usersWithEmailOnly.stream()
                    .map(Admin::getAdminEmail)
                    .collect(Collectors.toList());


            return emailList;
        }

        public ResponseEntity<ApiResponse<String>> removeAdmin(String email) {
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
                res.setMessage("only admins can remove other admins");
                res.setStatus(false);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
            }

            Admin admin = adminrepo.findByAdminEmail(email);
            if (admin == null) {
                res.setMessage("admin not found with email: " + email);
                res.setStatus(false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
            }

            adminrepo.deleteByAdminEmail(email);
            res.setMessage("admin removed successfully");
            res.setStatus(true);
            res.setData(email);
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }

        public ResponseEntity<ApiResponse<DashboardStats>> getDashboardStats() {
            ApiResponse<DashboardStats> res = new ApiResponse<>();

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                res.setMessage("unauthorized");
                res.setStatus(false);
                res.setData(null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
            }

            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            User currentUser = userrepo.findByEmail(principal.getUsername()).orElse(null);

            if (currentUser == null || !currentUser.getRole().equals("admin")) {
                res.setMessage("only admins can view dashboard stats");
                res.setStatus(false);
                res.setData(null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
            }

            long totalUsers = userrepo.count();
            long totalPosts = postrepo.count();
            long approvedPosts = postrepo.findByStatus(true).size();
            long pendingPosts = postrepo.findByStatus(false).size();

            DashboardStats stats = new DashboardStats(totalUsers, totalPosts, pendingPosts, approvedPosts);
            res.setMessage("dashboard stats retrieved successfully");
            res.setStatus(true);
            res.setData(stats);
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
}
