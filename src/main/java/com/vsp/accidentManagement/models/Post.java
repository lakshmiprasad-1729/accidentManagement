package com.vsp.accidentManagement.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "posts") // Add this annotation
public class Post {

    @Id
    private ObjectId id;

    @Field("ownerid")
    private ObjectId ownerId;

    @Field("content")
    private String content;

    @Field("imageurl")
    private String imageUrl;

    @Field("location")
    private String location;

    @Field("status")
    private String status;

    // Add constructors
    public Post() {}

    public Post(ObjectId ownerId, String content, String imageUrl, String location, String status) {
        this.ownerId = ownerId;
        this.content = content;
        this.imageUrl = imageUrl;
        this.location = location;
        this.status = status;
    }

    // Add getters and setters
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public ObjectId getOwnerId() { return ownerId; }
    public void setOwnerId(ObjectId ownerId) { this.ownerId = ownerId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}