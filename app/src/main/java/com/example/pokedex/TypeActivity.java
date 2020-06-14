package com.example.pokedex;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokedex.adapter.TypeAndLocAdapter;
import com.example.pokedex.model.PokemonApiResult;
import com.example.pokedex.webservice.PokemonService;
import com.example.pokedex.webservice.RetrofitClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TypeActivity extends NavigationDrawerActivity implements TypeAndLocAdapter.OnItemClickListener {

    private ArrayList<PokemonApiResult.Pokemon> typesArrayList;
    private TypeAndLocAdapter typeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = findViewById(R.id.layout);
        LayoutInflater.from(this).inflate(R.layout.activity_type, layout);

        typesArrayList = new ArrayList<>();
        typeAdapter = new TypeAndLocAdapter(this, typesArrayList);

        RecyclerView recyclerView = findViewById(R.id.recycler_types);

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
            Call<PokemonApiResult> call = service.getType();
            call.enqueue(new Callback<PokemonApiResult>() {
                @Override
                public void onResponse(Call<PokemonApiResult> call, Response<PokemonApiResult> response) {
                    PokemonApiResult pokedex = response.body();
                    assert pokedex != null;
                    ArrayList<PokemonApiResult.Pokemon> tempList = pokedex.getResults();
                    typesArrayList.addAll(tempList);
                    typeAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<PokemonApiResult> call, Throwable t) {
                    Log.e("Retrofit response msg", "failed");
                }
            });
        }else {
            Toast.makeText(this, "Check your internet connection!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(int position) {
        if (position != 19 && position != 18) {
            Intent intent = new Intent(this, AllTypesActivity.class);
            intent.putExtra("ID", position + 1);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }else {
            Toast.makeText(getApplicationContext(),"Data not found",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(this,PokemonActivity.class);
        startActivity(intent);
        overridePendingTransition(0,0);
    }
}
