package com.example.pokedex.webservice;

import android.content.AsyncTaskLoader;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pokedex.model.PokemonApiResult;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;

public class PokemonLoader extends AsyncTaskLoader<ArrayList<PokemonApiResult.Pokemon>>  {

    private ArrayList<PokemonApiResult.Pokemon> pokemons;

    public PokemonLoader(@NonNull Context context) {
        super(context);
    }

    @Nullable
    @Override
    public ArrayList<PokemonApiResult.Pokemon> loadInBackground() {
        int limit=807;
        int offset=0;
        PokemonService service = RetrofitClient.getClient().create(PokemonService.class);
        Call<PokemonApiResult> call = service.getPokemon(limit, offset);
        try {
            PokemonApiResult apiResult=call.execute().body();
            assert apiResult != null;
            pokemons=apiResult.getResults();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pokemons;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
