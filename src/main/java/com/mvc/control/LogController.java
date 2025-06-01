package com.mvc.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvc.error.GlobalExceptionHandler;
import com.mvc.model.error.ErrorDTO;
import com.mvc.model.logs.LoginLogDTO;
import com.mvc.service.usuario.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    @Autowired
    private UsuarioService usuarioService;

    private static final Path LOG_PATH_LOGIN = Paths.get("logs/login.json");

    @GetMapping("/login")
    public ResponseEntity<List<LoginLogDTO>> leerLogsDeLogin(HttpServletRequest request) {
        Long usuarioId = (Long) request.getAttribute("usuarioId");
        usuarioService.verificarRol(usuarioId, 3); // Solo ADMIN

        List<LoginLogDTO> logs = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            if (Files.notExists(LOG_PATH_LOGIN)) {
                Files.createDirectories(LOG_PATH_LOGIN.getParent());
                Files.createFile(LOG_PATH_LOGIN);
                return ResponseEntity.ok(logs);
            }

            try (BufferedReader reader = Files.newBufferedReader(LOG_PATH_LOGIN)) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    LoginLogDTO entry = mapper.readValue(linea, LoginLogDTO.class);
                    logs.add(entry);
                }
            }
            Collections.reverse(logs);
            return ResponseEntity.ok(logs);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/error")
    public List<ErrorDTO> obtenerErrores() {
        return GlobalExceptionHandler.getErroresCapturados();
    }

    @GetMapping("/forzar-error")
    public String forzarExcepcion() {
        throw new RuntimeException("Excepción de prueba lanzada manualmente");
    }

}
