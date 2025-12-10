package Practica2.JUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class RegistroUsuarioServiceTest {

    //Instancio mis dos interfaces
    @Mock
    private EmailService emailService;
    @Mock
    private UsuarioRepository usuarioRepository;

    //Inversion de dependencia
    @InjectMocks
    private RegistroUsuarioService negocio;

    @Test
    void usuarioDuplicado() {
        String email = "juan@gmail.com";
        String contrasena = "123";
        Usuario usuarioExistente = new Usuario(email, contrasena);
        Usuario usuarioNuevo = new Usuario(email, "321");

        when(usuarioRepository.buscarPorEmail(anyString())).thenReturn(usuarioExistente);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
           negocio.registrar(usuarioNuevo);
        });

        assertEquals("El usuario ya existe", exception.getMessage());

        verify(usuarioRepository, never()).guardar(usuarioNuevo);
        verifyNoInteractions(emailService);
    }

    @Test
    void datosInvalidos() {
        String emailVacio = "";
        Usuario usuarioNuevo = new Usuario(emailVacio, "123");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            negocio.registrar(usuarioNuevo);
        });
        assertEquals("El email es obligatorio", exception.getMessage());
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void casoExitoso() {
        String email = "nuevo@gmail.com";
        Usuario usuarioNuevo = new Usuario(email, "123");

        when(usuarioRepository.buscarPorEmail(email)).thenReturn(null);
        negocio.registrar(usuarioNuevo);

        verify(usuarioRepository, times(1)).guardar(usuarioNuevo);
        verify(emailService, times(1)).enviarBienvenida(email);
    }

}