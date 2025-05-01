package com.mohamedamir_araar_najib_tahiri_mohamedamine_mhammedi.tourisme.vue.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.agencetourist.R;
import com.mohamedamir_araar_najib_tahiri_mohamedamine_mhammedi.tourisme.modele.dao.ReservationDatabaseHelper;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;


public class DetailVoyageActivity extends AppCompatActivity {

    private ImageView imageVoyage;
    private TextView nomVoyage, description, destination, prix, duree, type, activites;
    private Spinner spinnerDates;
    private TextView placesRestantes;
    private EditText nbPlacesInput;
    private Button btnReserver;

    private List<TripDate> trips = new ArrayList<>();
    private TripDate dateChoisie;
    private int prixUnitaire = 0;
    private String voyageId = "";
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_voyage);

        userEmail = getIntent().getStringExtra("USER_EMAIL");
        imageVoyage = findViewById(R.id.imageVoyage);
        nomVoyage = findViewById(R.id.nomVoyage);
        description = findViewById(R.id.description);
        destination = findViewById(R.id.destination);
        prix = findViewById(R.id.prix);
        duree = findViewById(R.id.duree);
        type = findViewById(R.id.type);
        activites = findViewById(R.id.activites);
        spinnerDates = findViewById(R.id.spinnerDates);
        placesRestantes = findViewById(R.id.placesRestantes);
        nbPlacesInput = findViewById(R.id.nbPlacesInput);
        btnReserver = findViewById(R.id.btnReserver);

        //boutonnn retour
        Button btnRetour = findViewById(R.id.btnRetour);
        btnRetour.setOnClickListener(v -> finish());


        // recuperer les infos passees depuis HomeActivity
        voyageId = String.valueOf(getIntent().getIntExtra("id", -1));

        String imageUrl = getIntent().getStringExtra("image_url");
        String nom = getIntent().getStringExtra("nom_voyage");
        String desc = getIntent().getStringExtra("description");
        String dest = getIntent().getStringExtra("destination");
        int prixParPersonne = getIntent().getIntExtra("prix", 0);
        int dureeJours = getIntent().getIntExtra("duree_jours", 0);
        String typeVoyage = getIntent().getStringExtra("type_de_voyage");
        String act = getIntent().getStringExtra("activites_incluses");
        prixUnitaire = prixParPersonne;

        Glide.with(this).load(imageUrl).into(imageVoyage);
        nomVoyage.setText(nom);
        description.setText(desc);
        destination.setText(dest);
        prix.setText(prixParPersonne + "$ / personne");
        duree.setText("Durée : " + dureeJours + " jours");
        type.setText("Type : " + typeVoyage);
        activites.setText("Activités : " + act);

        try {
            JSONArray jsonTrips = new JSONArray(getIntent().getStringExtra("trips"));
            for (int i = 0; i < jsonTrips.length(); i++) {
                JSONObject t = jsonTrips.getJSONObject(i);
                String date = t.getString("date");
                int dispo = t.getInt("nb_places_disponibles");
                trips.add(new TripDate(date, dispo));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_white);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            for (TripDate trip : trips) adapter.add(trip.date);
            spinnerDates.setAdapter(adapter);

            spinnerDates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    dateChoisie = trips.get(position);
                    placesRestantes.setText("Places restantes : " + dateChoisie.places);
                }

                @Override public void onNothingSelected(AdapterView<?> parent) { }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Erreur de données: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        btnReserver.setOnClickListener(v -> {
            String nbPlacesText = nbPlacesInput.getText().toString();
            if (nbPlacesText.isEmpty()) {
                Toast.makeText(this, "Entrez un nombre de places", Toast.LENGTH_SHORT).show();
                return;
            }

            int nbPlaces = Integer.parseInt(nbPlacesText);
            if (nbPlaces > dateChoisie.places) {
                Toast.makeText(this, "Pas assez de places disponibles", Toast.LENGTH_SHORT).show();
                return;
            }

            int total = nbPlaces * prixUnitaire;
            Toast.makeText(this, "Réservation confirmée ! Total : " + total + "$", Toast.LENGTH_LONG).show();
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Réservation confirmée ✅")
                    .setMessage("Vous avez réservé " + nbPlaces + " place(s).\nTotal : " + total + "$.\nSouhaitez-vous retourner au menu ou voir votre historique ?")
                    .setPositiveButton("Menu", (dialog, which) -> {
                        Intent intent = new Intent(DetailVoyageActivity.this, HomeActivity.class);
                        intent.putExtra("USER_EMAIL", userEmail);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // ferme les anciennes activités
                        startActivity(intent);
                    })
                    .setNegativeButton("Historique", (dialog, which) -> {
                        Intent intent = new Intent(DetailVoyageActivity.this, HistoriqueActivity.class);
                        intent.putExtra("USER_EMAIL", userEmail);
                        startActivity(intent);
                    })
                    .show();


// enregistrement dans la base SQLite locale
            ReservationDatabaseHelper dbHelper = new ReservationDatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(ReservationDatabaseHelper.COL_VOYAGE_ID, voyageId);
            values.put(ReservationDatabaseHelper.COL_DATE, dateChoisie.date);
            values.put(ReservationDatabaseHelper.COL_NB_PLACES, nbPlaces);
            values.put(ReservationDatabaseHelper.COL_TOTAL, total);
            values.put(ReservationDatabaseHelper.COL_EMAIL, userEmail);



            long newRowId = db.insert(ReservationDatabaseHelper.TABLE_NAME, null, values);

            if (newRowId != -1) {
                Toast.makeText(this, "Réservation enregistrée hors ligne !", Toast.LENGTH_SHORT).show();

                int nouvellesPlaces = dateChoisie.places - nbPlaces;

                try {
                    JSONArray jsonTrips = new JSONArray(getIntent().getStringExtra("trips"));
                    for (int i = 0; i < jsonTrips.length(); i++) {
                        JSONObject trip = jsonTrips.getJSONObject(i);
                        if (trip.getString("date").equals(dateChoisie.date)) {
                            trip.put("nb_places_disponibles", dateChoisie.places - nbPlaces);
                        }
                    }


                    JSONObject updatedVoyage = new JSONObject();
                    updatedVoyage.put("nom_voyage", nom);
                    updatedVoyage.put("description", desc);
                    updatedVoyage.put("destination", dest);
                    updatedVoyage.put("prix", prixParPersonne);
                    updatedVoyage.put("image_url", imageUrl);
                    updatedVoyage.put("duree_jours", dureeJours);
                    updatedVoyage.put("type_de_voyage", typeVoyage);
                    updatedVoyage.put("activites_incluses", act);
                    updatedVoyage.put("trips", jsonTrips); 


                    String url = "http://10.0.2.2:3000/voyages/" + voyageId;

                    com.android.volley.toolbox.JsonObjectRequest patchRequest =
                            new com.android.volley.toolbox.JsonObjectRequest(
                                    com.android.volley.Request.Method.PATCH, url, updatedVoyage,
                                    response -> Toast.makeText(this, "Places mises à jour sur le serveur", Toast.LENGTH_SHORT).show(),
                                    error -> Toast.makeText(this, "Erreur JSON server : " + error.getMessage(), Toast.LENGTH_SHORT).show()
                            );

                    com.android.volley.toolbox.Volley.newRequestQueue(this).add(patchRequest);

                } catch (Exception e) {
                    Toast.makeText(this, "Erreur mise à jour serveur : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
            }


        });
    }

    public static class TripDate {
        public String date;
        public int places;

        public TripDate(String date, int places) {
            this.date = date;
            this.places = places;
        }
    }
}
