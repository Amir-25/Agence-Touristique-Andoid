package com.mohamedamir_araar_najib_tahiri_mohamedamine_mhammedi.tourisme.modele.entities;

public class Reservation {
    private String voyageId, date, statut;
    private int nbPlaces, total;

    public Reservation(String voyageId, String date, int nbPlaces, int total, String statut) {
        this.voyageId = voyageId;
        this.date = date;
        this.nbPlaces = nbPlaces;
        this.total = total;
        this.statut = statut;
    }

    public String getVoyageId() {
        return voyageId;
    }
    public String getDate() {
        return date;
    }
    public int getNbPlaces() {
        return nbPlaces;
    }
    public int getTotal() {
        return total;
    }
    public String getStatut() {
        return statut;
    }
    public void setStatut(String statut) {
        this.statut = statut;
    }

}

