package com.mohamedamir_araar_najib_tahiri_mohamedamine_mhammedi.tourisme.modele.entities;

import com.mohamedamir_araar_najib_tahiri_mohamedamine_mhammedi.tourisme.vue.activities.DetailVoyageActivity;
import java.util.List;

public class Voyage {
    private int id;
    private String nom_voyage, description, destination, image_url, type_de_voyage, activites_incluses;
    private int prix, duree_jours;

    private List<DetailVoyageActivity.TripDate> trips;

    public Voyage(int id, String nom_voyage, String description, int prix, String destination,
                  String image_url, int duree_jours, String type_de_voyage, String activites_incluses,
                  List<DetailVoyageActivity.TripDate> trips) {
        this.id = id;
        this.nom_voyage = nom_voyage;
        this.description = description;
        this.prix = prix;
        this.destination = destination;
        this.image_url = image_url;
        this.duree_jours = duree_jours;
        this.type_de_voyage = type_de_voyage;
        this.activites_incluses = activites_incluses;
        this.trips = trips;
    }
    public Voyage(int id, String nom_voyage, String description, int prix, String destination,
                  String image_url, int duree_jours, String type_de_voyage, String activites_incluses) {
        this.id = id;
        this.nom_voyage = nom_voyage;
        this.description = description;
        this.prix = prix;
        this.destination = destination;
        this.image_url = image_url;
        this.duree_jours = duree_jours;
        this.type_de_voyage = type_de_voyage;
        this.activites_incluses = activites_incluses;
    }


    public Voyage(String nom_voyage, String destination, double prix, String image_url) {
        this.nom_voyage = nom_voyage;
        this.destination = destination;
        this.prix = (int) prix;
        this.image_url = image_url;
    }

    public int getId() {
        return id;
    }

    public String getNom_voyage() {
        return nom_voyage;
    }

    public String getDescription() {
        return description;
    }

    public int getPrix() {
        return prix;
    }

    public String getDestination() {
        return destination;
    }

    public String getImage_url() {
        return image_url;
    }

    public int getDuree_jours() {
        return duree_jours;
    }

    public String getType_de_voyage() {
        return type_de_voyage;
    }

    public String getActivites_incluses() {
        return activites_incluses;
    }

    public List<DetailVoyageActivity.TripDate> getTrips() {
        return trips;
    }

    public void setTrips(List<DetailVoyageActivity.TripDate> trips) {
        this.trips = trips;
    }
}
