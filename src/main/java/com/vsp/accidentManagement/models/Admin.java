package com.vsp.accidentManagement.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.annotation.Collation;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document( collection = "admin")
@Getter
@Setter
public class Admin {

    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("id")
    private ObjectId id;

    @Field("adminEmail")
    private  String adminEmail;

    @Field("addedBy")
    private  ObjectId addedBy;

   public  Admin(String adminEmail, ObjectId addedBy) {
       this.adminEmail = adminEmail;
        this.addedBy = addedBy;
   }


}
