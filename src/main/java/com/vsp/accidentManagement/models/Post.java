package com.vsp.accidentManagement.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection = "posts")
@Getter
@Setter// Add this annotation
public class Post {

    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("id")
    private ObjectId id;
    @Field("name")
    private  String name;
    @Field("title")
    private String title;

    @Field("ownerEmail")
    private String ownerEmail;

    @Field("content")
    private String content;

    @Field("imageurl")
    private String imageUrl;

    @Field("location")
    private locationStructure location;

    @Field("address")
    private String address;

    @Field("status")
    private String status;


    @Field("Category")
    private   String category;

    @Field("Priority Level")
    private  String priorityLevel;
    // Add constructors


    public Post(String email, String content, String imageUrl, locationStructure location, String status,String category,String title,String priorityLevel,String address,String name) {
        this.ownerEmail = email;
        this.content = content;
        this.imageUrl = imageUrl;
        this.location = location;
        this.status = status;
        this.category = category;
        this.title = title;
        this.priorityLevel = priorityLevel;
        this.address = address;
        this.name=name;

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

    public locationStructure getLocation() { return location; }
    public void setLocation(locationStructure location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getType() { return category; }
    public void setType(String category) { this.category = category; }

   }


