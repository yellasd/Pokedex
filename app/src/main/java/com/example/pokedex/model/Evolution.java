package com.example.pokedex.model;

import java.util.ArrayList;

public class Evolution {
   private ArrayList<TempClass> pokemon;
   public static class TempClass{
       private ArrayList<PokemonApiResult.Pokemon> next_evolution;
       private ArrayList<PokemonApiResult.Pokemon> prev_evolution;

       public ArrayList<PokemonApiResult.Pokemon> getNext_evolution() {
           return next_evolution;
       }

       public ArrayList<PokemonApiResult.Pokemon> getPrev_evolution() {
           return prev_evolution;
       }
   }

    public ArrayList<TempClass> getPokemon() {
        return pokemon;
    }
}
