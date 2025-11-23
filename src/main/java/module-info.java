module org.fran.gestortienda {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires java.logging;
    requires javafx.base;
    requires org.fran.gestortienda;


    opens org.fran.gestortienda to javafx.fxml;
    exports org.fran.gestortienda;
}