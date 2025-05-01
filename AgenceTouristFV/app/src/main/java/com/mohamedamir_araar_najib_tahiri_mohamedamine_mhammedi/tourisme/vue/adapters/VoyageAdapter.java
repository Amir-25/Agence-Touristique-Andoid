package com.mohamedamir_araar_najib_tahiri_mohamedamine_mhammedi.tourisme.vue.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;


import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.agencetourist.R;
import com.mohamedamir_araar_najib_tahiri_mohamedamine_mhammedi.tourisme.modele.entities.Voyage;
import com.mohamedamir_araar_najib_tahiri_mohamedamine_mhammedi.tourisme.vue.activities.DetailVoyageActivity;
import com.mohamedamir_araar_najib_tahiri_mohamedamine_mhammedi.tourisme.vue.activities.HomeActivity;

import java.util.List;

public class VoyageAdapter extends RecyclerView.Adapter<VoyageAdapter.VoyageViewHolder> {
    private Context context;
    private List<Voyage> voyages;

    public VoyageAdapter(Context context, List<Voyage> voyages) {
        this.context = context;
        this.voyages = voyages;
    }

    @Override
    public VoyageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_voyage, parent, false);
        return new VoyageViewHolder(view);
    }
    @Override
    public void onBindViewHolder(VoyageViewHolder holder, int position) {
        Voyage voyage = voyages.get(position);
        holder.nomVoyage.setText(voyage.getNom_voyage());
        holder.destination.setText(voyage.getDestination());
        holder.prix.setText(voyage.getPrix() + " $");

        Glide.with(context)
                .load(voyage.getImage_url())
                .into(holder.imageVoyage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailVoyageActivity.class);
            intent.putExtra("nom_voyage", voyage.getNom_voyage());
            intent.putExtra("destination", voyage.getDestination());
            intent.putExtra("prix", voyage.getPrix());
            intent.putExtra("image_url", voyage.getImage_url());
            intent.putExtra("description", voyage.getDescription());
            intent.putExtra("duree_jours", voyage.getDuree_jours());
            intent.putExtra("type_de_voyage", voyage.getType_de_voyage());
            intent.putExtra("activites_incluses", voyage.getActivites_incluses());
            intent.putExtra("id", voyage.getId());

            try {
                JSONArray tripArray = new JSONArray();
                if (voyage.getTrips() != null) {
                    for (DetailVoyageActivity.TripDate trip : voyage.getTrips()) {
                        JSONObject obj = new JSONObject();
                        obj.put("date", trip.date);
                        obj.put("nb_places_disponibles", trip.places);
                        tripArray.put(obj);
                    }
                }
                intent.putExtra("trips", tripArray.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            String userEmail = "";
            if (context instanceof HomeActivity) {
                Intent homeIntent = ((HomeActivity) context).getIntent();
                userEmail = homeIntent.getStringExtra("USER_EMAIL");
            }
            intent.putExtra("USER_EMAIL", userEmail);

            context.startActivity(intent);

        });
    }
    @Override
    public int getItemCount() {
        return voyages.size();
    }
    public static class VoyageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageVoyage;
        TextView nomVoyage, destination, prix;

        public VoyageViewHolder(View itemView) {
            super(itemView);
            imageVoyage = itemView.findViewById(R.id.imageVoyage);
            nomVoyage = itemView.findViewById(R.id.nomVoyage);
            destination = itemView.findViewById(R.id.destination);
            prix = itemView.findViewById(R.id.prix);
        }
    }
}


