package org.fran.gestortienda;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.fran.gestortienda.DatabaseManager.DatabaseManager;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        // Asegura que la estructura de la BBDD existe
        DatabaseManager.crearTablas();

        // Inserta los datos iniciales solo si es necesario
        DatabaseManager.seedData();


        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/org/fran/gestortienda/ui/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Gestor-Tienda");
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED); //necesario para poder usar el boton de maximizar o ventana
        stage.show();
    }
}
