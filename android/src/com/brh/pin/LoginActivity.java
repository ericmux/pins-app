package com.brh.pin;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import com.brh.pin.api.APIHandler;

public class LoginActivity extends Activity {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
    }

    public void register(View view) {
        login(view);
    }

    public void login(View view) {
        sharedpreferences = this.getSharedPreferences("MyPref", 0);
        editor = sharedpreferences.edit();
        user = new User((((EditText)findViewById(R.id.editUsername)).getText().toString()),
                ((EditText)findViewById(R.id.editPassword)).getText().toString());
        APIHandler apiHandler = new APIHandler();
        apiHandler.signUp(user);

       // SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("username", user.getUsername());
        editor.putString("password", user.getPassword());
        editor.commit();

        Intent intent = new Intent(this, FeedActivity.class);
//        intent.putExtra("user_object", user);
        startActivity(intent);
        finish();
    }

    protected void onResume() {
        sharedpreferences = this.getSharedPreferences("MyPref", 0);
        if (sharedpreferences.contains("username")) {
            if(sharedpreferences.contains("password")){
                Intent intent = new Intent(this, FeedActivity.class);
//                intent.putExtra("user_object", user);
                startActivity(intent);
                finish();
            }
        }

        super.onResume();
    }
}
