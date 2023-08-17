package co.com.jorge.springboot.webflux.app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClassCastException.class)
    @ResponseBody
    public Mono<ResponseEntity<Map<String, Object>>> handleDecodingException(ClassCastException ex) {
        Map<String, Object> errorMessage = new HashMap<>();
        errorMessage.put("errors", Arrays.asList("Payload invalido"));
        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorMessage));
    }
}