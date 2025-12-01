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

    private static Stage primaryStage; // Referencia estática para poder cambiar la escena desde cualquier sitio
    private Stage loadingStage; // Ventana de carga (Splash Screen)

    /**
     * Punto de entrada principal de la aplicación JavaFX.
     * Configura la ventana, muestra la pantalla de carga e inicializa la base de datos en segundo plano.
     * @param stage El escenario principal proporcionado por JavaFX.
     */
    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        primaryStage.initStyle(StageStyle.UNDECORATED);
        showLoadingScreen();

        // Creamos una tarea en segundo plano para no congelar la interfaz visual
        Task<Void> dbTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Creamos tablas y metemos datos de prueba
                DatabaseManager.crearTablas();
                DatabaseManager.seedData();
                return null;
            }
        };

        // Qué hacer cuando la base de datos termine de cargar correctamente
        dbTask.setOnSucceeded(e -> {
            loadingStage.close(); // cerramos pantalla de carga
            loadScene("/org/fran/gestortienda/ui/main.fxml", "Gestor-Tienda"); // abrimos la pantalla principal
        });

        // Qué hacer si la base de datos falla al arrancar
        dbTask.setOnFailed(e -> {
            loadingStage.close();
            Throwable ex = dbTask.getException();
            ex.printStackTrace();
            showErrorDialog("Error de Base de Datos", "No se pudo conectar a la base de datos.", "Asegúrese de que el servidor de base de datos está en ejecución.");
            Platform.exit(); // Cerramos la aplicación
        });

        // Arrancamos el hilo de la tarea
        new Thread(dbTask).start();
    }

    /**
     * Carga y muestra una ventana temporal (Splash Screen) sin bordes.
     */
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
     * Método estático y universal para cambiar la vista principal.
     * Gestiona la carga del FXML y ajusta la ventana al tamaño de la pantalla.
     *
     * @param fxmlPath La ruta absoluta (desde src/main/resources) al fichero FXML.
     * @param title    El nuevo título para la ventana.
     */
    public static void loadScene(String fxmlPath, String title) {
        try {
            URL fxmlUrl = MainApp.class.getResource(fxmlPath);
            // Validación básica para evitar NullPointer si la ruta está mal
            if (fxmlUrl == null) {
                throw new IOException("No se encontró el archivo FXML: " + fxmlPath);
            }

            Parent root = FXMLLoader.load(Objects.requireNonNull(fxmlUrl));
            Scene scene = new Scene(root);

            primaryStage.setTitle(title);
            primaryStage.setScene(scene);

            // Ajuste manual para ocupar toda la pantalla visible (sin tapar la barra de tareas)
            // Esto es necesario porque usamos StageStyle.UNDECORATED y setMaximized(true) a veces falla en este modo.
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
     * Utilidad para mostrar alertas de error de forma rápida.
     * @param title Título de la ventana de alerta.
     * @param header Cabecera del mensaje.
     * @param content Contenido detallado del error.
     */
    private static void showErrorDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Lanza la aplicación JavaFX.
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
