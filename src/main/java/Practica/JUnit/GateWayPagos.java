package Practica.JUnit;

public interface GateWayPagos {
    // Retorna true si el banco autoriza, false si rechaza
    boolean autorizar(String tarjeta, double monto);
}
