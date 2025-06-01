package com.mvc.model.categoria;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mvc.model.producto.ProductoVO;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "categoria")
public class CategoriaVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private boolean baja;

    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ProductoVO> productos;

}
