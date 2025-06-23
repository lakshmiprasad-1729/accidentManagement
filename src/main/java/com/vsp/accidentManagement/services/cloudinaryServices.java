package  com.vsp.accidentManagement.services;


import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class cloudinaryServices {

    @Autowired
    private Cloudinary cloudinary;

    public Map<String, Object> uploadFile(MultipartFile file) {
        try {
            Map<String, Object> uploadParams = new HashMap<>();
            uploadParams.put("resource_type", "auto");

            return (Map<String, Object>) cloudinary.uploader()
                    .upload(file.getBytes(), uploadParams);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to Cloudinary", e);
        }
    }
}