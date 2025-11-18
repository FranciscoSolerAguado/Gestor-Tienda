module org.fran.gestortienda {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires java.logging;
    requires javafx.base;


    opens org.fran.gestortienda to javafx.fxml;
    exports org.fran.gestortienda;
}