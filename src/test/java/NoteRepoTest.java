import com.marufeb.note.model.Form;
import com.marufeb.note.model.FormFactory;
import com.marufeb.note.model.Note;
import com.marufeb.note.model.exceptions.InvalidContentException;
import com.marufeb.note.repository.FormRepo;
import com.marufeb.note.repository.NoteRepo;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

public class NoteRepoTest {

    private static NoteRepo repo;
    private static FormRepo formRepo;
    private static long test1;

    @BeforeClass
    public static void setup() {
        repo = new NoteRepo();
        formRepo = new FormRepo();
        final Note note = new Note(new Date(System.currentTimeMillis()), "Test");
        repo.add(note);
        test1 = note.getId();
    }

    @Test
    public void creation() {
        assertTrue(repo.get(test1).isPresent());
        final Note note = repo.get(test1).get();
        final Note note1 = new Note(new Date(System.currentTimeMillis()), new GregorianCalendar(2021, Calendar.JANUARY, 1).getTime(), "Test_Mod");
        repo.add(note1);
        assertTrue(repo.getAll().stream().anyMatch(it->it.getTitle().equals("Test")));

        try {
            note.addContent("name", "Jack");
            note.addContent("surname", "Banana");
        } catch (InvalidContentException ignore) { }

        assertTrue(note.getContent("name").isPresent());
        assertEquals(note.getContent("name").get().getValue(), "Jack");

        assertTrue(note.getContent("surname").isPresent());
        assertEquals(note.getContent("surname").get().getValue(), "Banana");

        repo.update(note);

        final FormFactory formFactory = new FormFactory();
        final Form form = formFactory
                .addField("field_1", Form.FieldType.TEXT_FIELD, 0)
                .addField("field_2", Form.FieldType.TEXT_FIELD, 1)
                .addField("field_3", Form.FieldType.TEXT_FIELD, 2)
                .configure()
                .build();
        formRepo.add(form);

    }

    @Test
    public void update() {
        test1 = repo.getAll().get(0).getId();
        final Optional<Note> note = repo.get(test1);
        assertTrue(note.isPresent());
        final Optional<Note.Content> name = note.get().getContent("name");
        assertTrue(name.isPresent());
        assertEquals(name.get().getValue(), "Jack");

        note.get().setTitle("Test_M");

        repo.update(note.get());

        assertTrue(repo.get(test1).isPresent());
        final Note note1 = repo.get(test1).get();
        assertEquals(note1.getTitle(), "Test_M");
    }
}
