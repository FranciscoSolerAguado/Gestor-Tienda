package org.fran.gestortienda;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.fran.gestortienda.DatabaseManager.DatabaseH2Manager;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        DatabaseH2Manager databaseH2Manager = new DatabaseH2Manager();
        databaseH2Manager.crearTablas();

        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/org/fran/gestortienda/ui/inicio.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Chat-OFFline");
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED); //necesario para poder usar el boton de maximizar o ventana
        stage.show();
    }
}
