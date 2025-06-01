package com.mvc.control;

import com.mvc.model.rol.RolVO;
import com.mvc.service.rol.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rol")
public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping(value = "/", produces = "application/json")
    public List<RolVO> listarTodos() {
        return rolService.obtenerTodos();
    }
}
