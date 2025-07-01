package com.vsp.accidentManagement.controllers;

import com.vsp.accidentManagement.models.Admin;
import com.vsp.accidentManagement.services.AdminServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminRequests {

    @Autowired
    private AdminServices adminservices;

    @PostMapping("/add-admin/{email}")
    public Admin addAdmin(@PathVariable String email){
        return adminservices.addAdmin(email);
    }

    @GetMapping("/get-admin-emails")
    public List<String> getAdminEmails(){
        return adminservices.getAllAdminsEmail();
    }

}
