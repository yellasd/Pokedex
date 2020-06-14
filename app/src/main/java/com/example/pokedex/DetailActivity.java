package com.example.pokedex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pokedex.model.Evolution;
import com.example.pokedex.model.PokemonApiResult;
import com.example.pokedex.model.PokemonDetails;
import com.example.pokedex.webservice.PokemonService;
import com.example.pokedex.webservice.RetrofitClient;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailActivity extends NavigationDrawerActivity {

    TextView type1, type2, region, hp, attack, defense, spattack, spdefense, speed, nextevol1, nextevol2, prevevol1, prevevol2;
    ImageView imageView;
    int id;
    String k;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = findViewById(R.id.layout);
        LayoutInflater.from(this).inflate(R.layout.activity_detail, layout);

        TextView textView = findViewById(R.id.pokename);

        Intent intent = getIntent();
        id = intent.getIntExtra("POKEID", 1);

        k = intent.getStringExtra("NAME");
        assert k != null;
        k = Character.toUpperCase(k.charAt(0)) + k.substring(1);
        textView.setText(k);

        type1 = findViewById(R.id.type1);
        type2 = findViewById(R.id.type2);
        region = findViewById(R.id.region);
        hp = findViewById(R.id.hp);
        attack = findViewById(R.id.attack);
        defense = findViewById(R.id.defense);
        spattack = findViewById(R.id.special_attack);
        spdefense = findViewById(R.id.special_defense);
        speed = findViewById(R.id.speed);
        imageView = findViewById(R.id.pokemonImage);
        nextevol1 = findViewById(R.id.postevolution1);
        nextevol2 = findViewById(R.id.postevolution2);
        prevevol1 = findViewById(R.id.preevolution1);
        prevevol2 = findViewById(R.id.preevolution2);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null)
            networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            if (id <= 650)
                Picasso.with(getApplicationContext())
                        .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/back/" + id + ".png")
                        .into(imageView);
            else
                Picasso.with(getApplicationContext())
                        .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + id + ".png")
                        .into(imageView);

            PokemonService service = RetrofitClient.getClient().create(PokemonService.class);
            Call<PokemonDetails> detailsCall = service.getPokemonDetails(Integer.toString(id));
            detailsCall.enqueue(new Callback<PokemonDetails>() {
                @Override
                public void onResponse(Call<PokemonDetails> call, Response<PokemonDetails> response) {
                    PokemonDetails details = response.body();
                    assert details != null;
                    type1.setText(details.getTypeDetails().get(0).getType().getName());
                    if (details.getTypeDetails().size() != 1)
                        type2.setText(details.getTypeDetails().get(1).getType().getName());
                    else type2.setText("");

                    ArrayList<PokemonDetails.Statistics> statistics = details.getStats();
                    String s;
                    PokemonDetails.Statistics st;

                    if (statistics.size() >= 1) {
                        st = statistics.get(0);
                        s = "HP - " + st.getBase_stat();
                        hp.setText(s);
                    } else hp.setText("-");

                    if (statistics.size() >= 2) {
                        st = statistics.get(1);
                        s = "Attack - " + st.getBase_stat();
                        attack.setText(s);
                    } else attack.setText("-");

                    if (statistics.size() >= 3) {
                        st = statistics.get(2);
                        s = "Defense - " + st.getBase_stat();
                        defense.setText(s);
                    } else defense.setText("-");

                    if (statistics.size() >= 4) {
                        st = statistics.get(3);
                        s = "Sp.Attack - " + st.getBase_stat();
                        spattack.setText(s);
                    } else spattack.setText("-");

                    if (statistics.size() >= 5) {
                        st = statistics.get(4);
                        s = "Sp.Defense - " + st.getBase_stat();
                        spdefense.setText(s);
                    } else spdefense.setText("-");

                    if (statistics.size() >= 6) {
                        st = statistics.get(5);
                        s = "Speed - " + st.getBase_stat();
                        speed.setText(s);
                    } else defense.setText("-");


                    if (id >= 0 && id <= 151)
                        region.setText("Kanto");

                    if (id >= 152 && id <= 251)
                        region.setText("Johto");

                    if (id >= 252 && id <= 386)
                        region.setText("Hoenn");

                    if (id >= 387 && id <= 493)
                        region.setText("Sinnoh");

                    if (id >= 494 && id <= 649)
                        region.setText("Unova");

                    if (id >= 650 && id <= 721)
                        region.setText("Kalos");

                    if (id >= 722 && id <= 802)
                        region.setText("Alola");
                }

                @Override
                public void onFailure(Call<PokemonDetails> call, Throwable t) {
                    Log.e("DetailActivity", "FAILED");
                }
            });

            if (id < 151) {
                PokemonService pokemonService = new Retrofit.Builder()
                        .baseUrl("https://raw.githubusercontent.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build().create(PokemonService.class);
                Call<Evolution> call = pokemonService.getEvolution();
                call.enqueue(new Callback<Evolution>() {
                    @Override
                    public void onResponse(Call<Evolution> call, Response<Evolution> response) {
                        Evolution evolution = response.body();
                        assert evolution != null;
                        Evolution.TempClass tempClass = evolution.getPokemon().get(id);
                        ArrayList<PokemonApiResult.Pokemon> nextevol = tempClass.getNext_evolution();
                        if (nextevol != null) {
                            if (nextevol.size() == 1)
                                nextevol1.setText(nextevol.get(0).getName());
                            if (nextevol.size() == 2) {
                                nextevol1.setText(nextevol.get(0).getName());
                                nextevol2.setText(nextevol.get(1).getName());
                            }
                        }
                        ArrayList<PokemonApiResult.Pokemon> preevol = tempClass.getPrev_evolution();
                        if (preevol != null) {
                            if (preevol.size() == 1)
                                prevevol1.setText(preevol.get(0).getName());
                            if (preevol.size() == 2) {
                                prevevol1.setText(preevol.get(0).getName());
                                prevevol2.setText(preevol.get(1).getName());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Evolution> call, Throwable t) {

                    }
                });
            }

        } else {
            Toast.makeText(this, "Check your internet connection!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, PokemonActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void shareWithOtherApps(View view) throws IOException {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        LinearLayout rootView = findViewById(R.id.container);
        Bitmap bitmap = getScreenShot(rootView);
        File imgPath = store(bitmap);
        shareImage(imgPath);
    }

    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        screenView.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public File store(Bitmap bm) throws IOException {

        String root = Environment.getExternalStorageDirectory().toString();
        File dir = new File(root + "/pokedex/");
        if (!dir.exists()) {
            dir.mkdir();
        }
        String filename = k + ".jpg";
        File imagePath = new File( dir +"/"+ filename);
        imagePath.createNewFile();
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(imagePath);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imagePath;
    }

    private void shareImage(File imagePath) {
        Uri uri = FileProvider.getUriForFile(DetailActivity.this, BuildConfig.APPLICATION_ID + ".provider", imagePath);
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.setType("image/jpeg");
        String body = "I'm loving " + k + "! Do check it out";
        share.putExtra(Intent.EXTRA_TEXT, body);
        share.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(share, "Share via"));
    }
}
