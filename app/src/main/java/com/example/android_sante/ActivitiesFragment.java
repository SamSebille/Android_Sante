package com.example.android_sante;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat; // Pour la gestion des couleurs/drawables
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
    private ImageButton btnMinus, btnPlus; // Changé en ImageButton
    private MaterialButton btnOk;
    private GridLayout gridActivities;
    private ImageButton selectedActivityButton = null;
    private ChipGroup chipGroupFilter;
    // Supprimé : BottomNavigationView (non présent dans votre XML)

    private int durationMinutes = 30; // Durée initiale en minutes
    private final int DURATION_STEP = 15; // Incrément/décrément en minutes
    private final int MIN_DURATION = 15; // Durée minimale
    private final int MAX_DURATION = 6 * 60; // Durée maximale (ex: 6 heures)

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflater le layout pour ce fragment
        View view = inflater.inflate(R.layout.fragment_activities, container, false); // Assurez-vous que le nom du layout est correct
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialisation des vues APRES que la vue ait été créée
        // Utilisez view.findViewById car nous sommes dans un Fragment
        tvDuration = view.findViewById(R.id.tvDuration);
        btnMinus = view.findViewById(R.id.btnMinus); // Trouve l'ImageButton
        btnPlus = view.findViewById(R.id.btnPlus);   // Trouve l'ImageButton
        btnOk = view.findViewById(R.id.btnOk);
        gridActivities = view.findViewById(R.id.gridActivities);
        chipGroupFilter = view.findViewById(R.id.chipGroupFilter);
        // Supprimé : Initialisation BottomNavigationView

        // Configuration initiale
        updateDurationDisplay();
        setupActivityButtons();
        setupTimeButtons();
        setupOkButton();
        setupChipGroupListener(); // Ajout pour gérer les filtres si besoin

        // Pré-sélectionner une activité (ex: Marche) si nécessaire
        // Assurez-vous d'avoir ajouté le background selector aux ImageButtons dans le XML
        ImageButton initialButton = view.findViewById(R.id.btnActivityWalk);
        if (initialButton != null) {
            selectActivityButton(initialButton);
        }
    }

    private void setupActivityButtons() {
        // Assurez-vous que gridActivities a bien été trouvé
        if (gridActivities == null) return;

        for (int i = 0; i < gridActivities.getChildCount(); i++) {
            View child = gridActivities.getChildAt(i);
            if (child instanceof ImageButton) {
                ImageButton button = (ImageButton) child;
                // IMPORTANT: Pour que la sélection visuelle fonctionne, ajoutez
                // android:background="@drawable/activity_button_selector"
                // à chaque ImageButton dans votre XML.
                button.setOnClickListener(v -> selectActivityButton(button));
            }
        }
    }

    private void selectActivityButton(ImageButton buttonToSelect) {
        // Désélectionner l'ancien bouton s'il existe
        if (selectedActivityButton != null) {
            selectedActivityButton.setSelected(false);
            // Si vous n'utilisez pas le sélecteur de fond, vous devrez manuellement
            // changer le fond ici (par ex. remettre le fond gris)
            //selectedActivityButton.setBackgroundResource(R.drawable.background_gris_normal); // Exemple
            //selectedActivityButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.colorPrimaryText)); // Exemple pour remettre teinte
        }

        // Sélectionner le nouveau bouton
        buttonToSelect.setSelected(true);
        // Si vous n'utilisez pas le sélecteur de fond, vous devrez manuellement
        // changer le fond ici (par ex. mettre le fond bleu avec bordure)
        // buttonToSelect.setBackgroundResource(R.drawable.background_bleu_selection); // Exemple
        // Vous pouvez aussi changer la teinte de l'icône si désiré
        // buttonToSelect.setColorFilter(ContextCompat.getColor(requireContext(), R.color.Primary_blue));

        selectedActivityButton = buttonToSelect;

        // Vous pouvez stocker le type d'activité ici si besoin
        // String activityType = buttonToSelect.getContentDescription().toString();
        // Log.d("ActivitySelection", "Selected: " + activityType);
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
        updateDurationDisplay(); // Mettre à jour l'affichage dans tous les cas pour refléter le blocage éventuel
    }

    private void updateDurationDisplay() {
        long hours = TimeUnit.MINUTES.toHours(durationMinutes);
        long minutes = durationMinutes % 60; // Utiliser modulo pour les minutes restantes

        // Formatte en H:MM (ex: 0:30, 1:15, 2:00)
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
            //Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();

            // Optionnel: Naviguer vers un autre fragment ou fermer quelque chose
            // Exemple : getParentFragmentManager().popBackStack();
        });
    }

    private void setupChipGroupListener() {
        chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipPopulaires) {
                // Logique pour filtrer/afficher les activités populaires
                //Toast.makeText(requireContext(), "Filtre: Populaires", Toast.LENGTH_SHORT).show();
                // Ici, vous pourriez masquer/afficher certains boutons dans la grille,
                // ou recharger une liste différente si la grille est dynamique.
            } else if (checkedId == R.id.chipToutes) {
                // Logique pour afficher toutes les activités
                //Toast.makeText(requireContext(), "Filtre: Toutes", Toast.LENGTH_SHORT).show();
                // Afficher tous les boutons/activités.
            } else {
                // Aucun chip sélectionné (si selectionRequired=false) ou état inattendu
            }
        });
    }
}