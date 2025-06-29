package com.vsp.accidentManagement.models;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;


@Component
@Getter
@Setter
public class userDetails {
    
    public ObjectId id;
    public String name;
    public String email;
    public String role; 

    public userDetails() {
    }
    public userDetails(ObjectId id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}
