package com.mohamedamir_araar_najib_tahiri_mohamedamine_mhammedi.tourisme.vue.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.app.DatePickerDialog;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.agencetourist.R;

public class FilterActivity extends AppCompatActivity {

    private EditText editDestination, editBudget, editDate;
    private RadioGroup radioGroupType;

    private Button btnApplyFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        editDestination = findViewById(R.id.editDestination);
        editBudget = findViewById(R.id.editBudget);
        editDate = findViewById(R.id.editDate);
        Button btnVoirDates = findViewById(R.id.btnVoirDates);
        btnVoirDates.setOnClickListener(v -> {
            com.android.volley.RequestQueue queue = com.android.volley.toolbox.Volley.newRequestQueue(this);
            String url = "http://10.0.2.2:3000/voyages";

            com.android.volley.toolbox.JsonArrayRequest request = new com.android.volley.toolbox.JsonArrayRequest(
                    com.android.volley.Request.Method.GET, url, null,
                    response -> {
                        try {
                            java.util.Set<String> allDates = new java.util.HashSet<>();
                            for (int i = 0; i < response.length(); i++) {
                                org.json.JSONObject obj = response.getJSONObject(i);
                                if (!obj.has("trips")) continue;

                                org.json.JSONArray trips = obj.getJSONArray("trips");
                                for (int j = 0; j < trips.length(); j++) {
                                    org.json.JSONObject trip = trips.getJSONObject(j);
                                    String date = trip.getString("date");
                                    allDates.add(date);
                                }
                            }

                            if (allDates.isEmpty()) {
                                android.widget.Toast.makeText(this, "Aucune date trouvée", android.widget.Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String[] datesArray = allDates.toArray(new String[0]);
                            java.util.Arrays.sort(datesArray);

                            new android.app.AlertDialog.Builder(this)
                                    .setTitle("Dates disponibles 📅")
                                    .setItems(datesArray, (dialog, which) -> {
                                        editDate.setText(datesArray[which]);
                                    })
                                    .setNegativeButton("Fermer", null)
                                    .show();

                        } catch (Exception e) {
                            android.widget.Toast.makeText(this, "Erreur: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> android.widget.Toast.makeText(this, "Erreur serveur", android.widget.Toast.LENGTH_SHORT).show()
            );

            queue.add(request);
        });
        
        editDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    FilterActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        editDate.setText(formattedDate);
                    },
                    year, month, day
            );

            datePickerDialog.show();
        });

        radioGroupType = findViewById(R.id.radioGroupType);
        btnApplyFilters = findViewById(R.id.btnApplyFilters);
        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> finish());


        btnApplyFilters.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("destination", editDestination.getText().toString());
            intent.putExtra("budget", editBudget.getText().toString());
            int selectedId = radioGroupType.getCheckedRadioButtonId();
            String selectedType = "";
            if (selectedId != -1) {
                RadioButton selectedRadio = findViewById(selectedId);
                selectedType = selectedRadio.getText().toString();
            }
            intent.putExtra("type", selectedType);
            intent.putExtra("date", editDate.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}
