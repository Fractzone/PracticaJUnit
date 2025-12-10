package Practica2.JUnit;

// LA CLASE A PROBAR (System Under Test)
public class RegistroUsuarioService {
    private UsuarioRepository repositorio;
    private EmailService emailService;

    public RegistroUsuarioService(UsuarioRepository repo, EmailService email) {
        this.repositorio = repo;
        this.emailService = email;
    }

    public void registrar(Usuario usuario) {

        // Regla 1: El email no puede ser null ni vacío
        if (usuario.email == null || usuario.email.isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }

        // Regla 2: No se puede registrar dos veces el mismo email
        if (repositorio.buscarPorEmail(usuario.email) != null) {
            throw new RuntimeException("El usuario ya existe");
        }

        // Acción: Guardar en BD
        repositorio.guardar(usuario);
        // Acción: Enviar correo
        emailService.enviarBienvenida(usuario.email);
    }
}
