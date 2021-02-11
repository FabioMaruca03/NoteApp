package com.marufeb.note.model.exceptions;

import com.marufeb.note.model.Form;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "Treatment")
@Table(name = "treatments")
public class Treatment extends Form {

    @Column(name = "n")
    private int tNumber;

    public Treatment(int tNumber) {
        this.tNumber = tNumber;
    }

    public int getNumber() {
        return tNumber;
    }

    public void setNumber(int tNumber) {
        this.tNumber = tNumber;
    }
}
