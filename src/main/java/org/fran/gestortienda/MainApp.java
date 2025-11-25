package org.fran.gestortienda;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.fran.gestortienda.DatabaseManager.DatabaseManager;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class MainApp extends Application {

    private static Stage primaryStage; // Guardamos una referencia al Stage principal
    private Stage loadingStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage; // Guardamos el stage para poder usarlo más tarde
        primaryStage.initStyle(StageStyle.UNDECORATED);
        showLoadingScreen();

        Task<Void> dbTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                DatabaseManager.crearTablas();
                DatabaseManager.seedData();
                return null;
            }
        };

        dbTask.setOnSucceeded(e -> {
            loadingStage.close();
            loadScene("/org/fran/gestortienda/ui/main.fxml", "Gestor-Tienda");
        });

        dbTask.setOnFailed(e -> {
            loadingStage.close();
            Throwable ex = dbTask.getException();
            ex.printStackTrace();
            showErrorDialog("Error de Base de Datos", "No se pudo conectar a la base de datos.", "Asegúrese de que el servidor de base de datos está en ejecución.");
            Platform.exit();
        });

        new Thread(dbTask).start();
    }

    private void showLoadingScreen() {
        try {
            loadingStage = new Stage(StageStyle.UNDECORATED);
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/org/fran/gestortienda/ui/inicio.fxml"));
            Scene loadingScene = new Scene(fxmlLoader.load());
            loadingStage.setScene(loadingScene);
            loadingStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Error Crítico", "No se pudo cargar la pantalla de inicio.", "El fichero FXML de inicio parece estar dañado.");
            Platform.exit();
        }
    }

    /**
     * Método estático para cambiar la escena actual del Stage principal.
     * Puede ser llamado desde cualquier controlador.
     *
     * @param fxmlPath La ruta absoluta (desde src/main/resources) al fichero FXML.
     * @param title    El nuevo título para la ventana.
     */
    public static void loadScene(String fxmlPath, String title) {
        try {
            URL fxmlUrl = MainApp.class.getResource(fxmlPath);
            Parent root = FXMLLoader.load(Objects.requireNonNull(fxmlUrl));
            Scene scene = new Scene(root);

            primaryStage.setTitle(title);
            primaryStage.setScene(scene);

            // 2. AJUSTE MANUAL A LA PANTALLA VISIBLE (SIN USAR setMaximized)
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setX(screenBounds.getMinX());
            primaryStage.setY(screenBounds.getMinY());
            primaryStage.setWidth(screenBounds.getWidth());
            primaryStage.setHeight(screenBounds.getHeight());

            if (!primaryStage.isShowing()) {
                primaryStage.show();
            }

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            showErrorDialog("Error de Navegación", "No se pudo cargar la vista: " + fxmlPath, "El fichero FXML podría estar dañado o no se encuentra.");
        }
    }

    /**
     * Muestra un diálogo de error genérico.
     */
    private static void showErrorDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
