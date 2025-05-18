package com.mvc.model.enums;

public enum Transportista {
    SEUR("Seur", "/img/logosTransportistas/seur.svg"),
    DHL("DHL", "/img/logosTransportistas/dhl.svg"),
    MRW("MRW", "/img/logosTransportistas/mrw.svg"),
    GLS("GLS", "/img/logosTransportistas/gls.svg");

    private final String nombre;
    private final String logo;

    Transportista(String nombre, String logo) {
        this.nombre = nombre;
        this.logo = logo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getLogo() {
        return logo;
    }
}
