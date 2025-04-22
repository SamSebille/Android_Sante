package com.example.android_sante;

import android.os.Bundle;
import android.widget.*;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActivitiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivitiesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ActivitiesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ActivitiesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActivitiesFragment newInstance(String param1, String param2) {
        ActivitiesFragment fragment = new ActivitiesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private TextView tvDuration;
    private ImageButton btnMinus, btnPlus;
    private Button btnOk;
    private GridLayout gridActivities;
    private ImageButton selectedActivityButton = null;
    private ChipGroup chipGroupFilter;

    private int durationMinutes = 30; // Durée initiale en minutes
    private final int DURATION_STEP = 15; // Incrément/décrément en minutes
    private final int MIN_DURATION = 15; // Durée minimale
    private final int MAX_DURATION = 6 * 60; // Durée maximale (ex: 6 heures)

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflater le layout pour ce fragment
        View view = inflater.inflate(R.layout.fragment_activities, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvDuration = view.findViewById(R.id.tvDuration);
        btnMinus = view.findViewById(R.id.btnMinus);
        btnPlus = view.findViewById(R.id.btnPlus);
        btnOk = view.findViewById(R.id.btnOk);
        gridActivities = view.findViewById(R.id.gridActivities);
        chipGroupFilter = view.findViewById(R.id.chipGroupFilter);

        // Configuration initiale
        updateDurationDisplay();
        setupActivityButtons();
        setupTimeButtons();
        setupOkButton();
        setupChipGroupListener();

        filterActivities(chipGroupFilter.getCheckedChipId());

        ImageButton initialButton = view.findViewById(R.id.btnActivityWalk);
        if (initialButton != null && initialButton.getVisibility() == View.VISIBLE) {
            selectActivityButton(initialButton);
        } else {
            selectFirstVisibleActivity();
        }
    }

    private void setupActivityButtons() {
        if (gridActivities == null) return;

        for (int i = 0; i < gridActivities.getChildCount(); i++) {
            View child = gridActivities.getChildAt(i);
            if (child instanceof ImageButton) {
                ImageButton button = (ImageButton) child;
                button.setOnClickListener(v -> selectActivityButton(button));
            }
        }
    }

    private void selectActivityButton(ImageButton buttonToSelect) {
        // Désélectionner l'ancien bouton s'il existe
        if (selectedActivityButton != null) {
            selectedActivityButton.setSelected(false);
        }

        // Sélectionner le nouveau bouton
        buttonToSelect.setSelected(true);

        selectedActivityButton = buttonToSelect;
    }


    private void setupTimeButtons() {
        btnMinus.setOnClickListener(v -> adjustDuration(-DURATION_STEP));
        btnPlus.setOnClickListener(v -> adjustDuration(DURATION_STEP));
    }

    private void adjustDuration(int change) {
        int newDuration = durationMinutes + change;
        // Appliquer les limites
        if (newDuration >= MIN_DURATION && newDuration <= MAX_DURATION) {
            durationMinutes = newDuration;
        } else if (newDuration < MIN_DURATION) {
            durationMinutes = MIN_DURATION;
        } else { // newDuration > MAX_DURATION
            durationMinutes = MAX_DURATION;
        }
        updateDurationDisplay();
    }

    private void updateDurationDisplay() {
        long hours = TimeUnit.MINUTES.toHours(durationMinutes);
        long minutes = durationMinutes % 60;

        // Formatte en H:MM
        String formattedTime = String.format(Locale.getDefault(), "%d:%02d", hours, minutes);
        tvDuration.setText(formattedTime);
    }

    private void setupOkButton() {
        btnOk.setOnClickListener(v -> {
            if (selectedActivityButton == null) {
                // Utilisez requireContext() dans un Fragment pour obtenir le Context
                Toast.makeText(requireContext(), "Veuillez sélectionner une activité", Toast.LENGTH_SHORT).show();
                return;
            }

            String activityType = selectedActivityButton.getContentDescription().toString();
            String durationText = tvDuration.getText().toString();

            // Action à effectuer (ex: enregistrer, afficher un résumé)
            String message = String.format(Locale.getDefault(),
                    "Activité '%s' ajoutée pour %s heures.",
                    activityType, durationText);
        });
    }

    private void setupChipGroupListener() {

        if (chipGroupFilter == null) return;

        chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> {
            filterActivities(checkedId);
        });
    }

    private void filterActivities(int checkedId) {
        if (gridActivities == null) return;

        boolean showAll = (checkedId == R.id.chipToutes);

        for (int i = 0; i < gridActivities.getChildCount(); i++){
            View child = gridActivities.getChildAt(i);
            if (child instanceof ImageButton) {
                ImageButton button = (ImageButton) child;
                Object tag = button.getTag();

                if (showAll) {
                    button.setVisibility(View.VISIBLE);
                } else {
                    if (tag != null && "popular".equals(tag.toString())){
                        button.setVisibility(View.VISIBLE);
                    } else {
                        button.setVisibility(View.GONE);
                    }
                }
            }
        }

        if (selectedActivityButton != null && selectedActivityButton.getVisibility() == View.GONE){
            selectedActivityButton.setBackgroundResource(R.drawable.activity_button_selector);

            selectedActivityButton.setActivated(false);

            selectedActivityButton = null;
            selectFirstVisibleActivity();
        }
    }

    private void selectFirstVisibleActivity() {
        if (gridActivities == null) return;
        for (int i = 0; i < gridActivities.getChildCount(); i++) {
            View child = gridActivities.getChildAt(i);
            if (child instanceof ImageButton && child.getVisibility() == View.VISIBLE) {
                selectActivityButton((ImageButton) child);
                break;
            }
        }
    }

    private void SelectActivityButton(ImageButton buttonToSelect){
        if (buttonToSelect.getVisibility() == View.GONE){
            return;
        }

        if (selectedActivityButton != null && selectedActivityButton != buttonToSelect){
            selectedActivityButton.setActivated(false);
        }

        buttonToSelect.setActivated(true);

        selectedActivityButton = buttonToSelect;
    }

}