package com.vsp.accidentManagement.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexOptions;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.stereotype.Indexed;


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
    private  String status;



}
