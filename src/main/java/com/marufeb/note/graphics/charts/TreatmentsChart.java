package com.marufeb.note.graphics.charts;

import com.marufeb.note.model.Note;
import com.marufeb.note.repository.NoteRepo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TreatmentsChart extends PieChart {
    public static NoteRepo repo;

    public TreatmentsChart() {
        super();
        setTitle("Treatments number");
    }

    /**
     * Updates the chart based on each {@link Note}'s number of treatments.
     * Please note that it does not automatically set the new data inside the chart. Use the proper setData() method.
     * @return The resulting {@link ObservableList}
     */
    public static ObservableList<Data> update() {
        final ObservableList<Data> data = FXCollections.observableArrayList();
        final List<Note> referred_by1 = repo.getAll();
        if (referred_by1.stream().findFirst().isPresent()) {
            final Map<Integer, List<Note>> collect = referred_by1.stream()
                    .collect(Collectors.groupingBy(it -> it.getTreatments().size()));
            collect.forEach((k, v)-> {
                data.add(new Data(String.valueOf(k), v.size()));
                v.forEach(value -> System.out.println(k + "\t -> \t" + value));
            });
        }
        return data;
    }
}
