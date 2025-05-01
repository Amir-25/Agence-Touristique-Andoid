package com.mohamedamir_araar_najib_tahiri_mohamedamine_mhammedi.tourisme.vue.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.agencetourist.R;
import com.mohamedamir_araar_najib_tahiri_mohamedamine_mhammedi.tourisme.modele.entities.Voyage;
import com.mohamedamir_araar_najib_tahiri_mohamedamine_mhammedi.tourisme.vue.adapters.VoyageAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private TextView welcomeMessage;
    private Button logoutButton, searchButton, filterButton;
    private EditText searchBar;
    private RecyclerView voyageRecyclerView;
    private VoyageAdapter voyageAdapter;
    private List<Voyage> voyageList = new ArrayList<>();

    private static final String URL = "http://10.0.2.2:3000/voyages";

    //pour stocker les filtres
    private String filterDestination = "";
    private String filterBudget = "";
    private String filterType = "";
    private String filterDate = "";
    private String userEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Button btnHistorique = findViewById(R.id.btnHistorique);

        btnHistorique.setOnClickListener(v -> {
            Toast.makeText(this, "Email envoyé : " + userEmail, Toast.LENGTH_SHORT).show(); // TEST
            Intent intent = new Intent(HomeActivity.this, HistoriqueActivity.class);
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });



        welcomeMessage = findViewById(R.id.welcomeMessage);
        logoutButton = findViewById(R.id.logoutButton);
        searchButton = findViewById(R.id.searchButton);
        filterButton = findViewById(R.id.filterButton);
        searchBar = findViewById(R.id.searchBar);
        voyageRecyclerView = findViewById(R.id.voyageRecyclerView);

        voyageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        voyageAdapter = new VoyageAdapter(this, voyageList);
        voyageRecyclerView.setAdapter(voyageAdapter);

        userEmail = getIntent().getStringExtra("USER_EMAIL"); //testtttttttttttt
        if (userEmail != null) {
            String urlClient = "http://10.0.2.2:3000/clients?email=" + userEmail;

            JsonArrayRequest prenomRequest = new JsonArrayRequest(Request.Method.GET, urlClient, null,
                    response -> {
                        try {
                            if (response.length() > 0) {
                                JSONObject user = response.getJSONObject(0);
                                String prenom = user.getString("prenom");
                                welcomeMessage.setText("Bienvenue, " + prenom + "!");
                            } else {
                                welcomeMessage.setText("Bienvenue !");
                            }
                        } catch (Exception e) {
                            welcomeMessage.setText("Bienvenue !");
                        }
                    },
                    error -> welcomeMessage.setText("Bienvenue !")
            );

            Volley.newRequestQueue(this).add(prenomRequest);
        }




        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        searchButton.setOnClickListener(v -> fetchVoyages());

        // page filtre
        filterButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, FilterActivity.class);
            startActivityForResult(intent, 1);
        });

        fetchVoyages(); // charger les voyages au demarrage
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            filterDestination = data.getStringExtra("destination");
            filterBudget = data.getStringExtra("budget");
            filterType = data.getStringExtra("type");
            filterDate = data.getStringExtra("date");

            fetchVoyages();
        }
    }

    private void fetchVoyages() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        voyageList.clear();
                        String query = searchBar.getText().toString().trim().toLowerCase();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            String nom = obj.getString("nom_voyage").toLowerCase();
                            String destination = obj.getString("destination");
                            double prix = obj.getDouble("prix");
                            String imageUrl = obj.getString("image_url");
                            String type = obj.getString("type_de_voyage");

                            // filtre par recherche textuelle
                            if (!TextUtils.isEmpty(query) && !nom.contains(query)) continue;

                            // filtres de la page filtre
                            if (!TextUtils.isEmpty(filterDestination) && !destination.toLowerCase().contains(filterDestination.toLowerCase())) continue;
                            if (!TextUtils.isEmpty(filterType) && !type.toLowerCase().contains(filterType.toLowerCase())) continue;
                            if (!TextUtils.isEmpty(filterBudget)) {
                                double max = Double.parseDouble(filterBudget);
                                if (prix > max) continue;
                            }

                            // filtre de date
                            if (!TextUtils.isEmpty(filterDate)) {
                                if (!obj.has("trips")) {
                                    Toast.makeText(this, "Voyage sans données de trips !", Toast.LENGTH_SHORT).show();
                                    continue;
                                }
                                JSONArray trips = obj.getJSONArray("trips");
                                boolean dateMatch = false;
                                for (int j = 0; j < trips.length(); j++) {
                                    JSONObject trip = trips.getJSONObject(j);
                                    if (trip.getString("date").equals(filterDate)) {
                                        dateMatch = true;
                                        break;
                                    }
                                }
                                if (!dateMatch) continue;
                            }

                            int id = obj.getInt("id");
                            String desc = obj.getString("description");
                            int duree_jours = obj.getInt("duree_jours");
                            String type_de_voyage = obj.getString("type_de_voyage");
                            String activites = obj.getString("activites_incluses");

                            Voyage voyage = new Voyage(id, nom, desc, (int) prix, destination, imageUrl, duree_jours, type_de_voyage, activites);

                            JSONArray tripsArray = obj.getJSONArray("trips");
                            List<DetailVoyageActivity.TripDate> listeDeTrips = new ArrayList<>();

                            for (int j = 0; j < tripsArray.length(); j++) {
                                JSONObject t = tripsArray.getJSONObject(j);
                                String date = t.getString("date");
                                int nbPlaces = t.getInt("nb_places_disponibles");
                                listeDeTrips.add(new DetailVoyageActivity.TripDate(date, nbPlaces));
                            }

                            voyage.setTrips(listeDeTrips);

                            voyageList.add(voyage);

                        }

                        voyageAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Erreur de connexion au serveur", Toast.LENGTH_SHORT).show());

        queue.add(request);
    }
}
