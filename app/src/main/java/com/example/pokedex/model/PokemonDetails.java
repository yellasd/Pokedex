package com.example.pokedex.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PokemonDetails {

    @SerializedName("types")
    private ArrayList<TypeDetails> typeDetails;
    private ArrayList<Statistics> stats;

    public static class TypeDetails {

        private int slot;
        private PokemonApiResult.Pokemon type;

        public PokemonApiResult.Pokemon getType() {
            return type;
        }

        public void setType(PokemonApiResult.Pokemon type) {
            this.type = type;
        }
    }

    public static class Statistics {
        private int base_stat;
        private int effort;
        private PokemonApiResult.Pokemon stat;

        public int getBase_stat() {
            return base_stat;
        }

        public void setBase_stat(int base_stat) {
            this.base_stat = base_stat;
        }

        public PokemonApiResult.Pokemon getStat() {
            return stat;
        }

        public void setStat(PokemonApiResult.Pokemon stat) {
            this.stat = stat;
        }
    }

    public ArrayList<TypeDetails> getTypeDetails() {
        return typeDetails;
    }

    public void setTypeDetails(ArrayList<TypeDetails> typeDetails) {
        this.typeDetails = typeDetails;
    }

    public ArrayList<Statistics> getStats() {
        return stats;
    }
}
