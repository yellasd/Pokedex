package com.example.pokedex;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.pokedex.adapter.PokemonAdapter;
import com.example.pokedex.database.FavouritesDatabase;
import com.example.pokedex.model.PokemonApiResult;

import java.util.ArrayList;
import java.util.Objects;

public class FavouritesActivity extends NavigationDrawerActivity implements PokemonAdapter.OnItemClickListener {

    private ArrayList<PokemonApiResult.Pokemon> pokemon;
    private PokemonAdapter pokemonAdapter;
    private FavouritesDatabase favouritesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = findViewById(R.id.layout);
        LayoutInflater.from(this).inflate(R.layout.activity_favourites, layout);

        pokemon = new ArrayList<>();
        favouritesDatabase = new FavouritesDatabase(this);


        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null)
            networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            Cursor results = favouritesDatabase.getData();
            if (results.getCount() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("NO DATA").show();
            } else {

                while (results.moveToNext()) {
                    PokemonApiResult.Pokemon poke = new PokemonApiResult.Pokemon();
                    poke.setName(results.getString(0));
                    poke.setUrl(results.getString(2));
                    pokemon.add(poke);
                }

                RecyclerView recyclerView = findViewById(R.id.recycler_favourites);
                pokemonAdapter=new PokemonAdapter(this,pokemon);

                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(pokemonAdapter);
                recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
                recyclerView.setHasFixedSize(true);

                pokemonAdapter.setOnItemClickListener(this);
            }
        }else {
            Toast.makeText(getApplicationContext(),"Check your internet connection!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(int position, ImageView imageView) {
        //shared element transition
        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(FavouritesActivity.this, imageView, Objects.requireNonNull(ViewCompat.getTransitionName(imageView)));
        Intent detail = new Intent(this, DetailActivity.class);
        detail.putExtra("POKEID", pokemon.get(position).getNumber());
        detail.putExtra("NAME", pokemon.get(position).getName());
        startActivity(detail, compat.toBundle());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, PokemonActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
