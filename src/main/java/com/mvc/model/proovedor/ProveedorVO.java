package com.mvc.model.proovedor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mvc.model.producto.ProductoVO;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "proveedor")
public class ProveedorVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private boolean baja;

    @OneToMany(mappedBy = "proveedor", fetch = FetchType.LAZY)
@JsonManagedReference
private List<ProductoVO> productos;

}
