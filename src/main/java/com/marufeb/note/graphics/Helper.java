package com.marufeb.note.graphics;

import com.marufeb.note.model.Form;
import com.marufeb.note.model.Note;
import com.marufeb.note.model.exceptions.ExceptionsHandler;
import com.marufeb.note.repository.ResourceLoader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.impl.CTRowImpl;

/**
 * A basic helper class for my GUI
 * @author fabiomaruca
 * @since January 2021
 */
public class Helper {

    private final static ResourceLoader loader = new ResourceLoader();

    /**
     * Loads a new Scene based on a given fxml file.
     * @param fileName The fxml filename ("/fxml/$FILENAME$.fxml) without extension
     * @param mid The operation to execute on the {@link Parent}
     * @return The requested {@link Scene}. NULL if no file has been found.
     */
    public static Scene loadFXML(String fileName, Consumer<Parent> mid) {
        final FXMLLoader loader = new FXMLLoader(Helper.class.getResource("/fxml/"+fileName+".fxml"));
        try {
            final Parent load = loader.load();
            mid.accept(load);
            return new Scene(load);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates a new directory chooser in order to select a valid data folder
     * @param window The window owner
     * @return The desired list of notes
     */
    public static List<Note> open(Window window) {
        final DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Notes opener");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        final File file = chooser.showDialog(window);
        return loader.loadNotes(file);
    }

    static Optional<File> selection(Window window, FileChooser.ExtensionFilter filter, String initialFileName) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(System.getProperty("user.home"));
        fileChooser.setTitle("Select where to store the file");
        fileChooser.setSelectedExtensionFilter(filter);
        fileChooser.setInitialFileName(initialFileName);
        return Optional.ofNullable(fileChooser.showSaveDialog(window));
    }

    /**
     * Creates and shows a new window which will allow the user to create or choose a {@link Form} to use
     * @return The chosen {@link Form}
     * @throws IOException If the FXML file referred to the GUI is not found. You could ignore this scenario
     */
    public static Form selectForm() throws IOException {
        final AtomicReference<Form> reference = new AtomicReference<>();
        final FormPopup popup = new FormPopup(reference::set);

        final FXMLLoader loader = new FXMLLoader(Form.class.getResource("/fxml/form.fxml"));
        loader.setController(popup);
        final Parent load = loader.load();
        final Stage stage = new Stage();
        stage.setScene(new Scene(load));
        stage.setTitle("Creator");

        stage.showAndWait();
        return reference.get();
    }

    public static void exportToExcel(Note note, File file) {
        if (!file.exists() || file.delete()) {
            try {
                if (!file.createNewFile()) return;
                final Workbook sheets = new XSSFWorkbook();
                final Sheet sheet = sheets.createSheet();

                final Row title = sheet.createRow(0);
                title.createCell(0).setCellValue(note.getTitle());

                sort(note);

                for (int i = 0; i < note.getContent().size(); i++) {
                    final Note.Content content = note.getContent().get(i);
                    final Row row = sheet.createRow(i+3);
                    final Cell property = row.createCell(0);
                    property.setCellValue(content.getName());
                    final Cell value = row.createCell(1);
                    value.setCellValue(content.getValue());
                }

                try (final FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    sheets.write(fileOutputStream);
                } finally {
                    sheets.close();
                }
            } catch (Exception e) {
                ExceptionsHandler.register(e);
            }
        }
    }

    private static void sort(Note note) {
        note.getContent().sort(Comparator.comparing(content-> {
            final Optional<Form.Field> first = note.getRelatedForm().getFields().stream().filter(it -> it.getName().equals(content.getName())).findFirst();
            return first.map(Form.Field::getIndex).orElse(Integer.MAX_VALUE);
        }));
    }

    public static void exportToWord(Note note, File file) {
        if (!file.exists() || file.delete()) {
            try {
                final XWPFDocument document = new XWPFDocument();

                //-----| HEADER

                final XWPFParagraph header = document.createParagraph();
                header.setAlignment(ParagraphAlignment.CENTER);
                final XWPFRun header_run = header.createRun();
                header_run.setText(note.getTitle());
                header_run.setFontSize(20);
                header_run.addBreak();

                //-----| TABLE

                final XWPFTable table = document.createTable();
                table.getRow(0).getCell(0).setText("Properties");
                table.setCellMargins(200, 200, 200, 200);

                sort(note);

                note.getContent().forEach(content -> {
                    final Optional<Form.Field> any = note.getRelatedForm().getFields().stream().filter(it -> it.getName().equals(content.getName())).findAny();
                    any.ifPresent(field -> {
                        final XWPFTableRow row_k = table.createRow();

                        if (!content.getName().isBlank()) {
                            row_k.getCell(0).setText(content.getName());

                            row_k.addNewTableCell().setText(content.getValue().isBlank() ? "" : content.getValue());
                        }
                    });
                });

                //-----| SAVE

                try (final FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    document.write(fileOutputStream);
                } finally {
                    document.close();
                }

            } catch (Exception e) {
                ExceptionsHandler.register(e);
            }
        }
    }

}
