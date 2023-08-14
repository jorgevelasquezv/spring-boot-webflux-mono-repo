package co.com.jorge.springboot.webflux.app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClassCastException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleDecodingException(ClassCastException ex) {
        Map<String, Object> errorMessage = new HashMap<>();
        errorMessage.put("error", "Payload invalido");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
}