package com.example.android_sante;

import android.os.Bundle;
import android.widget.*;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.chip.ChipGroup;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ActivitiesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

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
    }

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

        if (chipGroupFilter.getCheckedChipId() == View.NO_ID) {
            chipGroupFilter.check(R.id.chipToutes);
        } else {
            filterActivities(chipGroupFilter.getCheckedChipId());
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

            String activityType = selectedActivityButton.getContentDescription().toString();
            String durationText = tvDuration.getText().toString();
            String message = String.format(Locale.getDefault(),
                    "Activité '%s' ajoutée pour %s heures.",
                    activityType, durationText);
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        });
    }

    private void setupChipGroupListener() {
        if (chipGroupFilter == null) return;
        chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == View.NO_ID) {
            } else {
                filterActivities(checkedId);
            }
        });
    }

    private void filterActivities(int checkedId) {
        if (gridActivities == null) return;

        boolean showAll = (checkedId == R.id.chipToutes);

        for (int i = 0; i < gridActivities.getChildCount(); i++) {
            View child = gridActivities.getChildAt(i);
            if (child instanceof ImageButton) {
                ImageButton button = (ImageButton) child;
                Object tag = button.getTag();
                boolean shouldBeVisible;

                if (showAll) {
                    shouldBeVisible = true;
                } else {
                    shouldBeVisible = (tag != null && "popular".equals(tag.toString()));
                }

                if (shouldBeVisible) {
                    button.setVisibility(View.VISIBLE);
                } else {
                    button.setVisibility(View.GONE);
                }
            }
        }

        if (selectedActivityButton != null && selectedActivityButton.getVisibility() == View.GONE) {
            selectedActivityButton.setSelected(false);
            selectedActivityButton = null;
            selectFirstVisibleActivity();
        } else if (selectedActivityButton == null) {
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

        if (firstVisibleButton != null) {
            selectActivityButton(firstVisibleButton);
        } else {
            if (selectedActivityButton != null) {
                selectedActivityButton.setSelected(false);
                selectedActivityButton = null;
            }
        }
    }
}