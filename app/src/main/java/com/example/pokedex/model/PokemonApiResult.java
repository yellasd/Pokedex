package com.example.pokedex.model;

import java.util.ArrayList;

public class PokemonApiResult {

    private ArrayList<Pokemon> results;
    private String next;

    public ArrayList<Pokemon> getResults() {
        return results;
    }

    public void setResults(ArrayList<Pokemon> results) {
        this.results = results;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public static class Pokemon {

        private int number;
        private String url,name;

        public int getNumber() {
            String[] parts=url.split("/");
            return Integer.parseInt(parts[parts.length-1]);
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


}
