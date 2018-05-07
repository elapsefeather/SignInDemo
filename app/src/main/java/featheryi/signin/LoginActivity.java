package featheryi.signin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    String TAG = "LoginActivity";
    private FirebaseAuth mAuth;

    com.google.android.gms.common.SignInButton google;
    Button login, create;
    EditText ed_email, ed_password;

    private static final int RC_SIGN_IN = 123;
    String email = "", password = "";

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());

//            new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
//            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
//            new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        init();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_google:

                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        RC_SIGN_IN);
                break;
            case R.id.btn_login:
                email = ed_email.getText().toString().trim();
                password = ed_password.getText().toString().trim();

                if (email.equals("")) {
//                    未輸入帳號
                    Toast.makeText(this, R.string.email_null, Toast.LENGTH_LONG).show();
                } else if (password.equals("")) {
//                    未輸入密碼
                    Toast.makeText(this, R.string.password_null, Toast.LENGTH_LONG).show();
                } else if (password.length() < 6 || password.length() > 20) {
//                   密碼不為6-20碼
                    Toast.makeText(this, R.string.password_errorsize, Toast.LENGTH_LONG).show();
                } else {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        Intent login = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(login);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                break;
            case R.id.btn_create:
                Intent create = new Intent(LoginActivity.this, CreateUserActivity.class);
                startActivity(create);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                Intent login = new Intent(this, MainActivity.class);
                startActivity(login);

            } else {
                Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void init() {

//        自定義隱私政策和服務條款
//        startActivityForResult(
//                AuthUI.getInstance()
//                        .createSignInIntentBuilder()
//                        .setAvailableProviders(...)
//                        .setTosUrl("https://superapp.example.com/terms-of-service.html")
//                        .setPrivacyPolicyUrl("https://superapp.example.com/privacy-policy.html")
//                        .build(),
//                RC_SIGN_IN);

        ed_email = (EditText) findViewById(R.id.ed_email);
        ed_password = (EditText) findViewById(R.id.ed_password);

        login = (Button) findViewById(R.id.btn_login);
        login.setOnClickListener(this);

        create = (Button) findViewById(R.id.btn_create);
        create.setOnClickListener(this);

        google = (com.google.android.gms.common.SignInButton) findViewById(R.id.btn_google);
        google.setOnClickListener(this);
    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent login = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(login);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
