package com.sisrh.dao;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;

import com.sisrh.model.Usuario;

/**
 * Clase DAO para operaciones de persistencia de Usuarios
 */
public class UsuarioDAO {
    
    private EntityManager em;
    
    /**
     * Constructor que recibe un EntityManager
     * @param em EntityManager para operaciones de persistencia
     */
    public UsuarioDAO(EntityManager em) {
        this.em = em;
    }
    
    /**
     * Guarda un usuario en la base de datos
     * @param usuario el usuario a guardar
     * @return el usuario guardado con su ID asignado
     */
    public Usuario guardar(Usuario usuario) {
        try {
            em.getTransaction().begin();
            if (usuario.getId() == null) {
                em.persist(usuario);
            } else {
                usuario = em.merge(usuario);
            }
            em.getTransaction().commit();
            return usuario;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }
    
    /**
     * Busca un usuario por su ID
     * @param id el ID del usuario
     * @return el usuario encontrado o null si no existe
     */
    public Usuario buscarPorId(Long id) {
        return em.find(Usuario.class, id);
    }
    
    /**
     * Busca un usuario por su nombre de usuario
     * @param username el nombre de usuario
     * @return el usuario encontrado o null si no existe
     */
    public Usuario buscarPorUsername(String username) {
        try {
            return em.createQuery("SELECT u FROM Usuario u WHERE u.username = :username", Usuario.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Obtiene todos los usuarios
     * @return lista de todos los usuarios
     */
    public List<Usuario> listarTodos() {
        return em.createQuery("SELECT u FROM Usuario u ORDER BY u.username", Usuario.class)
                .getResultList();
    }
    
    /**
     * Obtiene todos los usuarios activos
     * @return lista de usuarios activos
     */
    public List<Usuario> listarActivos() {
        return em.createQuery("SELECT u FROM Usuario u WHERE u.activo = true ORDER BY u.username", Usuario.class)
                .getResultList();
    }
    
    /**
     * Autentica un usuario por su nombre de usuario y contraseña
     * @param username el nombre de usuario
     * @param password la contraseña
     * @return el usuario autenticado o null si las credenciales son inválidas
     */
    public Usuario autenticar(String username, String password) {
        Usuario usuario = buscarPorUsername(username);
        
        if (usuario != null && usuario.isActivo()) {
            // En un sistema real, la contraseña debería estar encriptada
            // y se debería verificar con un método seguro
            if (usuario.getPassword().equals(password)) {
                usuario.setUltimoAcceso(LocalDateTime.now());
                guardar(usuario);
                return usuario;
            }
        }
        
        return null;
    }
    
    /**
     * Desactiva un usuario
     * @param usuario el usuario a desactivar
     * @return el usuario actualizado
     */
    public Usuario desactivar(Usuario usuario) {
        usuario.setActivo(false);
        return guardar(usuario);
    }
    
    /**
     * Cambia la contraseña de un usuario
     * @param usuario el usuario
     * @param nuevaPassword la nueva contraseña
     * @return el usuario actualizado
     */
    public Usuario cambiarPassword(Usuario usuario, String nuevaPassword) {
        // En un sistema real, la contraseña debería encriptarse antes de guardarla
        usuario.setPassword(nuevaPassword);
        return guardar(usuario);
    }
    
    /**
     * Crea un usuario administrador por defecto si no existe ningún usuario
     */
    public void crearUsuarioAdminPorDefecto() {
        if (listarTodos().isEmpty()) {
            Usuario admin = new Usuario("admin", "admin123", "Administrador", Usuario.Rol.ADMIN);
            admin.setEmail("admin@empresa.com");
            guardar(admin);
            System.out.println("Usuario administrador creado por defecto: admin / admin123");
        }
    }
}