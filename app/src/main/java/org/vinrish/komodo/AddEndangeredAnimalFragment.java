package org.vinrish.komodo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.vinrish.komodo.databinding.FragmentAddAnimalBinding;

public class AddEndangeredAnimalFragment extends Fragment {

    private FragmentAddAnimalBinding binding;
    private FirebaseFirestore database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddAnimalBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseFirestore.getInstance();

        binding.btnAddAnimal.setOnClickListener(v -> addAnimal());
    }

    private void addAnimal() {
        String animalName = binding.etAnimalName.getText().toString().trim();
        String populationStr = binding.etAnimalPopulation.getText().toString().trim();
        String imageUrl = binding.etImageUrl.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();

        if (TextUtils.isEmpty(animalName)) {
            Snackbar.make(requireView(), "Endangered Animal name is required", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(populationStr)) {
            Snackbar.make(requireView(), "Endangered Population is required", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(imageUrl)) {
            Snackbar.make(requireView(), "Endangered Image URL is required", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            Snackbar.make(requireView(), "Endangered Description is required", Snackbar.LENGTH_SHORT).show();
            return;
        }

        int population = Integer.parseInt(populationStr);
        EndangeredAnimal animal = new EndangeredAnimal(animalName, population, imageUrl);

        database.collection("animals")
                .add(animal)
                .addOnSuccessListener(documentReference -> {
                    Snackbar.make(requireView(), "Endangered Animal added successfully", Snackbar.LENGTH_SHORT).show();
                    addDescription(documentReference.getId(), description);

                    binding.etAnimalName.setText("");
                    binding.etAnimalPopulation.setText("");
                    binding.etImageUrl.setText("");
                    binding.etDescription.setText("");
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(requireView(), "Error adding Endangered animal", Snackbar.LENGTH_SHORT).show();
                });
    }

    private void addDescription(String animalId, String description) {
        CollectionReference descriptionsRef = database.collection("animals")
                .document(animalId)
                .collection("descriptions");

        descriptionsRef.add(new EndangeredDescription(description))
                .addOnSuccessListener(aVoid -> {
                    Snackbar.make(requireView(), "Description added successfully", Snackbar.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(requireView(), "Error adding description", Snackbar.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}