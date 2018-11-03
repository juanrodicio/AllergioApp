package es.uca.allergioapp.Activities.Admin;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import es.uca.allergioapp.ApiRest.AllergioApiClient;
import es.uca.allergioapp.ApiRest.Callback.AddDeleteCallback;
import es.uca.allergioapp.ApiRest.Callback.AllergyCallback;
import es.uca.allergioapp.POJOs.Allergy;
import es.uca.allergioapp.POJOs.Ingredient;
import es.uca.allergioapp.R;

public class AllergyDetails extends AppCompatActivity {

    String allergyName, allergyDesc;
    TextView allergyNameView;
    TextView allergyDescView;
    Button deleteButton;
    Button updateButton;
    ImageView backButton;
    Activity mActivity;
    List<Ingredient> relatedIngredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergy_details);

        allergyNameView = findViewById(R.id.allergyName);
        allergyDescView = findViewById(R.id.descText);
        deleteButton = findViewById(R.id.deleteButton);
        updateButton = findViewById(R.id.updateButton);
        backButton = findViewById(R.id.backButton);
        mActivity = this;

        if (Build.VERSION.SDK_INT >= 24) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary, getTheme()));
        }

        allergyName = getIntent().getStringExtra("allergyName");
        allergyNameView.setText(allergyName);

        AllergioApiClient apiClient = new AllergioApiClient();
        apiClient.invokeGetAllergy(allergyName, new AllergyCallback() {
            @Override
            public void onSuccess(Allergy allergy) {
                allergyName = allergy.getName();
                allergyDesc = allergy.getDescription();
                allergyDescView.setText(allergyDesc);

                relatedIngredients = allergy.getRelatedIngredients();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(mActivity, "Ha ocurrido un error leyendo la descripción de la alergia.", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllergioApiClient apiClient = new AllergioApiClient();
                apiClient.invokeDeleteAllergy(allergyName, new AddDeleteCallback() {
                    @Override
                    public void onSuccess(Boolean result) {
                        if (result) {
                            Toast.makeText(mActivity, "Alergia eliminada con éxito", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(mActivity, "Ha ocurrido un error eliminando la alergia", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(mActivity, "Ha ocurrido un fallo, inténtelo de nuevo más tarde", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    public void goToEditAllergy(View view) {

        AllergioApiClient apiClient = new AllergioApiClient();
        apiClient.invokeGetAllergy(allergyName, new AllergyCallback() {
            @Override
            public void onSuccess(Allergy allergy) {
                allergyDesc = allergy.getDescription();
                allergyDescView.setText(allergyDesc);

                relatedIngredients = allergy.getRelatedIngredients();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(mActivity, "Ha ocurrido un error leyendo la descripción de la alergia.", Toast.LENGTH_LONG).show();
            }
        });

        Intent updateAllergy = new Intent(this, AddAllergyActivity.class);
        updateAllergy.putExtra("allergyName", allergyName);
        updateAllergy.putExtra("allergyDesc", allergyDesc);
        ArrayList<String> ingredients = new ArrayList<>();
        for (Ingredient ing :
                relatedIngredients) {
            ingredients.add(ing.getName());
        }
        updateAllergy.putExtra("allergyRelatedIngredients", ingredients);

        startActivity(updateAllergy);
    }
}
