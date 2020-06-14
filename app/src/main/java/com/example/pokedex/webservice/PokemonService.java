package com.example.pokedex.webservice;

import com.example.pokedex.model.Evolution;
import com.example.pokedex.model.PokemonApiResult;
import com.example.pokedex.model.PokemonDetails;
import com.example.pokedex.model.PokemonOfAType;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PokemonService {

    @GET("pokemon")
    Call<PokemonApiResult> getPokemon(@Query("limit")int limit, @Query("offset")int offset);

    @GET("type")
    Call<PokemonApiResult> getType();

    @GET("region")
    Call<PokemonApiResult> getLocation();

    @GET("pokemon/{id}/")
    Call<PokemonDetails> getPokemonDetails(@Path("id")String id);

    @GET("type/{id}")
    Call<PokemonOfAType> getPokemonOfAType(@Path("id")String id);

    @GET("Biuni/PokemonGO-Pokedex/master/pokedex.json")
    Call<Evolution> getEvolution();


}
