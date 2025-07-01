package com.vsp.accidentManagement.services;

import com.vsp.accidentManagement.Repo.AdminRepository;
import com.vsp.accidentManagement.Repo.userRepo;
import com.vsp.accidentManagement.models.Admin;
import com.vsp.accidentManagement.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AdminServices {

        @Autowired
        private AdminRepository adminrepo;

        @Autowired
        private userRepo userrepo;

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
}
