package com.example.pokedex.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokedex.R;
import com.example.pokedex.model.PokemonApiResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private ArrayList<PokemonApiResult.Pokemon> pokemonArrayList;
    private ArrayList<PokemonApiResult.Pokemon> tempArrayList;

    private OnItemClickListener listener;

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<PokemonApiResult.Pokemon> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(tempArrayList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (PokemonApiResult.Pokemon pokemon : tempArrayList) {
                    if ((pokemon.getName().toLowerCase() + pokemon.getNumber()).contains(filterPattern))
                        filteredList.add(pokemon);
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            pokemonArrayList.clear();
            pokemonArrayList.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

    public interface OnItemClickListener {
        void onItemClick(int position, ImageView imageView);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public PokemonAdapter(Context context, ArrayList<PokemonApiResult.Pokemon> pokemonArrayList) {
        this.context = context;
        this.pokemonArrayList = pokemonArrayList;

        tempArrayList = new ArrayList<>(pokemonArrayList);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pokemon_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String s = Character.toUpperCase(pokemonArrayList.get(position).getName().charAt(0)) + pokemonArrayList.get(position).getName().substring(1);
        String string = s + "-" + pokemonArrayList.get(position).getNumber();
        holder.textView.setText(string);
        if (pokemonArrayList.get(position).getNumber() < 650)
            Picasso.with(context)
                    .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/back/" + pokemonArrayList.get(position).getNumber() + ".png")
                    .into(holder.imageView);
        else
            Picasso.with(context)
                    .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemonArrayList.get(position).getNumber() + ".png")
                    .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return pokemonArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;
        ImageView shareImage;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position, imageView);
                        }
                    }
                }
            });
        }
    }

}
