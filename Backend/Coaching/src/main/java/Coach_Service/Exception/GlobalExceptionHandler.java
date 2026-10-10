package Coach_Service.Exception;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Catch both 4xx (HttpClientErrorException) and 5xx (HttpServerErrorException) from RestClient
    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<String> handleRestClientException(HttpStatusCodeException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ex.getResponseBodyAsString());
    }
}