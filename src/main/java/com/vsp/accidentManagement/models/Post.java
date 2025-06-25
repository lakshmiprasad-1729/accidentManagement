package com.vsp.accidentManagement.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "posts") // Add this annotation
public class Post {

    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("id")
    private ObjectId id;

    @Field("ownerEmail")
    private String ownerEmail;

    @Field("content")
    private String content;

    @Field("imageurl")
    private String imageUrl;

    @Field("location")
    private String location;

    @Field("status")
    private String status;

    @Field("statusbyfieldemployee")
    private String statusbyfieldemployee;

    @Field("Type")
    private   String type;
    // Add constructors
    public Post() {}

    public Post(String email, String content, String imageUrl, String location, String status,String type,String statusbyfieldemployee) {
        this.ownerEmail = email;
        this.content = content;
        this.imageUrl = imageUrl;
        this.location = location;
        this.status = status;
        this.type = type;
        this.statusbyfieldemployee = statusbyfieldemployee;
    }

    // Add getters and setters
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public String ownerEmail() { return ownerEmail; }
    public void setownerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatusbyfieldemployee(){return  statusbyfieldemployee; }
    public void setStatusbyfieldemployee(String statusbyfieldemployee){this.statusbyfieldemployee = statusbyfieldemployee;}
}