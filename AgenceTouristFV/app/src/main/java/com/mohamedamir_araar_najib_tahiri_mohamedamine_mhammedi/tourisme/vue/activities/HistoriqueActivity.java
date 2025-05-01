package com.mohamedamir_araar_najib_tahiri_mohamedamine_mhammedi.tourisme.vue.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agencetourist.R;
import com.mohamedamir_araar_najib_tahiri_mohamedamine_mhammedi.tourisme.vue.adapters.HistoriqueAdapter;
import com.mohamedamir_araar_najib_tahiri_mohamedamine_mhammedi.tourisme.modele.entities.Reservation;
import com.mohamedamir_araar_najib_tahiri_mohamedamine_mhammedi.tourisme.modele.dao.ReservationDatabaseHelper;

import java.util.ArrayList;
import java.util.List;


public class HistoriqueActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HistoriqueAdapter adapter;
    private List<Reservation> listeReservations;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique);

        Button btnRetour = findViewById(R.id.btnRetour);
        btnRetour.setOnClickListener(v -> {
            Intent intent = new Intent(HistoriqueActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
            finish();
        });


        userEmail = getIntent().getStringExtra("USER_EMAIL");
        Toast.makeText(this, "Email reçu : " + userEmail, Toast.LENGTH_SHORT).show();
        if (userEmail == null) {
            Toast.makeText(this, "Email utilisateur manquant !", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        recyclerView = findViewById(R.id.recyclerHistorique);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listeReservations = new ArrayList<>();

        ReservationDatabaseHelper dbHelper = new ReservationDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery("SELECT * FROM reservations WHERE email_client = ?", new String[]{userEmail});

            while (cursor.moveToNext()) {
                String voyageId = cursor.getString(cursor.getColumnIndexOrThrow("voyage_id"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                int nbPlaces = cursor.getInt(cursor.getColumnIndexOrThrow("nb_places"));
                int total = cursor.getInt(cursor.getColumnIndexOrThrow("total"));

                String statut = cursor.getString(cursor.getColumnIndexOrThrow("statut"));
                listeReservations.add(new Reservation(voyageId, date, nbPlaces, total, statut));
            }

            cursor.close();
        } catch (Exception e) {
            Toast.makeText(this, "erreur chargement historique : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }


        adapter = new HistoriqueAdapter(listeReservations);
        recyclerView.setAdapter(adapter);
    }
}

