module org.fran.gestortienda {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.fran.gestortienda to javafx.fxml;
    exports org.fran.gestortienda;
}