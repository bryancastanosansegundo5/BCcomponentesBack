package com.mvc.model.configuracion;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "configuracion")
public class ConfiguracionVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clave;

    private String valor;
}
