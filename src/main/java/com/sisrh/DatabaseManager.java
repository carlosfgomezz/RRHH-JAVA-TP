package com.sisrh;

import java.io.File;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Clase singleton que gestiona la conexión con la base de datos ObjectDB
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private EntityManagerFactory emf;
    private static final String DB_PATH = "sisrh.odb";
    
    /**
     * Constructor privado para implementar el patrón Singleton
     */
    private DatabaseManager() {
        // Inicialización del EntityManagerFactory
    }
    
    /**
     * Obtiene la instancia única de DatabaseManager
     * @return instancia de DatabaseManager
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Inicializa la base de datos
     */
    public void initDatabase() {
        try {
            // Verificar si el archivo de base de datos existe
            File dbFile = new File(DB_PATH);
            if (!dbFile.exists()) {
                System.out.println("Creando nueva base de datos: " + DB_PATH);
            } else {
                System.out.println("Conectando a base de datos existente: " + DB_PATH);
            }
            
            // Crear EntityManagerFactory
            emf = Persistence.createEntityManagerFactory(DB_PATH);
            
            // Verificar la conexión creando un EntityManager
            EntityManager em = emf.createEntityManager();
            em.close();
            
            System.out.println("Conexión a la base de datos establecida correctamente.");
        } catch (Exception e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Obtiene un nuevo EntityManager para operaciones de base de datos
     * @return EntityManager
     */
    public EntityManager getEntityManager() {
        if (emf == null) {
            initDatabase();
        }
        return emf.createEntityManager();
    }
    
    /**
     * Cierra la conexión con la base de datos
     */
    public void closeConnection() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            System.out.println("Conexión a la base de datos cerrada.");
        }
    }
}