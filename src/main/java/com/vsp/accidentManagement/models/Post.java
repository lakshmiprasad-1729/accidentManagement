package com.vsp.accidentManagement.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Document(collection = "posts")
@Data // Combines @Getter, @Setter, @ToString, @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("id")
    private ObjectId id;

    @Field("name")
    private String name;

    @Field("title")
    private String title;

    @Field("ownerEmail")
    private String ownerEmail;

    @Field("content")
    private String content;

    @Field("imageurl")
    private String imageUrl;

    @Field("location")
    private LocationStructure location;

    @Field("address")
    private String address;

    @Field("status")
    private Boolean status;

    @Field("category")
    private String category;

    @Field("priorityLevel")
    private String priorityLevel;

    @CreatedDate
    @Field("createDate")
    private LocalDateTime createDate;


    public String getFormattedCreateDate() {
        return createDate != null ? createDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
    }

    // Custom constructor for creating new posts (without ID)
    public Post(String name, String title, String ownerEmail, String content,
                String imageUrl, LocationStructure location, String address,
                Boolean status, String category, String priorityLevel) {
        this.name = name;
        this.title = title;
        this.ownerEmail = ownerEmail;
        this.content = content;
        this.imageUrl = imageUrl;
        this.location = location;
        this.address = address;
        this.status = status;
        this.category = category;
        this.priorityLevel = priorityLevel;
    }
}