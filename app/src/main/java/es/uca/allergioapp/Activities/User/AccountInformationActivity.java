package es.uca.allergioapp.Activities.User;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import dmax.dialog.SpotsDialog;
import es.uca.allergioapp.ApiRest.AllergioApiClient;
import es.uca.allergioapp.ApiRest.Callback.LoginCallback;
import es.uca.allergioapp.POJOs.Authority;
import es.uca.allergioapp.POJOs.PreferenceManager;
import es.uca.allergioapp.POJOs.User;
import es.uca.allergioapp.R;

public class AccountInformationActivity extends AppCompatActivity {

    ImageView backButton;
    TextView usernameText;
    TextView nameText;
    TextView surnameText;
    TextView accountTypeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_information);

        if (Build.VERSION.SDK_INT >= 24) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary, getTheme()));
        }

        final AlertDialog dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Cargando perfil")
                .setCancelable(false)
                .build();

        dialog.show();

        usernameText = findViewById(R.id.usernameText);
        nameText = findViewById(R.id.nameText);
        surnameText = findViewById(R.id.surnameText);
        accountTypeText = findViewById(R.id.accountType);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        PreferenceManager preferenceManager = new PreferenceManager(this);
        final String username = preferenceManager.getUsername();

        AllergioApiClient apiClient = new AllergioApiClient();
        apiClient.invokeGetUser(username, new LoginCallback() {
            @Override
            public void onSuccess(@NonNull User user) {
                dialog.dismiss();
                usernameText.setText(username);
                nameText.setText(user.getName());
                surnameText.setText(user.getSurname());
                Authority clientRol = new Authority();
                clientRol.setAuthority("CLIENT_ROL");

                for (Authority auth :
                        user.getAuthorities()) {
                    if (auth.getAuthority().equals("CLIENT_ROL")) {
                        accountTypeText.setText("Usuario");
                        return;
                    } else {
                        accountTypeText.setText("Administrador");
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                Toast.makeText(AccountInformationActivity.this,
                        "No se ha podido recuperar la información del usuario, " +
                                "inténtelo de nuevo más tarde",
                        Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });


    }
}
