package com.example.android_sante;

import android.os.Bundle;
import android.widget.*;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
// Supprimez cette ligne si ContextCompat n'est pas utilisé, sinon assurez-vous de l'importation.
// import androidx.core.content.ContextCompat;

import com.google.android.material.chip.ChipGroup;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ActivitiesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ActivitiesFragment() {
        // Required empty public constructor
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
        setupChipGroupListener(); // Configurer le listener avant de potentiellement changer l'état du ChipGroup

        // Assurer qu'un filtre est appliqué au démarrage
        // Si R.id.chipToutes est votre chip par défaut dans le XML avec android:checked="true",
        // cette partie est redondante car le listener sera appelé.
        // Sinon, forcez un check ici.
        if (chipGroupFilter.getCheckedChipId() == View.NO_ID) {
            // Mettez ici l'ID du chip que vous voulez voir sélectionné par défaut
            // Par exemple, R.id.chipToutes ou R.id.chipPopulaires
            chipGroupFilter.check(R.id.chipToutes); // Ceci déclenchera le listener et filterActivities
        } else {
            // Si un chip est déjà coché (par ex. via XML), lancer le filtrage initial
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
        // Si on essaie de sélectionner un bouton non visible ou null, on ne fait rien.
        // Ou, si on veut désélectionner l'actuel, on pourrait ajouter cette logique ici.
        if (buttonToSelect == null || buttonToSelect.getVisibility() == View.GONE) {
            // Optionnel: si on veut désélectionner le bouton actif si on clique sur "rien"
            // if (selectedActivityButton != null) {
            //     selectedActivityButton.setSelected(false);
            //     selectedActivityButton = null;
            // }
            return;
        }

        // Si le bouton à sélectionner est déjà celui qui est sélectionné, ne rien faire.
        if (selectedActivityButton == buttonToSelect) {
            return;
        }

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
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show(); // Afficher le message
        });
    }

    private void setupChipGroupListener() {
        if (chipGroupFilter == null) return;
        chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> {
            // Si aucun chip n'est sélectionné (possible si clearCheckable est true et l'utilisateur désélectionne)
            // On peut choisir de forcer une sélection par défaut ou gérer ce cas.
            if (checkedId == View.NO_ID) {
                // Par exemple, sélectionner le premier chip par défaut si rien n'est coché.
                // chipGroupFilter.check(R.id.chipToutes); // Déclenchera à nouveau le listener
                // Ou simplement ne rien filtrer / afficher un message.
                // Pour l'instant, on suppose qu'un chip est toujours coché.
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
                } else { // chipPopulaires est coché
                    shouldBeVisible = (tag != null && "popular".equals(tag.toString()));
                }

                if (shouldBeVisible) {
                    button.setVisibility(View.VISIBLE);
                } else {
                    button.setVisibility(View.GONE);
                    // Si ce bouton caché était celui sélectionné, on ne le désélectionne pas encore ici,
                    // car selectedActivityButton pourrait être ce bouton.
                    // La désélection du selectedActivityButton caché est gérée après la boucle.
                    // Cependant, si un bouton est caché et N'EST PAS le selectedActivityButton mais est quand même
                    // dans un état sélectionné (ce qui ne devrait pas arriver avec une logique correcte),
                    // on pourrait le forcer ici : if (button.isSelected()) button.setSelected(false);
                }
            }
        }

        // Après avoir mis à jour la visibilité de tous les boutons :
        if (selectedActivityButton != null && selectedActivityButton.getVisibility() == View.GONE) {
            // Le bouton précédemment sélectionné est maintenant caché.
            // Il faut le désélectionner visuellement et logiquement.
            selectedActivityButton.setSelected(false); // <- CORRECTION IMPORTANTE
            selectedActivityButton = null;
            selectFirstVisibleActivity(); // Sélectionner un nouveau bouton parmi les visibles
        } else if (selectedActivityButton == null) {
            // Aucun bouton n'est actuellement sélectionné (soit au démarrage, soit après la désélection ci-dessus)
            selectFirstVisibleActivity();
        }
        // Si selectedActivityButton n'est pas null et est visible, il reste sélectionné, rien à faire.
    }

    private void selectFirstVisibleActivity() {
        if (gridActivities == null) return;
        ImageButton firstVisibleButton = null;
        for (int i = 0; i < gridActivities.getChildCount(); i++) {
            View child = gridActivities.getChildAt(i);
            if (child instanceof ImageButton && child.getVisibility() == View.VISIBLE) {
                firstVisibleButton = (ImageButton) child;
                break; // On a trouvé le premier, on peut arrêter
            }
        }

        if (firstVisibleButton != null) {
            selectActivityButton(firstVisibleButton); // Ceci va gérer la désélection de l'ancien s'il y en avait un
        } else {
            // Aucun bouton n'est visible. S'il y avait un bouton sélectionné, il faut le désélectionner.
            if (selectedActivityButton != null) {
                selectedActivityButton.setSelected(false); // <- CORRECTION IMPORTANTE
                selectedActivityButton = null;
            }
        }
    }

    // Supprimer cette méthode si elle est un duplicata ou une version erronée de selectActivityButton
    // private void SelectActivityButton(ImageButton buttonToSelect){ ... }
}