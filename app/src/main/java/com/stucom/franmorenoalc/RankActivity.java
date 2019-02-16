package com.stucom.franmorenoalc;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.stucom.franmorenoalc.model.Player;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankActivity extends AppCompatActivity {

    private String token;
    TextView textView;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private List<Player> players;
    private List<Player> players2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        //we load token and mail stored in SharedPreferences
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        token = prefs.getString("token", null);
        Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
        textView = findViewById(R.id.textView);
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

    /**
     * Gets all info from players stored at the API
     */
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
                            players = apiResponse.getData();
                            //a list without usernames "User", to clean garbage
                            players2 = new ArrayList<>();
                            for(Player p: players){
                                if(!p.getName().equals("User")){
                                    players2.add(p);
                                }
                            }
                            Collections.sort(players2);
                            PlayersAdapter adapter = new PlayersAdapter(players2);
                            adapter.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v){
                                    Player playerSelect = players2.get(recyclerView.getChildAdapterPosition(v));
                                    playerSelectedDialog(playerSelect);
                                    //Toast.makeText(getApplicationContext(), "seleccion: "+ players2.get(recyclerView.getChildAdapterPosition(view)).getName(),Toast.LENGTH_SHORT).show();
                                }
                            });
                            recyclerView.setAdapter(adapter);
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
        TextView textViewPoints;
        ImageView imageView;


        PlayersViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewPoints = itemView.findViewById(R.id.textViewPoints);
        }
    }

    class PlayersAdapter extends RecyclerView.Adapter<PlayersViewHolder> implements View.OnClickListener {

        private List<Player> players;
        private View.OnClickListener listener;

        PlayersAdapter(List<Player> players) {
            super();
            this.players = players;
        }

        @NonNull @Override
        public PlayersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.list_item, parent, false);
            view.setOnClickListener(this);
            return new PlayersViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull PlayersViewHolder viewHolder, int position) {
            Player player = players.get(position);
            viewHolder.textView.setText(player.getName());
            viewHolder.textViewPoints.setText(player.getTotalScore());
            Picasso.get().load(player.getImage()).into(viewHolder.imageView);
        }

        //OnClickListener methods
        @Override
        public int getItemCount() {
            return players.size();
        }



        public void setOnClickListener(View.OnClickListener listener){
            this.listener = listener;
        }
        @Override
        public void onClick(View v) {
            if(listener != null){
                listener.onClick(v);
            }
        }
    }


    public void playerSelectedDialog(final Player player){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams") View mView = getLayoutInflater().inflate(R.layout.player_detail, null);
        //elements in dialog
        TextView userName = mView.findViewById(R.id.playerUserName);
        ImageView userImg = mView.findViewById(R.id.playerIMG);
        final EditText userText = mView.findViewById(R.id.playerMSG);
        Button userTextSend = mView.findViewById(R.id.playerSEND);

        userName.setText(player.getName());
        Picasso.get().load(player.getImage()).into(userImg);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        userTextSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = userText.getText().toString();
                if(text.trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Write something...",Toast.LENGTH_SHORT).show();
                }
                else{
                    sendMessageToUser(player.getId(), text);
                    dialog.cancel();
                    //Toast.makeText(getApplicationContext(), "sent",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    /**
     * Send message to another player via Volley
     * @param id String
     * @param text String
     */
    public void sendMessageToUser(String id, final String text) {

        String URL = "https://api.flx.cat/dam2game/message/"+ id;

        StringRequest request = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
            @Override public void onResponse(String response) {
                String json = response.toString();
                Gson gson = new Gson();
                Type typeToken = new TypeToken<APIResponse>() {}.getType();
                APIResponse apiResponse = gson.fromJson(json, typeToken);
                if(apiResponse.getErrorCode() == 0){
                    Toast.makeText(getApplicationContext(),"Message sent",Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override public void onErrorResponse(VolleyError error) {
                String message = error.toString();
                NetworkResponse response = error.networkResponse;
                if (response != null) {
                    message = response.statusCode + " " + message;
                }
            }

        }) {
            @Override protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                params.put("text", text);
                return params;
            }
        };

        MyVolley.getInstance(this).add(request);

    }

}
