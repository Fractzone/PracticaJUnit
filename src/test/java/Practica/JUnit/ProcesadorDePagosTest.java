package Practica.JUnit;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;


// 1. Habilitamos Mockito en JUnit 6
@ExtendWith(MockitoExtension.class)

class ProcesadorDePagosTest {
    // 2. @Mock: Crea un "doble" de la interfaz.
    // No es la real, es un títere que nosotros controlamos.
    @Mock
    private GateWayPagos gatewayMock;

    // 3. @InjectMocks: Crea la instancia real de ProcesadorDePagos
    // e inyecta automáticamente el gatewayMock dentro de ella.
    @InjectMocks
    private ProcesadorDePagos procesador;

    @Test
    void debeCobrarExitosamente_CuandoBancoAutoriza() {
        // --- ARRANGE (Preparar) ---
        String tarjeta = "1234-5678";
        double monto = 100.0;

        // Enseñamos al Mock a comportarse:
        // "Cuando te llamen con estos datos, responde true (autorizado)"
        when(gatewayMock.autorizar(tarjeta, monto)).thenReturn(true);

        // --- ACT (Actuar) ---
        // Ejecutamos el metodo real. Como el mock devuelve true, no debería fallar.
        assertDoesNotThrow(() -> procesador.cobrar(tarjeta, monto));

        // --- ASSERT (Verificar) ---
        // Verificamos que el procesador REALMENTE llamó al banco (gateway) una vez.
        // Esto evita que alguien borre la línea de llamada al banco por error.
        verify(gatewayMock, times(1)).autorizar(tarjeta, monto);
    }

    @Test
    void debeLanzarExcepcion_CuandoBancoRechaza() {
        // --- ARRANGE ---
        String tarjeta = "9999-9999";
        double monto = 50.0;

        // Simulamos que el banco rechaza la transacción
        when(gatewayMock.autorizar(anyString(), eq(monto))).thenReturn(false);

        // --- ACT & ASSERT ---
        // Esperamos que ocurra una RuntimeException con un mensaje específico
        RuntimeException excepcion =
                assertThrows(RuntimeException.class, () -> {
                    procesador.cobrar(tarjeta, monto);
                });
        assertEquals("Pago rechazado por el banco", excepcion.getMessage());
    }

    @Test
    void debeFallar_CuandoMontoEsInvalido() {
        // --- ACT & ASSERT ---
        // Probamos con -50. No necesitamos configurar el Mock porque
        // la lógica debería fallar ANTES de llamar al gateway.
        assertThrows(IllegalArgumentException.class, () -> {
            procesador.cobrar("1234", -50.0);
        });
        // --- ASSERT EXTRA ---
        // Verificamos que NUNCA se molestó al gateway. Ahorramos recursos.
        verifyNoInteractions(gatewayMock);
    }

    @Test
    void debeBloquearMontoExcesivo(){
        assertThrows(IllegalArgumentException.class, () -> {
            procesador.cobrar("1234-5678", 15000);
        });
        verifyNoInteractions(gatewayMock);
    }
}
