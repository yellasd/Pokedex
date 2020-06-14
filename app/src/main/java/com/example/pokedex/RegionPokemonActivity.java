package com.example.pokedex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.pokedex.adapter.PokemonAdapter;
import com.example.pokedex.adapter.TypeAndLocAdapter;
import com.example.pokedex.model.PokemonApiResult;
import com.example.pokedex.model.PokemonOfAType;
import com.example.pokedex.webservice.PokemonService;
import com.example.pokedex.webservice.RetrofitClient;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegionPokemonActivity extends NavigationDrawerActivity implements PokemonAdapter.OnItemClickListener {

    private ArrayList<PokemonApiResult.Pokemon> regionPokemon;
    private PokemonAdapter pokemonAdapter;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = findViewById(R.id.layout);
        LayoutInflater.from(this).inflate(R.layout.activity_all_types, layout);

        intent = getIntent();
        int position = intent.getIntExtra("ID", 0);

        regionPokemon = new ArrayList<>();

        int limit = 0;
        int offset = 0;
        switch (position + 1) {
            case 1:
                getSupportActionBar().setTitle("Kanto");
                limit = 151;
                offset = 0;
                break;
            case 2:
                getSupportActionBar().setTitle("Johto");
                limit = 100;
                offset = 151;
                break;
            case 3:
                getSupportActionBar().setTitle("Hoenn");
                limit = 135;
                offset = 251;
                break;
            case 4:
                getSupportActionBar().setTitle("Sinnoh");
                limit = 107;
                offset = 386;
                break;
            case 5:
                getSupportActionBar().setTitle("Unova");
                limit = 156;
                offset = 493;
                break;
            case 6:
                getSupportActionBar().setTitle("Kalos");
                limit = 72;
                offset = 649;
                break;
            case 7:
                getSupportActionBar().setTitle("Alola");
                limit = 81;
                offset = 801;
                break;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null)
            networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            PokemonService service = RetrofitClient.getClient().create(PokemonService.class);
            Call<PokemonApiResult> call = service.getPokemon(limit, offset);
            call.enqueue(new Callback<PokemonApiResult>() {
                @Override
                public void onResponse(Call<PokemonApiResult> call, Response<PokemonApiResult> response) {
                    PokemonApiResult pokedex = response.body();
                    assert pokedex != null;
                    ArrayList<PokemonApiResult.Pokemon> tempList = pokedex.getResults();
                    regionPokemon.addAll(tempList);

                    RecyclerView recyclerView = findViewById(R.id.recycler_all_types);
                    pokemonAdapter = new PokemonAdapter(getApplicationContext(), regionPokemon);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(pokemonAdapter);
                    recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                    recyclerView.setHasFixedSize(true);

                    pokemonAdapter.setOnItemClickListener(RegionPokemonActivity.this);
                }

                @Override
                public void onFailure(Call<PokemonApiResult> call, Throwable t) {
                    Log.e("Retrofit response msg", "failed");
                }
            });
        } else {
            Toast.makeText(this, "Check your internet connection!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(int position,ImageView imageView) {

        //shared element transition
        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(RegionPokemonActivity.this, imageView, Objects.requireNonNull(ViewCompat.getTransitionName(imageView)));
        Intent detail = new Intent(this, DetailActivity.class);
        detail.putExtra("POKEID", regionPokemon.get(position).getNumber());
        detail.putExtra("NAME", regionPokemon.get(position).getName());
        startActivity(detail, compat.toBundle());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, RegionActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
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

    ItemTouchHelper.SimpleCallback simpleCallback=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Toast.makeText(getApplicationContext(),"Added to favourites",Toast.LENGTH_SHORT).show();
        }
    };
}