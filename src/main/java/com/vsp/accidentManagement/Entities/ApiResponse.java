package  com.vsp.accidentManagement.Entities;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    public ApiResponse() {

    }

}

