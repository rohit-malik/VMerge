package com.example.nitinmalik.uploading_video;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

// "http://172.26.1.221/AndroidUploadImage/upload.php"
// "http://172.26.1.221/AndroidUploadImage/date_modified.php"

public class MainActivity extends AppCompatActivity  implements GoogleApiClient.OnConnectionFailedListener{

    Context context = this;
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu items for use in the action bar
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.settings, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle presses on the action bar items
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                openSettings();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    public void openSettings(){
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }

    String videopath;
    String date;
    String url = "http://172.26.1.221/AndroidUploadImage/upload.php";
    //String videourl = "http://172.26.1.221/AndroidUploadImage/uploads/videoplayback.mp4";
    //VideoView videoView;
    //ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button view_button = findViewById(R.id.button_view);

        Intent i = getIntent();
        if (i.getStringExtra("ip_address") != null) {
            url = i.getStringExtra("ip_address");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Log.d("Permission", "Permission not  granted");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
            Log.d("Requesting", "Requesting permission");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Log.d("Permission", "Permission not  granted");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
            Log.d("Requesting", "Requesting permission");
        }




        view_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EventListActivity.class);
                startActivity(intent);
            }
        });



        //google signin code

//        GoogleSignInOptions gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//        googleApiClient=new GoogleApiClient.Builder(this)
//                .enableAutoManage(this,this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
//                .build();
//
//
//
//        signInButton=(SignInButton)findViewById(R.id.sign_in_button);
//        signInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
//                startActivityForResult(intent,RC_SIGN_IN);
//            }
//        });



    }


    SignInButton signInButton;
    private GoogleApiClient googleApiClient;
    TextView textView;
    private static final int RC_SIGN_IN = 1;


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            Log.d("MainActivity results", result.getSignInAccount().getEmail());
            String email_id = result.getSignInAccount().getEmail();
            String name = result.getSignInAccount().getDisplayName();
            AddUserInfo(name, email_id);
            gotoProfile(name, email_id);
        }else{
            Toast.makeText(getApplicationContext(),"Sign in cancel",Toast.LENGTH_LONG).show();
        }
    }
    private void gotoProfile(String user_name, String email_id){
        Log.d("MainActivity", "before intett");
        Intent intent=new Intent(context,EventListActivity.class);
//        intent.putExtra("user_name", user_name);
//        intent.putExtra("email_id", email_id);
        SharedPreferences user_info = getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = user_info.edit();
        prefEditor.putString("user_name", user_name);
        prefEditor.putString("email_id", email_id);
        prefEditor.apply();
        Log.d("MainActivity", "before intett2");

        startActivity(intent);
        Log.d("MainActivity", "before intett3");

    }

    private void AddUserInfo(final String name, final String email_id){

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        com.android.volley.request.StringRequest stringrequest = new com.android.volley.request.StringRequest(Request.Method.POST,"http://172.26.1.221/AndroidUploadImage/AddUserInfo.php",new Response.Listener<String>(){
            @Override
            public void onResponse(String response){
                Log.d("Event",response);
                try {
                    Log.d("Json",response);
                    JSONObject jObj = new JSONObject(response);
                    String message = jObj.getString("message");

                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){

            }
        }){
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new java.util.HashMap<>();
                params.put("name", name);
                params.put("email_id", email_id);
                return params;
            }
        };

        requestQueue.add(stringrequest);

    }

}
