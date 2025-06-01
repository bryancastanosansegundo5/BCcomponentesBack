package com.mvc.error;

import com.mvc.model.error.ErrorDTO;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final List<ErrorDTO> erroresCapturados = new ArrayList<>();

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGlobalException(Exception ex, WebRequest request) {
        ErrorDTO error = new ErrorDTO(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error interno del servidor",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        // Guardar para luego mostrar desde el frontend
        erroresCapturados.add(error);

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static List<ErrorDTO> getErroresCapturados() {
        return erroresCapturados;
    }
}
