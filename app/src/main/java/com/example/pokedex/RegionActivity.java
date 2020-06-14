package com.example.pokedex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.pokedex.adapter.TypeAndLocAdapter;
import com.example.pokedex.model.PokemonApiResult;
import com.example.pokedex.webservice.PokemonService;
import com.example.pokedex.webservice.RetrofitClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegionActivity extends NavigationDrawerActivity implements TypeAndLocAdapter.OnItemClickListener {

    private ArrayList<PokemonApiResult.Pokemon> regionsArrayList;
    private TypeAndLocAdapter typeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = findViewById(R.id.layout);
        LayoutInflater.from(this).inflate(R.layout.activity_region, layout);

        regionsArrayList = new ArrayList<>();
        typeAdapter = new TypeAndLocAdapter(this, regionsArrayList);

        RecyclerView recyclerView = findViewById(R.id.recycler_regions);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(typeAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);

        typeAdapter.setOnItemClickListener(this);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null)
            networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            PokemonService service = RetrofitClient.getClient().create(PokemonService.class);
            Call<PokemonApiResult> call = service.getLocation();
            call.enqueue(new Callback<PokemonApiResult>() {
                @Override
                public void onResponse(Call<PokemonApiResult> call, Response<PokemonApiResult> response) {
                    PokemonApiResult pokedex = response.body();
                    assert pokedex != null;
                    ArrayList<PokemonApiResult.Pokemon> tempList = pokedex.getResults();
                    regionsArrayList.addAll(tempList);
                    typeAdapter.notifyDataSetChanged();
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
    public void onItemClick(int position) {
        Intent p = new Intent(this, RegionPokemonActivity.class);
        p.putExtra("ID", position);
        startActivity(p);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, PokemonActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
