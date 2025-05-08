package com.example.android_sante;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.android_sante.databinding.FragmentWeightBinding;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.*;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import static android.content.Intent.getIntent;

public class WeightFragment extends Fragment {

    private FragmentWeightBinding binding;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    double currentWeight;
    double reachableWeight;

    private ArrayList<BarEntry> entries = new ArrayList<>();
    private int entryCount = 0;

    private String id = "";
    private DataBody dataBody;

    public WeightFragment() {
        // Required empty public constructor
    }

    public static WeightFragment newInstance(String param1, String param2) {
        WeightFragment fragment = new WeightFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getActivity().getIntent();

        this.id = intent.getStringExtra("ID");
        if (!id.isBlank())
            this.dataBody = JsonUtils.getDataBody(getContext(), Integer.parseInt(id));

        this.initVariables(dataBody);
    }

    private void initVariables(DataBody dataBody) {
        if (dataBody != null) {
            if (dataBody.getWeights() != null) {
                this.currentWeight = dataBody.getLastWeight();
                for (float weight: dataBody.getWeights().values()) {
                    this.updateGraphWeight(weight);
                }
            }
            if (dataBody.getWeightGoal() != 0f ) {
                this.reachableWeight = dataBody.getWeightGoal();
                this.updateProgressWeight();
                this.displayObjectiveWeight();            
            }
            
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWeightBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateProgressWeight();
        setupWeightGraph();
        visibilityObjectiveWeight();
        addNewObjectiveWeight();
        addCurrentWeight();

        // Sélection par défaut
        selectedButtonGraph(binding.btnWeek);

        // Listeners
        binding.btnWeek.setOnClickListener(v -> selectedButtonGraph(binding.btnWeek));
        binding.btnMonth.setOnClickListener(v -> selectedButtonGraph(binding.btnMonth));
        binding.btnAllTime.setOnClickListener(v -> selectedButtonGraph(binding.btnAllTime));
    }

    private void updateProgressWeight() {
        if (currentWeight <= 0 || reachableWeight <= 0) {
            binding.ProgressPercent.setText("0%");
            binding.progressBar.setProgress(0);
        }
        else {
            int progressPercentage = (int)(((float)reachableWeight / currentWeight) * 100);
            progressPercentage = Math.min(progressPercentage, 100);

            binding.ProgressPercent.setText(progressPercentage + "%");
            binding.progressBar.setProgress(progressPercentage);
        }
    }

    private void visibilityObjectiveWeight() {
        binding.addObjectiveWeight.setOnClickListener(view -> {
            if (binding.addObjectiveWeightBloc.getVisibility() == View.GONE) {
                binding.addObjectiveWeightBloc.setVisibility(View.VISIBLE);
                binding.addObjectiveWeight.setText("Cacher");
            } else {
                binding.addObjectiveWeightBloc.setVisibility(View.GONE);
                binding.addObjectiveWeight.setText("Un objectif ?");
            }
        });
    }

    private void addNewObjectiveWeight() {
        binding.btnSaveObjectiveWeight.setOnClickListener(view -> {
            try {
                String weightText = binding.editObjectiveTextWeight.getText().toString();
                if (!weightText.isEmpty()) {
                    reachableWeight = Double.parseDouble(weightText);
                    displayObjectiveWeight();
                    updateProgressWeight();
                    binding.editObjectiveTextWeight.getText().clear();
                    binding.addObjectiveWeightBloc.setVisibility(View.GONE);

                    Toast.makeText(getContext(), "Objectif de " + reachableWeight + " kg enregistré !",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext()," Le champ est vide, veuillez écrire quelque chose !",
                            Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(),"Erreur : format non valide",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCurrentWeight() {
        binding.btnSaveWeight.setOnClickListener(view -> {
            try {
                String weightText = binding.editTextWeight.getText().toString();
                if (!weightText.isEmpty()) {
                    currentWeight = Double.parseDouble(weightText);
                    updateProgressWeight();
                    updateGraphWeight(currentWeight);
                    binding.editTextWeight.getText().clear();

                    Toast.makeText(getContext(), currentWeight + " kg a été enregistré pour cette semaine !",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext()," Le champ est vide, veuillez écrire quelque chose !",
                            Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(),"Erreur : format non valide",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectedButtonGraph(MaterialButton selectedButton) {
        MaterialButton[] buttons = {
                binding.btnWeek,
                binding.btnMonth,
                binding.btnAllTime
        };
        for (MaterialButton button : buttons) {
            if (button.equals(selectedButton)) {
                button.setTextAppearance(requireContext(), R.style.MyButtonSelected);
                button.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.white));
            } else {
                button.setTextAppearance(requireContext(), R.style.MyButtonUnselected);
                button.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.transparent));
            }
        }

        // Mettre à jour le graphique selon la période sélectionnée
        if (selectedButton == binding.btnWeek) {
            displayWeekData();
        } else if (selectedButton == binding.btnMonth) {
            displayMonthData();
        } else if (selectedButton == binding.btnAllTime) {
            displayYearData();
        }
    }

    private void setupWeightGraph() {
        // Configuration des principaux éléments du graphe
        binding.barChart.getDescription().setEnabled(false);
        binding.barChart.setDrawGridBackground(false);
        binding.barChart.getAxisRight().setEnabled(false);

        // Configuration des axes
        XAxis xAxis = binding.barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis yAxis = binding.barChart.getAxisLeft();
        yAxis.setDrawGridLines(true);
        yAxis.setGranularity(2f); // Graduation 2 par 2

        // Créer et ajouter un DataSet vide
        BarDataSet dataSet = new BarDataSet(entries, "Poids");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);

        BarData data = new BarData(dataSet);
        binding.barChart.setData(data);
        binding.barChart.invalidate();
    }

    private void updateGraphWeight(double weight) {
        // Ajouter la nouvelle valeur au graphique
        entries.add(new BarEntry(entryCount++, (float) weight));

        // Mettre à jour le DataSet avec les nouvelles données
        BarDataSet dataSet = new BarDataSet(entries, "Poids");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);

        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(12f);

        // Mettre à jour le graphique
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.05f); // Ajuster la largeur des barres

        binding.barChart.setData(data);
        binding.barChart.notifyDataSetChanged();
        binding.barChart.invalidate();

    }

    private void displayObjectiveWeight() {
        binding.actualObjectiveWeight.setText(String.valueOf(reachableWeight + "kg"));
    }

    private void displayWeekData() {
        //TODO
    }

    private void displayMonthData() {
        //TODO
    }

    private void displayYearData() {
        //TODO
    }
}