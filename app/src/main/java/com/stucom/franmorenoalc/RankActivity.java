package com.stucom.franmorenoalc;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.stucom.franmorenoalc.model.Player;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

public class RankActivity extends AppCompatActivity {

    private String token;
    TextView textView;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        //we load token and mail stored in SharedPreferences
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        //token = prefs.getString("token", null);
        token = "3c488b7ff21eacf4b5954275160fe5933f2aa36a6aa0cf31dbfe295e2063edb6be94d27e1b499f3b0f137e258d6594920fd512d2ed08df54b08d8258853559ac";
        //Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
        textView = findViewById(R.id.hintRank);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadRanks();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        downloadRanks();
    }

    public void downloadRanks() {
        String URL = "https://api.flx.cat/dam2game/ranking?token="+ token;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Toast.makeText(getApplicationContext(), "onResponse", Toast.LENGTH_SHORT).show();
                        String json = response.toString();
                        Gson gson = new Gson();
                        Type typeToken = new TypeToken<APIResponse<List<Player>>>() {}.getType();
                        APIResponse<List<Player>> apiResponse = gson.fromJson(json, typeToken);
                            List<Player> players = apiResponse.getData();
                            PlayersAdapter adapter = new PlayersAdapter(players);
                            recyclerView.setAdapter(adapter);
                            Toast.makeText(getApplicationContext(), players.get(6).getName() , Toast.LENGTH_SHORT).show();
                        //textView.setText(message);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
              @Override public void onErrorResponse(VolleyError error) {
                String message = error.toString();
                NetworkResponse response = error.networkResponse;
                if (response != null) {
                    message = response.statusCode + " " + message;
                }
                  textView.setText("ERROR " + message);
                  swipeRefreshLayout.setRefreshing(false);

              }

        });

        MyVolley.getInstance(this).add(request);

    }

    class PlayersViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        TextView textViewDwarf;
        ImageView imageView;

        PlayersViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewDwarf = itemView.findViewById(R.id.textViewDwarf);
        }
    }

    class PlayersAdapter extends RecyclerView.Adapter<PlayersViewHolder> {

        private List<Player> players;

        PlayersAdapter(List<Player> players) {
            super();
            this.players = players;
        }

        @NonNull @Override
        public PlayersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.list_item, parent, false);
            return new PlayersViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull PlayersViewHolder viewHolder, int position) {
            Player player = players.get(position);
            viewHolder.textView.setText(player.getName());
            /*String dwarf = player.isDwarf() ? "Nan" : "Normal";
            viewHolder.textViewDwarf.setText(dwarf); */
            Picasso.get().load(player.getImage()).into(viewHolder.imageView);
        }
        @Override
        public int getItemCount() {
            return players.size();
        }
    }
}
