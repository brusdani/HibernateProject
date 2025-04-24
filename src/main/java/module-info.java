module com.example.databaseapplication {
    // JavaFX modules
    requires javafx.controls;
    requires javafx.fxml;

    // JPA & JDBC modules
    requires java.persistence;
    requires java.sql;

    // Hibernate ORM (module name provided by Hibernate)
    requires org.hibernate.orm.core;

    // Logging (SLF4J + Logback)
    requires org.slf4j;
    requires ch.qos.logback.classic;

    // Open packages for reflective access
    opens com.example.databaseapplication to javafx.fxml;
    opens com.example.databaseapplication.model to org.hibernate.orm.core,java.persistence;

    // Export root package if needed
    exports com.example.databaseapplication;
}