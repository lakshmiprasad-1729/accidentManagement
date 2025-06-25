package  com.vsp.accidentManagement.Entities;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ApiResponse<T> {
    @JsonProperty("status")
    private boolean status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private T data;

    // constructors, getters, setters
    public ApiResponse(Boolean success, String message, T data) {
        this.status = success;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(){

    }

    public  void setStatus(Boolean success) {
        this.status = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }
}

//@PostMapping("/users")
//public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User user) {
//    User savedUser = userService.save(user);
//    ApiResponse<User> response = new ApiResponse<>(true, "User created successfully", savedUser);
//    return ResponseEntity.status(HttpStatus.CREATED).body(response);
//}