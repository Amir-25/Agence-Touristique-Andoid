package com.mohamedamir_araar_najib_tahiri_mohamedamine_mhammedi.tourisme.vue.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;


import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agencetourist.R;
import com.mohamedamir_araar_najib_tahiri_mohamedamine_mhammedi.tourisme.modele.entities.Reservation;
import com.mohamedamir_araar_najib_tahiri_mohamedamine_mhammedi.tourisme.modele.dao.ReservationDatabaseHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


public class HistoriqueAdapter extends RecyclerView.Adapter<HistoriqueAdapter.ViewHolder> {
    private List<Reservation> reservations;

    public HistoriqueAdapter(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Reservation r = reservations.get(position);


        String urlVoyage = "http://10.0.2.2:3000/voyages/" + r.getVoyageId();
        com.android.volley.toolbox.JsonObjectRequest voyageRequest =
                new com.android.volley.toolbox.JsonObjectRequest(
                        com.android.volley.Request.Method.GET, urlVoyage, null,
                        response -> {
                            String imageUrl = response.optString("image_url");
                            String destination = response.optString("destination", "Inconnue");

                            holder.destination.setText("Destination : " + destination);
                            Glide.with(holder.imgDestination.getContext())
                                    .load(imageUrl)
                                    .into(holder.imgDestination);
                        },
                        error -> {
                            holder.destination.setText("Destination : Erreur");
                        }
                );
        com.android.volley.toolbox.Volley.newRequestQueue(holder.itemView.getContext()).add(voyageRequest);

        //holder.destination.setText("Destination : " + r.getVoyageId());
        holder.date.setText("Date : " + r.getDate());
        holder.total.setText("Total payé : " + r.getTotal() + "$");
        holder.statut.setText("Statut : " + r.getStatut());

        if (r.getStatut().equals("Annulée")) {
            holder.btnAnnuler.setEnabled(false);
            holder.btnAnnuler.setText("Annulée");
        } else {
            holder.btnAnnuler.setEnabled(true);
            holder.btnAnnuler.setText("Annuler");
            holder.btnAnnuler.setOnClickListener(v -> {
                r.setStatut("Annulée");
                notifyItemChanged(position);

                // metre à jour SQLite
                ReservationDatabaseHelper dbHelper = new ReservationDatabaseHelper(holder.itemView.getContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("UPDATE reservations SET statut = 'Annulée' WHERE voyage_id = ? AND date = ?",
                        new String[]{r.getVoyageId(), r.getDate()});

                // mettre à jour le nombre de places sur le serveur JSON
                updatePlacesOnServer(r.getVoyageId(), r.getDate(), r.getNbPlaces(), holder);
            });
        }
    }


    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgDestination;
        TextView destination, date, total, statut;
        Button btnAnnuler;

        public ViewHolder(View itemView) {
            super(itemView);
            imgDestination = itemView.findViewById(R.id.imgDestination);
            destination = itemView.findViewById(R.id.txtDestination);
            date = itemView.findViewById(R.id.txtDate);
            total = itemView.findViewById(R.id.txtTotal);
            statut = itemView.findViewById(R.id.txtStatut);
            btnAnnuler = itemView.findViewById(R.id.btnAnnuler);
        }
    }


    private void updatePlacesOnServer(String voyageId, String date, int nbPlacesARestaurer, ViewHolder holder) {
        String url = "http://10.0.2.2:3000/voyages/" + voyageId;

        com.android.volley.toolbox.JsonObjectRequest getRequest = new com.android.volley.toolbox.JsonObjectRequest(
                com.android.volley.Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray trips = response.getJSONArray("trips");
                        for (int i = 0; i < trips.length(); i++) {
                            JSONObject trip = trips.getJSONObject(i);
                            if (trip.getString("date").equals(date)) {
                                int current = trip.getInt("nb_places_disponibles");
                                trip.put("nb_places_disponibles", current + nbPlacesARestaurer);
                                break;
                            }
                        }
                        response.put("trips", trips);

                        com.android.volley.toolbox.JsonObjectRequest putRequest =
                                new com.android.volley.toolbox.JsonObjectRequest(
                                        com.android.volley.Request.Method.PUT, url, response,
                                        r -> Toast.makeText(holder.itemView.getContext(), "Places restaurees ✅", Toast.LENGTH_SHORT).show(),
                                        e -> Toast.makeText(holder.itemView.getContext(), "Erreur maj places", Toast.LENGTH_SHORT).show()
                                );
                        com.android.volley.toolbox.Volley.newRequestQueue(holder.itemView.getContext()).add(putRequest);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(holder.itemView.getContext(), "Erreur serveur", Toast.LENGTH_SHORT).show()
        );
        com.android.volley.toolbox.Volley.newRequestQueue(holder.itemView.getContext()).add(getRequest);
    }


}

