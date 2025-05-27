package com.sisrh.service;

import java.util.List;

import javax.persistence.EntityManager;

import com.sisrh.DatabaseManager;
import com.sisrh.dao.ConfiguracionDAO;
import com.sisrh.dao.UsuarioDAO;
import com.sisrh.model.HistorialSesion;
import com.sisrh.model.Usuario;

/**
 * Servicio para la gestión de usuarios y autenticación
 */
public class UsuarioService {
    
    private UsuarioDAO usuarioDAO;
    private ConfiguracionDAO configuracionDAO;
    
    /**
     * Constructor por defecto
     */
    public UsuarioService() {
        EntityManager em = DatabaseManager.getInstance().getEntityManager();
        this.usuarioDAO = new UsuarioDAO(em);
        this.configuracionDAO = new ConfiguracionDAO(em);
    }
    
    /**
     * Constructor que recibe DAOs específicos (útil para pruebas)
     * @param usuarioDAO el DAO de usuarios a utilizar
     * @param configuracionDAO el DAO de configuraciones a utilizar
     */
    public UsuarioService(UsuarioDAO usuarioDAO, ConfiguracionDAO configuracionDAO) {
        this.usuarioDAO = usuarioDAO;
        this.configuracionDAO = configuracionDAO;
    }
    
    /**
     * Inicializa el sistema con un usuario administrador por defecto
     * y las configuraciones básicas
     */
    public void inicializarSistema() {
        // Crear usuario administrador por defecto si no existe ninguno
        usuarioDAO.crearUsuarioAdminPorDefecto();
        
        // Inicializar configuraciones por defecto
        Usuario admin = usuarioDAO.buscarPorUsername("admin");
        if (admin != null) {
            configuracionDAO.inicializarConfiguracionesPorDefecto(admin);
        }
    }
    
    /**
     * Registra un nuevo usuario en el sistema
     * @param usuario el usuario a registrar
     * @return el usuario registrado
     */
    public Usuario registrarUsuario(Usuario usuario) {
        // Verificar que no exista otro usuario con el mismo username
        Usuario existente = usuarioDAO.buscarPorUsername(usuario.getUsername());
        if (existente != null) {
            throw new IllegalArgumentException("Ya existe un usuario con el nombre " + usuario.getUsername());
        }
        
        // En un sistema real, aquí se encriptaría la contraseña
        
        return usuarioDAO.guardar(usuario);
    }
    
    /**
     * Actualiza los datos de un usuario
     * @param usuario el usuario con los datos actualizados
     * @return el usuario actualizado
     */
    public Usuario actualizarUsuario(Usuario usuario) {
        // Verificar que el usuario exista
        Usuario existente = usuarioDAO.buscarPorId(usuario.getId());
        if (existente == null) {
            throw new IllegalArgumentException("El usuario no existe");
        }
        
        // Verificar que no se duplique el username con otro usuario
        Usuario otroPorUsername = usuarioDAO.buscarPorUsername(usuario.getUsername());
        if (otroPorUsername != null && !otroPorUsername.getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Ya existe otro usuario con el nombre " + usuario.getUsername());
        }
        
        // Mantener la contraseña original si no se proporciona una nueva
        if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
            usuario.setPassword(existente.getPassword());
        } else {
            // En un sistema real, aquí se encriptaría la nueva contraseña
        }
        
        return usuarioDAO.guardar(usuario);
    }
    
    /**
     * Cambia la contraseña de un usuario
     * @param usuario el usuario
     * @param nuevaPassword la nueva contraseña
     * @return el usuario actualizado
     */
    public Usuario cambiarPassword(Usuario usuario, String nuevaPassword) {
        // En un sistema real, aquí se encriptaría la contraseña
        return usuarioDAO.cambiarPassword(usuario, nuevaPassword);
    }
    
    /**
     * Desactiva un usuario
     * @param usuario el usuario a desactivar
     * @return el usuario actualizado
     */
    public Usuario desactivarUsuario(Usuario usuario) {
        return usuarioDAO.desactivar(usuario);
    }
    
    /**
     * Autentica un usuario por su nombre de usuario y contraseña
     * @param username el nombre de usuario
     * @param password la contraseña
     * @param ipAcceso la dirección IP desde donde se accede
     * @return el usuario autenticado o null si las credenciales son inválidas
     */
    public Usuario autenticar(String username, String password, String ipAcceso) {
        Usuario usuario = usuarioDAO.autenticar(username, password);
        
        if (usuario != null) {
            // Registrar el acceso en el historial
            HistorialSesion sesion = usuario.registrarAcceso(ipAcceso);
            usuarioDAO.guardar(usuario);
        }
        
        return usuario;
    }
    
    /**
     * Registra el cierre de sesión de un usuario
     * @param usuario el usuario
     * @param sesion la sesión a cerrar
     * @return el usuario actualizado
     */
    public Usuario cerrarSesion(Usuario usuario, HistorialSesion sesion) {
        sesion.finalizarSesion();
        return usuarioDAO.guardar(usuario);
    }
    
    /**
     * Busca un usuario por su ID
     * @param id el ID del usuario
     * @return el usuario encontrado o null si no existe
     */
    public Usuario buscarPorId(Long id) {
        return usuarioDAO.buscarPorId(id);
    }
    
    /**
     * Busca un usuario por su nombre de usuario
     * @param username el nombre de usuario
     * @return el usuario encontrado o null si no existe
     */
    public Usuario buscarPorUsername(String username) {
        return usuarioDAO.buscarPorUsername(username);
    }
    
    /**
     * Obtiene todos los usuarios
     * @return lista de todos los usuarios
     */
    public List<Usuario> listarTodos() {
        return usuarioDAO.listarTodos();
    }
    
    /**
     * Obtiene todos los usuarios activos
     * @return lista de usuarios activos
     */
    public List<Usuario> listarActivos() {
        return usuarioDAO.listarActivos();
    }
}