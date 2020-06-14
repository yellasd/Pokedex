package com.example.pokedex.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokedex.R;
import com.example.pokedex.model.PokemonApiResult;
import com.google.gson.TypeAdapter;

import java.util.ArrayList;

public class TypeAndLocAdapter extends RecyclerView.Adapter<TypeAndLocAdapter.MyViewHolder> {

    private Context context;
    private OnItemClickListener listener;
    private ArrayList<PokemonApiResult.Pokemon> types;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.typeandloc, parent, false);
        return new TypeAndLocAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String s=Character.toUpperCase(types.get(position).getName().charAt(0))+types.get(position).getName().substring(1);
        holder.textView.setText(s);
    }

    @Override
    public int getItemCount() {
        return types.size();
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }

    public TypeAndLocAdapter(Context context, ArrayList<PokemonApiResult.Pokemon> types) {
        this.context = context;
        this.types = types;
    }



    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.typeAndLoc);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

}
