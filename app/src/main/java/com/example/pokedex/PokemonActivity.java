package com.example.pokedex;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokedex.adapter.PokemonAdapter;
import com.example.pokedex.database.FavouritesDatabase;
import com.example.pokedex.model.PokemonApiResult;
import com.example.pokedex.webservice.PokemonLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class PokemonActivity extends NavigationDrawerActivity implements LoaderManager.LoaderCallbacks<ArrayList<PokemonApiResult.Pokemon>>, PokemonAdapter.OnItemClickListener {

    private ArrayList<PokemonApiResult.Pokemon> pokemon;
    private PokemonAdapter pokemonAdapter;
    FavouritesDatabase favouritesDatabase;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = findViewById(R.id.layout);
        LayoutInflater.from(this).inflate(R.layout.activity_pokemon, layout);
        favouritesDatabase = new FavouritesDatabase(this);

        pokemon = new ArrayList<>();


        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null)
            networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            if (getLoaderManager().getLoader(0) == null)
                getLoaderManager().initLoader(0, null, this);
        } else {
            Toast.makeText(this, "Check your internet connection!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<ArrayList<PokemonApiResult.Pokemon>> onCreateLoader(int id, Bundle args) {
        return new PokemonLoader(getApplicationContext());
    }

    @Override
    public void onLoadFinished(android.content.Loader<ArrayList<PokemonApiResult.Pokemon>> loader, ArrayList<PokemonApiResult.Pokemon> data) {
        //if (data != null && pokemon.size() == 0 && pokemonAdapter.getItemCount()==0)
        pokemon.addAll(data);

        pokemonAdapter = new PokemonAdapter(this, pokemon);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(pokemonAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);
        pokemonAdapter.setOnItemClickListener(PokemonActivity.this);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onLoaderReset(android.content.Loader<ArrayList<PokemonApiResult.Pokemon>> loader) {
        int size = pokemon.size();
        pokemon.clear();
        pokemonAdapter.notifyItemRangeChanged(0, size);
    }

    @Override
    public void onItemClick(int position, ImageView imageView) {

        //shared element transition
        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(PokemonActivity.this, imageView, Objects.requireNonNull(ViewCompat.getTransitionName(imageView)));
        Intent detail = new Intent(this, DetailActivity.class);
        detail.putExtra("POKEID", pokemon.get(position).getNumber());
        detail.putExtra("NAME", pokemon.get(position).getName());
        startActivity(detail, compat.toBundle());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finishAffinity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        android.widget.SearchView searchView = (android.widget.SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pokemonAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();

            boolean isInserted = favouritesDatabase.insertData(pokemon.get(position).getName(), pokemon.get(position).getNumber(), pokemon.get(position).getUrl());
            if (isInserted)
                Toast.makeText(getApplicationContext(), "Added to favourites!", Toast.LENGTH_SHORT).show();

            Intent pokemon = new Intent(getApplicationContext(), PokemonActivity.class);
            startActivity(pokemon);
            overridePendingTransition(0, 0);
            finish();
        }
    };
}
