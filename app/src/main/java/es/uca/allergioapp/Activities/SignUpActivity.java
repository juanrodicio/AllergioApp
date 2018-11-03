package es.uca.allergioapp.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import dmax.dialog.SpotsDialog;
import es.uca.allergioapp.ApiRest.AllergioApiClient;
import es.uca.allergioapp.ApiRest.Callback.SignUpCallback;
import es.uca.allergioapp.R;

public class SignUpActivity extends AppCompatActivity {

    TextView nameText;
    TextView surnameText;
    TextView usernameText;
    TextView passwordText;
    TextView confirmPasswordText;
    Button signUpButton;

    String name, surname, username, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameText = findViewById(R.id.nameText);
        surnameText = findViewById(R.id.surnameText);
        usernameText = findViewById(R.id.usernameText);
        passwordText = findViewById(R.id.passwordText);
        confirmPasswordText = findViewById(R.id.confirmPasswordText);
        signUpButton = findViewById(R.id.signUpButton);

        if (Build.VERSION.SDK_INT >= 24) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void signUp(View view) {

        name = nameText.getText().toString();
        surname = surnameText.getText().toString();
        username = usernameText.getText().toString();
        password = passwordText.getText().toString();
        confirmPassword = confirmPasswordText.getText().toString();

        if (!validate(name, surname, username, password, confirmPassword)) {
            onSignUpFailed();
            return;
        }

        signUpButton.setEnabled(false);

        final AlertDialog dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Registrando cuenta")
                .setCancelable(false)
                .build();
        dialog.show();

        AllergioApiClient apiClient = new AllergioApiClient();
        apiClient.invokeRegister(name, surname, username, password, new SignUpCallback() {
            @Override
            public void onSuccess(@NonNull Boolean result) {
                dialog.dismiss();

                if (result) {
                    onSignUpSuccess();
                } else {
                    onSignUpFailed();
                    usernameText.setError("El nombre de usuario ya existe");
                }
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                dialog.dismiss();
                Log.e("FAIL-RESPONSE", "onFailure SignUpCallback");
            }
        });

    }

    private void onSignUpSuccess() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("username", username);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void onSignUpFailed() {
        signUpButton.setEnabled(true);
    }

    private boolean validate(String name, String surname, String username, String password, String confirmPassword) {

        boolean valid = true;

        if (name.isEmpty() || name.length() < 2) {
            nameText.setError("Debe ingresar un nombre válido");
            valid = false;
        } else
            nameText.setError(null);

        if (surname.isEmpty() || surname.length() < 2) {
            surnameText.setError("Debe ingresar un apellido/s válido/s");
            valid = false;
        } else
            surnameText.setError(null);

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

        if (!password.equals(confirmPassword)) {
            confirmPasswordText.setError("Las contraseñas deben coincidir");
            valid = false;
        } else
            confirmPasswordText.setError(null);

        return valid;
    }

    public void goToLogIn(View view) {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}
