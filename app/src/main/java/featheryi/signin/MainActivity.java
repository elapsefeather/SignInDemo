package featheryi.signin;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    CallbackManager callbackManager;

    LoginButton facebook;
    TextView text;
    Button logout, delete;
    String login_pass = "", google_login = "", facebook_login = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login_pass = getIntent().getExtras().getString("login_pass");

        mAuth = FirebaseAuth.getInstance();

        callbackManager = CallbackManager.Factory.create();

        init();
        getUser();
    }

    public void init() {
        google_login = getResources().getString(R.string.google_login);
        facebook_login = getResources().getString(R.string.fb_login);

        text = (TextView) findViewById(R.id.text);

        logout = (Button) findViewById(R.id.btn_logout);
        delete = (Button) findViewById(R.id.btn_delete);
        facebook = (LoginButton) findViewById(R.id.main_btn_facebook);

        if (login_pass.equals(google_login)) {

            logout.setOnClickListener(this);
            delete.setOnClickListener(this);

            logout.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            facebook.setVisibility(View.GONE);
        } else if (login_pass.equals(facebook_login)) {

            facebook.setOnClickListener(this);

            logout.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            facebook.setVisibility(View.VISIBLE);
        }
    }

    public void getUser() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();

            text.setText("name = " + name +
                    "\nemail = " + email +
                    "\nuid = " + uid);
        }
    }

    public void disconnectFromFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                LoginManager.getInstance().logOut();

            }
        }).executeAsync();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_logout:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                toLogin();
                            }
                        });
                break;

            case R.id.btn_delete:
                AuthUI.getInstance()
                        .delete(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                toLogin();
                            }
                        });
                break;
            case R.id.main_btn_facebook:
                LoginManager.getInstance().logOut();
                toLogin();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void toLogin() {
        Intent logout = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(logout);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
