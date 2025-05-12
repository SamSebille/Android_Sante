package com.example.android_sante; // Assurez-vous que le package correspond à votre projet

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ActivitiesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "ActivitiesFragment";

    private String mParam1;
    private String mParam2;

    private String currentUserId;

    private TextView tvDuration;
    private ImageButton btnMinus, btnPlus;
    private Button btnOk;
    private GridLayout gridActivities;
    private ImageButton selectedActivityButton = null;
    private ChipGroup chipGroupFilter;

    private int durationMinutes = 30;
    private final int DURATION_STEP = 15;
    private final int MIN_DURATION = 15;
    private final int MAX_DURATION = 6 * 60;

    public ActivitiesFragment() {
    }

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

        if (getActivity() != null) {
            Intent intent = getActivity().getIntent();
            if (intent != null) {
                this.currentUserId = intent.getStringExtra("ID");
                if (this.currentUserId == null || this.currentUserId.isEmpty()) {
                    Log.e(TAG, "User ID non trouvé ou vide dans l'Intent.");
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Erreur: ID utilisateur manquant. Certaines fonctionnalités pourraient être affectées.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d(TAG, "User ID récupéré: " + this.currentUserId);
                }
            } else {
                Log.e(TAG, "L'Intent de l'activité est null. Impossible de récupérer l'ID utilisateur.");
            }
        } else {
            Log.e(TAG, "L'activité hôte est null lors de onCreate. Impossible de récupérer l'ID utilisateur.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activities, container, false);
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

        updateDurationDisplay();
        setupActivityButtons();
        setupTimeButtons();
        setupOkButton();
        setupChipGroupListener();

        if (chipGroupFilter.getCheckedChipId() == View.NO_ID && chipGroupFilter.getChildCount() > 0) {
            chipGroupFilter.check(R.id.chipPopulaires);
        }
        filterActivities(chipGroupFilter.getCheckedChipId());

        if (selectedActivityButton == null) {
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
        if (buttonToSelect == null || buttonToSelect.getVisibility() == View.GONE) {
            if (selectedActivityButton != null) {
                selectedActivityButton.setSelected(false);
            }
            selectedActivityButton = null;
            return;
        }

        if (selectedActivityButton == buttonToSelect) {
            return;
        }

        if (selectedActivityButton != null) {
            selectedActivityButton.setSelected(false);
        }

        buttonToSelect.setSelected(true);
        selectedActivityButton = buttonToSelect;
    }


    private void setupTimeButtons() {
        btnMinus.setOnClickListener(v -> adjustDuration(-DURATION_STEP));
        btnPlus.setOnClickListener(v -> adjustDuration(DURATION_STEP));
    }

    private void adjustDuration(int change) {
        int newDuration = durationMinutes + change;
        if (newDuration >= MIN_DURATION && newDuration <= MAX_DURATION) {
            durationMinutes = newDuration;
        } else if (newDuration < MIN_DURATION) {
            durationMinutes = MIN_DURATION;
        } else {
            durationMinutes = MAX_DURATION;
        }
        updateDurationDisplay();
    }

    private void updateDurationDisplay() {
        long hours = TimeUnit.MINUTES.toHours(durationMinutes);
        long minutes = durationMinutes % 60;
        String formattedTime = String.format(Locale.getDefault(), "%d:%02d", hours, minutes);
        tvDuration.setText(formattedTime);
    }

    private void setupOkButton() {
        btnOk.setOnClickListener(v -> {
            if (selectedActivityButton == null) {
                Toast.makeText(requireContext(), "Veuillez sélectionner une activité", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentUserId == null || currentUserId.isEmpty()) {
                Toast.makeText(requireContext(), "Erreur: ID utilisateur non disponible. Impossible d'enregistrer.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Tentative d'enregistrement d'activité sans User ID.");
                return;
            }

            String activityType = selectedActivityButton.getContentDescription().toString();
            Activity newActivity = new Activity(currentUserId, activityType, durationMinutes);

            JsonUtils.addActivityEntry(requireContext(), newActivity, "activity.json");

            String durationText = tvDuration.getText().toString();
            String message = String.format(Locale.getDefault(),
                    "Activité '%s' ajoutée pour %s.",
                    activityType, durationText);
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();

            Log.d(TAG, "Activité enregistrée dans " + "activity.json" + ": " + newActivity.toString());
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

        boolean showPopularOnly = (checkedId == R.id.chipPopulaires);

        boolean selectedButtonBecomesInvisible = false;
        if (selectedActivityButton != null) {
            Object tag = selectedActivityButton.getTag();

            if (showPopularOnly && (tag == null || !"popular".equals(tag.toString()))) {
                selectedButtonBecomesInvisible = true;
            }
        }

        for (int i = 0; i < gridActivities.getChildCount(); i++) {
            View child = gridActivities.getChildAt(i);
            if (child instanceof ImageButton) {
                ImageButton button = (ImageButton) child;
                Object tag = button.getTag();
                boolean isPopular = (tag != null && "popular".equals(tag.toString()));

                if (showPopularOnly) {
                    button.setVisibility(isPopular ? View.VISIBLE : View.GONE);
                } else {
                    button.setVisibility(View.VISIBLE);
                }
            }
        }

        if (selectedButtonBecomesInvisible) {
            selectedActivityButton.setSelected(false);
            selectedActivityButton = null;
            selectFirstVisibleActivity();
        } else if (selectedActivityButton == null || selectedActivityButton.getVisibility() == View.GONE) {
            selectFirstVisibleActivity();
        }
    }

    private void selectFirstVisibleActivity() {
        if (gridActivities == null) return;

        ImageButton firstVisibleButton = null;
        for (int i = 0; i < gridActivities.getChildCount(); i++) {
            View child = gridActivities.getChildAt(i);
            if (child instanceof ImageButton && child.getVisibility() == View.VISIBLE) {
                firstVisibleButton = (ImageButton) child;
                break;
            }
        }

        if (selectedActivityButton != null && selectedActivityButton != firstVisibleButton) {
            selectedActivityButton.setSelected(false);
        }

        selectedActivityButton = firstVisibleButton;

        if (selectedActivityButton != null) {
            selectedActivityButton.setSelected(true);
        }
    }
}