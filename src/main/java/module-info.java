module NoteApp {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.classmate;
    requires org.hibernate.orm.core;
    requires net.bytebuddy;
    requires java.persistence;
    requires java.sql;
    requires java.xml.bind;
    requires poi.ooxml;
    requires poi.ooxml.schemas;
    requires poi;

    opens com.marufeb.note to javafx.controls;
    opens com.marufeb.note.graphics to javafx.controls, javafx.fxml;
    opens com.marufeb.note.model;

    exports com.marufeb.note;
    exports com.marufeb.note.graphics;
    exports com.marufeb.note.graphics.form;
    exports com.marufeb.note.model;
    exports com.marufeb.note.model.exceptions;
    exports com.marufeb.note.repository;
}