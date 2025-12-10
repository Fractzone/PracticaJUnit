package Practica2.JUnit;

// Dependencia 1: Base de Datos
public interface UsuarioRepository {
    Usuario buscarPorEmail(String email);
    void guardar(Usuario usuario);
}

