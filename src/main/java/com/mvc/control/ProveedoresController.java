package com.mvc.control;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mvc.model.proovedor.ProveedorVO;
import com.mvc.service.proveedor.ProveedorService;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedoresController {

    @Autowired
    ProveedorService proveedorService;

    @GetMapping
    public List<ProveedorVO> getAllProveedores() {
        return proveedorService.findAll();
    }
}
