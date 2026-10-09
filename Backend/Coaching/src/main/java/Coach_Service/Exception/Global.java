package Coach_Service.Exception;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice // <--- THIS WAS MISSING!
public class Global {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> HttpClientErrorException(HttpClientErrorException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ex.getResponseBodyAsString());
    }
}