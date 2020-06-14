package com.example.pokedex.model;

import java.util.ArrayList;

public class PokemonOfAType {
    private ArrayList<Pokemon> pokemon;

    public ArrayList<PokemonOfAType.Pokemon> getPokemon() {
        return pokemon;
    }

    public void setPokemon(ArrayList<PokemonOfAType.Pokemon> pokemon) {
        this.pokemon = pokemon;
    }

    public static class Pokemon{
        private PokemonApiResult.Pokemon pokemon;

        public PokemonApiResult.Pokemon getPokemon() {
            return pokemon;
        }

        public void setPokemon(PokemonApiResult.Pokemon pokemon) {
            this.pokemon = pokemon;
        }
    }
}
