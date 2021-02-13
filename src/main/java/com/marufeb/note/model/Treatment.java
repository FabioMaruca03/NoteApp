package com.marufeb.note.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * <pre> Represents a medical treatment.
 * According to the client request the {@link Treatment} class will be static and non dynamically changeable.</pre>
 */
@Entity(name = "Treatment")
@DiscriminatorValue("treatment")
@SuppressWarnings("unused")
public class Treatment extends Form {

    @Column(name = "n")
    private int tNumber;

    private String date;

    private String commentary;

    private String evaluation;

    private String TTT;

    public Treatment() { }

    public Treatment(int tNumber) {
        this.tNumber = tNumber;
    }

    public int getNumber() {
        return tNumber;
    }

    public void setNumber(int tNumber) {
        this.tNumber = tNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public String getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(String evaluation) {
        this.evaluation = evaluation;
    }

    public String getTTT() {
        return TTT;
    }

    public void setTTT(String TTT) {
        this.TTT = TTT;
    }
}
