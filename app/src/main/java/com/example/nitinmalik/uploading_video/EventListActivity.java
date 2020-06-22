package com.example.nitinmalik.uploading_video;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EventListActivity extends AppCompatActivity {

    //this is the JSON Data URL
    //make sure you are using the correct ip else it will not work
    private static final String URL_EVENTS = "http://172.26.1.221/AndroidUploadImage/mysql_db.php";
    private static final String URL_USER_TYPE = "http://172.26.1.221/AndroidUploadImage/get_user_type.php?email_id=%s";

    //a list to store all the products
    List<VideoEvent> eventList;
    String uri;
    String user_type;

    //the recyclerview
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    CustomAdapter adapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.search);

        if (searchItem != null) {
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    //some operation
                    Log.d("Closing SearchView","---------------------------------------------");
                    Toast.makeText(getApplicationContext(), "Toasted", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //some operation
                }
            });

            MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    //Toast.makeText(ScrollingActivity.this, "onMenuItemActionExpand called", Toast.LENGTH_SHORT).show();
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    //Toast.makeText(getApplicationContext(), "Toasted", Toast.LENGTH_SHORT).show();
                    adapter.updateList(eventList);
                    return true;
                }
            });

            EditText searchPlate = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchPlate.setHint("Search");
            View searchPlateView = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
            searchPlateView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            // use this method for search process
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // use this method when query submitted
                    //Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
                    filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // use this method for auto complete search process
                    return false;
                }
            });
            SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        }
        return super.onCreateOptionsMenu(menu);

        //return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        Button add_button = findViewById(R.id.button_add_event);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // set a LinearLayoutManager with default vertical orientation
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setHasFixedSize(true);
        eventList = new ArrayList<>();
        Log.d("ONCREATE","IN ONCREATE");
        loadEvents();

        SharedPreferences user_info = getSharedPreferences("user_info", MODE_PRIVATE);
        final String email_id = user_info.getString("email_id", "None");
        uri = String.format(URL_USER_TYPE, email_id);
        //getUserType();

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (user_type.equals("admin")){
//                    Intent intent = new Intent(EventListActivity.this, AddEventActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//
//                else {
//                    Toast.makeText(EventListActivity.this, "Only for Admins", Toast.LENGTH_LONG).show();
//                }
                Intent intent = new Intent(EventListActivity.this, AddEventActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    /*
    @Override
    protected void onRestart() {
        super.onRestart();
        loadEvents();
    }
    */
    private void loadEvents() {

        /*
         * Creating a String Request
         * The request type is GET defined by first parameter
         * The URL is defined in the second parameter
         * Then we have a Response Listener and a Error Listener
         * In response listener we will get the JSON response as a String
         * */
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.getCache().clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_EVENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);
                            Log.d("ARRAY_LENGHT",String.valueOf(array.length()));
                            //traversing through all the object
                            for (int i = 0; i < array.length(); i++) {

                                //getting product object from json array
                                JSONObject event = array.getJSONObject(i);
                                Log.d("DATA",String.valueOf(event.getInt("event_ID")));
                                //adding the product to product list
                                eventList.add(new VideoEvent(
                                        event.getInt("event_ID"),
                                        event.getString("event_name"),
                                        event.getString("event_info")
                                ));
                            }

                            //creating adapter object and setting it to recyclerview
                            adapter = new CustomAdapter(EventListActivity.this, eventList);
                            recyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //adding our stringrequest to queue
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }

    void filter(String text){
        List<VideoEvent> temp = new ArrayList();
        for(VideoEvent d: eventList){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if((d.getEvent_name().toLowerCase()).contains(text.toLowerCase()) || (d.getEvent_info().toLowerCase()).contains(text.toLowerCase())){
                temp.add(d);
            }
        }
        //update recyclerview
        adapter.updateList(temp);
    }

    private void getUserType() {

        /*
         * Creating a String Request
         * The request type is GET defined by first parameter
         * The URL is defined in the second parameter
         * Then we have a Response Listener and a Error Listener
         * In response listener we will get the JSON response as a String
         * */
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.getCache().clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //converting the string to json array object
                            JSONArray array = new JSONArray(response);
                            Log.d("ARRAY_LENGHT",String.valueOf(array.length()));
                            //traversing through all the object

                                //getting product object from json array
                                user_type = array.getJSONObject(0).getString("user_type");
//                                Log.d("DATA",String.valueOf(video.getInt("user_type")));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //adding our stringrequest to queue
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);
    }

}
