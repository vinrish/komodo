package org.vinrish.komodo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class EndangeredAnimalAdapter extends RecyclerView.Adapter<EndangeredAnimalAdapter.AnimalViewHolder> {
    private List<EndangeredAnimal> animalList;
    private OnAnimalClickListener listener;

    public interface OnAnimalClickListener {
        void onAnimalClick(EndangeredAnimal animal);
    }

    public EndangeredAnimalAdapter(List<EndangeredAnimal> animalList, OnAnimalClickListener listener) {
        this.animalList = animalList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_animal, parent, false);
        return new AnimalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalViewHolder holder, int position) {
        EndangeredAnimal animal = animalList.get(position);
        Log.d("AnimalAdapter", "Image URL: " + animal.getImageUrl());
        holder.bind(animal, listener);
    }

    @Override
    public int getItemCount() {
        return animalList.size();
    }

    static class AnimalViewHolder extends RecyclerView.ViewHolder {
        ImageView animalImage;
        TextView animalName;
        TextView animalPopulation;

        AnimalViewHolder(@NonNull View itemView) {
            super(itemView);
            animalImage = itemView.findViewById(R.id.animal_image);
            animalName = itemView.findViewById(R.id.animal_name);
            animalPopulation = itemView.findViewById(R.id.animal_population);
        }

        void bind(final EndangeredAnimal animal, final OnAnimalClickListener listener) {
            Glide.with(itemView.getContext())
                    .load(animal.getImageUrl())
                    .error(R.drawable.tiger)
                    .into(animalImage);

            animalName.setText(animal.getName());
            animalPopulation.setText("Population: " + animal.getPopulation());

            itemView.setOnClickListener(v -> listener.onAnimalClick(animal));
        }
    }
}