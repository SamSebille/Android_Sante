package com.example.android_sante;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.android_sante.databinding.ActivityLoginBinding;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private boolean isLogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Copier les fichiers JSON des ressources brutes vers le stockage interne
        JsonUtils.copyRawJsonToInternalStorage(this, R.raw.crediential, "credentials.json");
        JsonUtils.copyRawJsonToInternalStorage(this, R.raw.databody, "databody.json");
        JsonUtils.copyRawJsonToInternalStorage(this, R.raw.food, "food.json");
        JsonUtils.copyRawJsonToInternalStorage(this, R.raw.lunch, "lunch.json");
        JsonUtils.copyRawJsonToInternalStorage(this, R.raw.activity, "activity.json");

        binding.tvToggle.setOnClickListener(view -> {
            isLogin = !isLogin;
            if (isLogin) {
                binding.tvTitle.setText(R.string.connexion);
                binding.loginButton.setText(R.string.connexionAccount);
                binding.tvToggle.setText(R.string.createAccount);
                binding.registerFields.setVisibility(View.GONE);
            } else {
                binding.tvTitle.setText(R.string.register);
                binding.loginButton.setText(R.string.registerAccount);
                binding.tvToggle.setText(R.string.connexionAccount);
                binding.registerFields.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        binding.loginButton.setOnClickListener(view -> {
            String id = "";
            if (binding.editTextEmail.getText() == null || binding.editTextEmail.getText().toString().isEmpty()) {
                binding.editTextEmail.setError("Please enter your email");
                return;
            }
            if (binding.editTextPassword.getText() == null || binding.editTextPassword.getText().toString().isEmpty()) {
                binding.editTextPassword.setError("Please enter your password");
                return;
            }

            String username = binding.editTextEmail.getText().toString();
            String password = binding.editTextPassword.getText().toString();

            List<Credential> userCredentials = JsonUtils.getUserCredentials(this);
            boolean authenticated = false;

            for (Credential credentials : userCredentials) {
                if (username.equals(credentials.getUsername()) && password.equals(credentials.getPassword())) {
                    id = String.valueOf(credentials.getId());
                    authenticated = true;
                    break;
                }
            }

            if (authenticated) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("ID", id);
                startActivity(intent);
            } else {
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Erreur de connexion")
                        .setMessage("Email ou mot de passe invalide")
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }
}