package es.uca.allergioapp.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import dmax.dialog.SpotsDialog;
import es.uca.allergioapp.Activities.Admin.ManageIngredientsActivity;
import es.uca.allergioapp.Activities.User.MainOCRActivity;
import es.uca.allergioapp.ApiRest.AllergioApiClient;
import es.uca.allergioapp.ApiRest.Callback.LoginCallback;
import es.uca.allergioapp.POJOs.Authority;
import es.uca.allergioapp.POJOs.PreferenceManager;
import es.uca.allergioapp.POJOs.User;
import es.uca.allergioapp.R;


public class LoginActivity extends AppCompatActivity {

    EditText usernameText;
    EditText passwordText;
    Button loginButton;
    Activity mActivity;

    String username, password;

    PreferenceManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mActivity = this;
        usernameText = findViewById(R.id.usernameText);
        passwordText = findViewById(R.id.passwordText);
        loginButton = findViewById(R.id.loginButton);

        prefManager = new PreferenceManager(this);

        if (prefManager.isLogged()) {
            AllergioApiClient apiClient = new AllergioApiClient();
            apiClient.invokeGetUser(prefManager.getUsername(), new LoginCallback() {
                @Override
                public void onSuccess(@NonNull User user) {
                    onLoginSuccess(user);
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    Toast.makeText(LoginActivity.this, "Ha ocurrido un error con su sesión, inicie sesión de nuevo", Toast.LENGTH_LONG).show();
                    prefManager.setLogged(false);
                }
            });
        }

        if (Build.VERSION.SDK_INT >= 24) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }


    public void login(View view) {

        username = usernameText.getText().toString();
        password = passwordText.getText().toString();

        if (!validate(username, password)) {
            loginButton.setEnabled(true);
            return;
        }

        loginButton.setEnabled(false);
        final AlertDialog dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Iniciando sesión")
                .setCancelable(false)
                .build();

        dialog.show();

        AllergioApiClient apiClient = new AllergioApiClient();
        apiClient.invokeLogin(username, password, new LoginCallback() {
            @Override
            public void onSuccess(@NonNull User result) {
                dialog.dismiss();
                if (result.getAccountNonExpired()
                        && result.getAccountNonLocked()
                        && result.getCredentialsNonExpired()
                        && result.getEnabled())
                    onLoginSuccess(result);
                else
                    onLoginFailed(result);
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                dialog.dismiss();
                Toast.makeText(mActivity, "Credenciales inválidas, inténtelo de nuevo", Toast.LENGTH_LONG).show();
                loginButton.setEnabled(true);
            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void onLoginSuccess(User result) {

        Boolean isAdmin = false;

        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setUsername(result.getUsername());
        preferenceManager.setLogged(true);

        List<Authority> authorities = result.getAuthorities();

        for (Authority auth :
                authorities) {
            if (auth.getAuthority().equals("ADMIN_ROL"))
                isAdmin = true;
        }

        if (isAdmin) {
            Intent adminPage = new Intent(this, ManageIngredientsActivity.class);
            startActivity(adminPage);

        } else {
            Intent OCRIntent = new Intent(this, MainOCRActivity.class);
            startActivity(OCRIntent);
        }
        finish();
    }

    private void onLoginFailed(User result) {

        if (!result.getAccountNonExpired())
            Toast.makeText(this, "Cuenta expirada, contacte con un administrador", Toast.LENGTH_SHORT).show();
        if (!result.getAccountNonLocked())
            Toast.makeText(this, "Cuenta bloqueada, contacte con un administrador", Toast.LENGTH_SHORT).show();
        if (!result.getCredentialsNonExpired())
            Toast.makeText(this, "Credenciales expiradas, contacte con un administrador", Toast.LENGTH_SHORT).show();
        if (!result.getEnabled())
            Toast.makeText(this, "Cuenta desactivada, contacte con un administrador", Toast.LENGTH_SHORT).show();

        loginButton.setEnabled(true);
    }

    private boolean validate(String username, String password) {
        boolean valid = true;

        if (username.isEmpty() || username.length() < 4) {
            usernameText.setError("Debe ingresar un nombre de usuario válido");
            valid = false;
        } else
            usernameText.setError(null);

        if (password.isEmpty() || password.length() < 4 || password.length() > 254) {
            passwordText.setError("La contraseña debe contener más de 4 carácteres alfanuméricos");
            valid = false;
        } else
            passwordText.setError(null);

        return valid;
    }


    public void goToSignUp(View view) {
        Intent signUp = new Intent(this, SignUpActivity.class);
        startActivityForResult(signUp, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        usernameText = findViewById(R.id.usernameText);
        loginButton = findViewById(R.id.loginButton);

        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                usernameText.setText(data.getStringExtra("username"));
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Usamos LoginActivity normalmente
            }

        }
    }
}
