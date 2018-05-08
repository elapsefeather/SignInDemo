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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateUserActivity extends AppCompatActivity implements View.OnClickListener {

    String TAG = "CreateUserActivity";
    private FirebaseAuth mAuth;

    Button createuser;
    EditText ed_email, ed_password, ed_password2;

    String email = "", password = "", password2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createuser);

        mAuth = FirebaseAuth.getInstance();

        init();
    }

    public void init() {

        ed_email = (EditText) findViewById(R.id.create_account);
        ed_password = (EditText) findViewById(R.id.create_password);
        ed_password2 = (EditText) findViewById(R.id.create_password2);

        createuser = (Button) findViewById(R.id.btn_createuser);
        createuser.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_createuser:

                email = ed_email.getText().toString().trim();
                password = ed_password.getText().toString().trim();
                password2 = ed_password2.getText().toString().trim();

                if (email.equals("")) {
//                    未輸入帳號
                    Toast.makeText(this, R.string.email_null, Toast.LENGTH_LONG).show();
                } else if (password.equals("")) {
//                    未輸入密碼
                    Toast.makeText(this, R.string.password_null, Toast.LENGTH_LONG).show();
                } else if (password.length() < 6 || password.length() > 20) {
//                   密碼不為6-20碼
                    Toast.makeText(this, R.string.password_errorsize, Toast.LENGTH_LONG).show();
                } else if (password.equals(password2)) {
//                    密碼輸入不同
                    Toast.makeText(this, R.string.password_different, Toast.LENGTH_LONG).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        Intent login = new Intent(CreateUserActivity.this, MainActivity.class);
                                        login.putExtra("login_pass",getResources().getString(R.string.google_login));
                                        startActivity(login);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(CreateUserActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                break;
        }
    }
}
