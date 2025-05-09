package com.example.android_sante;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.android_sante.databinding.FragmentLunchBinding;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LunchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LunchFragment extends Fragment {
    private FragmentLunchBinding binding;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String id = "";

    private Lunch lunch;

    private List<Food> meat = new ArrayList<Food>();
    private List<Food> fish = new ArrayList<Food>();
    private List<Food> vegetable = new ArrayList<Food>();
    private List<Food> cereal = new ArrayList<Food>();

    private ArrayAdapter<String> adapterFood;
    public LunchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LunchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LunchFragment newInstance(String param1, String param2) {
        LunchFragment fragment = new LunchFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLunchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getActivity().getIntent();

        this.id = intent.getStringExtra("ID");
        if (!id.isBlank())
            this.lunch = JsonUtils.getLunch(getContext(), Integer.parseInt(id));

        List<Food> foods = JsonUtils.getFood(getContext());

        if (foods != null) {
            foods.forEach(food  -> {
                if (food.getTypeFood().equals("meat")) {
                    meat.add(food);
                } else if (food.getTypeFood().equals("fish")) {
                    fish.add(food);
                } else if (food.getTypeFood().equals("vegetable")) {
                    vegetable.add(food);
                } else if (food.getTypeFood().equals("cereal")) {
                    cereal.add(food);
                }
            });
            System.out.println("LOG meat: " + meat.size() + meat.toString());
            System.out.println("LOG fish: " + fish.size() + fish.toString());
            System.out.println("LOG vegetable: " + vegetable.size() + vegetable.toString());
            System.out.println("LOG cereal: " + cereal.size() + cereal.toString());
        }

        // Sélection par défaut
        selectedButtonGraph(binding.btnMeat);

        // Listeners
        binding.btnMeat.setOnClickListener(v -> selectedButtonGraph(binding.btnMeat));
        binding.btnFish.setOnClickListener(v -> selectedButtonGraph(binding.btnFish));
        binding.btnvegetables.setOnClickListener(v -> selectedButtonGraph(binding.btnvegetables));
        binding.btnCereales.setOnClickListener(v -> selectedButtonGraph(binding.btnCereales));
    }

    private void selectedButtonGraph(MaterialButton selectedButton) {
        MaterialButton[] buttons = {
                binding.btnMeat,
                binding.btnFish,
                binding.btnvegetables,
                binding.btnCereales
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
        if (selectedButton == binding.btnMeat) {
            displayfoods(meat);
        } else if (selectedButton == binding.btnFish) {
            displayfoods(fish);
        } else if (selectedButton == binding.btnvegetables) {
            displayfoods(vegetable);
        } else if (selectedButton == binding.btnCereales) {
            displayfoods(cereal);
        }
    }

    private void displayfoods(List<Food> foods) {
        List<String> foodNames = new ArrayList<>();
        for (Food food : foods) {
            foodNames.add(food.getNameFood());
        }
        binding.autoFood.setText(null, false);
        adapterFood = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, foodNames);
        binding.autoFood.setAdapter(adapterFood);
    }
}