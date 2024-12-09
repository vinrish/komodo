package org.vinrish.komodo;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.vinrish.komodo.databinding.FragmentAnimalDetailBinding;

import java.util.ArrayList;
import java.util.List;

public class AnimalDetailFragment extends Fragment {
    private FragmentAnimalDetailBinding binding;
    private FirebaseFirestore database;
    private EndangeredDescriptionsAdapter descriptionAdapter;
    private List<String> descriptions;
    private String endangeredAnimalDocumentId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAnimalDetailBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseFirestore.getInstance();

        AnimalDetailFragmentArgs args = AnimalDetailFragmentArgs.fromBundle(getArguments());
        endangeredAnimalDocumentId = args.getAnimalDocumentId();

        binding.detailAnimalName.setText(args.getAnimalName());

        Glide.with(requireContext())
                .asBitmap()
                .load(args.getAnimalImageUrl())
                .error(R.drawable.tiger)
                .into(binding.detailAnimalImage);

        binding.detailAnimalPopulation.setText("Population: " + args.getAnimalPopulation());

        binding.recyclerViewDescriptions.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewDescriptions.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int spacing = 8;
                outRect.top = spacing;
                outRect.bottom = spacing;
            }
        });
        descriptions = new ArrayList<>();
        descriptionAdapter = new EndangeredDescriptionsAdapter(descriptions);
        binding.recyclerViewDescriptions.setAdapter(descriptionAdapter);
        fetchDescriptions(endangeredAnimalDocumentId);

        binding.buttonAddDescription.setOnClickListener(v -> {
            if (isUserLoggedIn()) {
                showAddDescriptionDialog();
            } else {
                Snackbar.make(requireView(), "Please log in to add a description", Snackbar.LENGTH_SHORT).show();
                redirectToLogin();
            }
        });

    }

    private boolean isUserLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private void redirectToLogin() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.loginFragment);
    }

    private void showAddDescriptionDialog() {
        EditText input = new EditText(getContext());
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Add Description")
                .setMessage("Enter a new description for the animal")
                .setView(input)
                .setPositiveButton("Add", (dialogInterface, i) -> {
                    String descriptionText = input.getText().toString().trim();
                    if (!descriptionText.isEmpty()) {
                        addDescriptionToFirestore(descriptionText);
                    } else {
                        Snackbar.make(requireView(), "Description cannot be empty", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void addDescriptionToFirestore(String descriptionText) {
        EndangeredDescription description = new EndangeredDescription(descriptionText);

        database.collection("animals")
                .document(endangeredAnimalDocumentId)
                .collection("descriptions")
                .add(description)
                .addOnSuccessListener(documentReference -> {
                    Snackbar.make(requireView(), "Description added", Snackbar.LENGTH_SHORT).show();
                    descriptions.add(descriptionText);
                    descriptionAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(requireView(), "Failed to add description", Snackbar.LENGTH_SHORT).show();
                });
    }

    private void fetchDescriptions(String documentId) {
        database.collection("animals")
                .document(documentId)
                .collection("descriptions")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> descriptionsList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            EndangeredDescription description = document.toObject(EndangeredDescription.class);
                            descriptionsList.add(description.getDescription());
                        }
                        descriptions.clear();
                        descriptions.addAll(descriptionsList);
                        descriptionAdapter.notifyDataSetChanged();
                    } else {
                        binding.detailAnimalPopulation.setText("Failed to load endangered descriptions.");
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}