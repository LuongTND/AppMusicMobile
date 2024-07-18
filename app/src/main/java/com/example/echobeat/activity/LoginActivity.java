package com.example.echobeat.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.echobeat.MainActivity;
import com.example.echobeat.R;

import com.example.echobeat.activity.loginModel.OptionRole;

import com.example.echobeat.dbFirebase.FirebaseHelper;
import com.example.echobeat.modelFirebase.User;
import com.example.echobeat.repository.UserRepository;
import com.example.echobeat.session.SessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout googleSignInButton;

    private FirebaseAuth auth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseHelper<User> userHelper;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        /*xu ly logic , kiem tra(( userid trong sqllite ton tai) va ( => den man roleoption
         */
        // Kiểm tra xem session tồn tại, nếu tồn tại thì vào thẳng MainActivity
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.getGoogleId() != null) {
            // Nếu Google ID tồn tại trong session, chuyển sang MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return; // Kết thúc onCreate để tránh tiếp tục xử lý logic đăng nhập
        }
        auth = FirebaseAuth.getInstance();
        googleSignInButton = findViewById(R.id.google_sign_in_button);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInButton.setOnClickListener(v -> {
            googleSingIn();
        });

    }
    private void googleSingIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 20);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    auth.signInWithCredential(credential).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            Toast.makeText(this, "User Signed In", Toast.LENGTH_SHORT).show();
                            firebaseAuthWithGoogle(account.getIdToken());
                        } else {
                            Toast.makeText(this, "Error" + task1.getException(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Đăng nhập thành công, cập nhật UI với thông tin người dùng đã đăng nhập
                        FirebaseUser user = auth.getCurrentUser();
                        String fullname = user.getDisplayName();
                        String email = user.getEmail();
                        String avatar = user.getPhotoUrl().toString();
                        String googleId = user.getUid();
                        int roleId = 1;

                        // Lưu thông tin người dùng vào session
                        SessionManager sessionManager = new SessionManager(this);
                        sessionManager.saveUserSession(googleId,googleId, email, fullname,roleId,avatar);

                        UserRepository userRepository = new UserRepository(getApplicationContext());
                        boolean check = userRepository.checkExistIdGoogle(googleId);
                        if (!check) {

                            // Chuyển đến activity tiếp theo sau khi đăng nhập thành công
                            Intent intent = new Intent(LoginActivity.this, OptionRole.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Welcome!!!", Toast.LENGTH_SHORT).show();
                            //nếu người dùng tồn tại chuyển đến main activity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }

                    } else {
                        // Đăng nhập thất bại, hiển thị thông báo cho người dùng
                        Toast.makeText(this, "Failed to sign in: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}