package com.marufeb.note.graphics.charts;

import com.marufeb.note.model.Note;
import com.marufeb.note.repository.NoteRepo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents a PieChart based on who references people
 * @author fabiomaruca
 * @since February 2021
 */
public class ReferredByChart extends PieChart {
    public static NoteRepo repo;

    public ReferredByChart() {
        super();
        setTitle("Referred By");
    }

    /**
     * Updates the chart based on each {@link Note}'s content which has a name of: <b>Referred by</b>.
     * Please note that it does not automatically set the new data inside the chart. Use the proper setData() method.
     * @return The resulting {@link ObservableList}
     */
    public static ObservableList<Data> update() {
        final ObservableList<Data> data = FXCollections.observableArrayList();
        final List<Note> referred_by1 = repo.getAll().stream().filter(it -> !it.getContent().isEmpty() && it.getContent("Referred by").isPresent()).collect(Collectors.toList());
        if (referred_by1.stream().findFirst().isPresent()) {
            final Map<String, List<Note>> collect = referred_by1.stream()
                    .collect(Collectors.groupingBy(it -> {
                        final Optional<Note.Content> referred_by = it.getContent("Referred by");
                        if (referred_by.isPresent())
                            return referred_by.get().getValue();
                        else return "unknown";
                    }));
            collect.forEach((k, v)-> data.add(new Data(k, v.size())));
        }
        return data;
    }

}
