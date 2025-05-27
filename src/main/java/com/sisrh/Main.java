package com.sisrh;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal que inicia la aplicación SISRH
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sisrh/view/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/sisrh/css/application.css").toExternalForm());
            
            primaryStage.setTitle("SISRH - Sistema Integral de Salarios y Recursos Humanos");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método principal que inicia la aplicación
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        // Inicializar la base de datos si es necesario
        initDatabase();
        
        // Lanzar la aplicación JavaFX
        launch(args);
    }
    
    /**
     * Inicializa la conexión con la base de datos ObjectDB
     */
    private static void initDatabase() {
        // Aquí se inicializará la conexión con ObjectDB
        // y se crearán las tablas si no existen
        DatabaseManager.getInstance().initDatabase();
    }
}