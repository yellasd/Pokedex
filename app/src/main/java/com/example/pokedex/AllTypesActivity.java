package com.example.pokedex;

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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokedex.adapter.PokemonAdapter;
import com.example.pokedex.model.PokemonApiResult;
import com.example.pokedex.model.PokemonOfAType;
import com.example.pokedex.webservice.PokemonService;
import com.example.pokedex.webservice.RetrofitClient;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllTypesActivity extends NavigationDrawerActivity implements PokemonAdapter.OnItemClickListener {

    private PokemonAdapter pokemonAdapter;
    private ArrayList<PokemonApiResult.Pokemon> typePokemons;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = findViewById(R.id.layout);
        LayoutInflater.from(this).inflate(R.layout.activity_all_types, layout);

        intent = getIntent();
        int id = intent.getIntExtra("ID", 1);

        typePokemons = new ArrayList<>();


        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null)
            networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            PokemonService service = RetrofitClient.getClient().create(PokemonService.class);
            Call<PokemonOfAType> call = service.getPokemonOfAType(Integer.toString(id));
            call.enqueue(new Callback<PokemonOfAType>() {
                @Override
                public void onResponse(Call<PokemonOfAType> call, Response<PokemonOfAType> response) {
                    PokemonOfAType pokedex = response.body();
                    assert pokedex != null;
                    ArrayList<PokemonOfAType.Pokemon> tempList = pokedex.getPokemon();
                    for(int i=0;i<tempList.size();i++){
                        typePokemons.add(tempList.get(i).getPokemon());
                    }
                    pokemonAdapter = new PokemonAdapter(getApplicationContext(), typePokemons);
                    pokemonAdapter.setOnItemClickListener(AllTypesActivity.this);

                    RecyclerView recyclerView = findViewById(R.id.recycler_all_types);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(pokemonAdapter);
                    recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                    recyclerView.setHasFixedSize(true);
                }

                @Override
                public void onFailure(Call<PokemonOfAType> call, Throwable t) {
                    Log.e("Retrofit response msg", "failed");
                }
            });
        }else {
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

        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(AllTypesActivity.this, imageView, Objects.requireNonNull(ViewCompat.getTransitionName(imageView)));
        Intent detail = new Intent(this, DetailActivity.class);
        detail.putExtra("POKEID", typePokemons.get(position).getNumber());
        detail.putExtra("NAME", typePokemons.get(position).getName());
        startActivity(detail, compat.toBundle());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(this,TypeActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
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

        }
    };
}
