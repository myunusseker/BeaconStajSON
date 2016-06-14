package com.ingenious.fellas.beaconstaj.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ingenious.fellas.beaconstaj.Classes.Globals;
import com.ingenious.fellas.beaconstaj.Classes.RequestHandler;
import com.ingenious.fellas.beaconstaj.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Signup extends AppCompatActivity {

    private Button button ;
    private EditText username;
    private EditText name;
    private EditText password;
    private EditText passwordConfirm;
    private EditText mail;
    CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        button = (Button) findViewById(R.id.signup_button);
        username = (EditText) findViewById(R.id.usernameSignup);
        password = (EditText) findViewById(R.id.passwordSignup);
        name = (EditText) findViewById(R.id.nameSignup);
        passwordConfirm = (EditText) findViewById(R.id.passwordConfirm);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayoutSignup);
        mail = (EditText) findViewById(R.id.mail);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String errormsg = "";
                View view = Signup.this.getCurrentFocus();
                if(view!=null)
                {
                    InputMethodManager mng = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mng.hideSoftInputFromWindow(view.getWindowToken(),0);
                }
                if(name.getText().toString().length() == 0)
                {
                    errormsg +="Lutfen Ad Soyad giriniz\n";
                }
                if(username.getText().toString().length() < 6)
                {
                    errormsg += "Kullanıcı adı en az 6 karakter olmalı.\n";
                }
                if(password.getText().toString().length() < 6)
                {
                    errormsg += "Şifre en az 6 karakter olmalı.\n";
                }
                if(!password.getText().toString().equals(passwordConfirm.getText().toString()))
                {
                    errormsg += "Şifreler aynı değil. Lütfen tekrar giriniz.\n";
                }
                if(errormsg.equals(""))
                    new signupAsync().execute(username.getText().toString(),password.getText().toString(),
                            name.getText().toString(),mail.getText().toString());
                else
                    Snackbar.make(coordinatorLayout,errormsg,Snackbar.LENGTH_LONG)
                            .show();
            }
        });
    }

    public class signupAsync extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            String url = Globals.URL + "signup.php";
            HashMap<String, String> h = new HashMap<>();
            h.put("username", params[0]);
            h.put("password",params[1]);
            h.put("name_surname",params[2]);
            h.put("email",params[3]);

            JSONObject jsonData = RequestHandler.sendPostRequest(url, h);

            return jsonData;
        }

        @Override
        protected void onPostExecute(JSONObject s) {

            try {
                int status = s.getInt("status");
                Log.i(Globals.TAG, "signup status -> " + String.valueOf(status));
                if(status == 200)
                {
                    JSONObject data = (JSONObject) s.get("data");

                    SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("username", data.getString("username"));
                    editor.putString("password", data.getString("password"));
                    editor.putString("email", data.getString("email"));
                    editor.putString("namesurname", data.getString("name_surname"));
                    editor.putInt("id", data.getInt("user_id"));
                    editor.commit();

                    Toast.makeText(Signup.this,"Welcome " + data.getString("name_surname"),Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Signup.this , MainActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Snackbar snackbar = Snackbar.make(coordinatorLayout,"Kayit Basarisiz.",Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
