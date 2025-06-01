package com.mvc.model.estadisticas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumenEstadisticasDTO {
    private double totalVentas;
    private long totalPedidos;
    private long totalUsuarios;
}
