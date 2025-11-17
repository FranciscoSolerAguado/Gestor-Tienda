module org.fran.gestortienda {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.fran.gestortienda to javafx.fxml;
    exports org.fran.gestortienda;
}