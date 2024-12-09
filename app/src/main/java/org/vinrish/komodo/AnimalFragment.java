package org.vinrish.komodo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.vinrish.komodo.databinding.FragmentAnimalBinding;

import java.util.ArrayList;
import java.util.List;

public class AnimalFragment extends Fragment implements EndangeredAnimalAdapter.OnAnimalClickListener{

    private EndangeredAnimalAdapter adapter;
    private FragmentAnimalBinding binding;
    private FirebaseFirestore database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAnimalBinding.inflate(inflater, container, false);

        database = FirebaseFirestore.getInstance();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerViewAnimals.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchAnimalsFromFirestore();
    }

    private void fetchAnimalsFromFirestore() {
        database.collection("animals")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<EndangeredAnimal> animalList = new ArrayList<>();
                        QuerySnapshot documents = task.getResult();

                        for (DocumentSnapshot document : documents) {
                            String documentId = document.getId();
                            String name = document.getString("name");
                            int population = document.getLong("population").intValue();
                            String imageUrl = document.getString("imageUrl");

                            EndangeredAnimal animal = new EndangeredAnimal(documentId, name, population, imageUrl);
                            animalList.add(animal);
                        }

                        adapter = new EndangeredAnimalAdapter(animalList, this);
                        binding.recyclerViewAnimals.setAdapter(adapter);
                    }
                });
    }

    @Override
    public void onAnimalClick(EndangeredAnimal animal) {
        AnimalFragmentDirections.ActionAnimalFragmentToAnimalDetailFragment action =
                AnimalFragmentDirections.actionAnimalFragmentToAnimalDetailFragment(
                        animal.getDocumentId(),
                        animal.getName(),
                        animal.getImageUrl(),
                        animal.getPopulation()
                );
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}