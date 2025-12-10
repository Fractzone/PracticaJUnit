package Practica.JUnit;

public class ProcesadorDePagos {
    private GateWayPagos gateway;

    // Inyección por constructor (Vital para testear)
    public ProcesadorDePagos(GateWayPagos gateway) {
        this.gateway = gateway;
    }

    public void cobrar(String tarjeta, double monto) {
        // Regla de Negocio 1: No procesar montos negativos
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser positivo");
        }

        //Regla del negocio 2
        if(monto > 10000){
            throw new IllegalArgumentException("Monto sospechoso, requiere revision manual");
        }

        // Llamada a la capa externa (Aquí usaremos Mockito luego)
        boolean autorizado = gateway.autorizar(tarjeta, monto);

        // Regla de Negocio 2: Si el banco rechaza, lanzamos excepción de negocio
        if (!autorizado) {
            throw new RuntimeException("Pago rechazado por el banco");
        }
        System.out.println("Pago realizado con éxito a la tarjeta: " + tarjeta);
    }
}


