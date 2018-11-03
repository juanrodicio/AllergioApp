package es.uca.allergioapp.Activities.Admin;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import es.uca.allergioapp.ApiRest.AllergioApiClient;
import es.uca.allergioapp.ApiRest.Callback.AddDeleteCallback;
import es.uca.allergioapp.ApiRest.Callback.AllergyCallback;
import es.uca.allergioapp.ApiRest.Callback.IngredientAllergyCallback;
import es.uca.allergioapp.POJOs.Allergy;
import es.uca.allergioapp.POJOs.Ingredient;
import es.uca.allergioapp.R;

public class IngredientDetails extends AppCompatActivity {

    String ingredientName;
    TextView ingredientNameView;
    Button deleteButton;
    ImageView backButton;
    ListView relatedAllergiesList;
    Activity mActivity;
    List<String> relatedAllergies;
    ArrayAdapter<String> arrayAdapter;
    TextView noRelatedAllergies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_details);

        ingredientNameView = findViewById(R.id.ingredientName);
        deleteButton = findViewById(R.id.deleteButton);
        relatedAllergiesList = findViewById(R.id.relatedAllergies);

        relatedAllergies = new ArrayList<>();
        backButton = findViewById(R.id.backButton);

        noRelatedAllergies = findViewById(R.id.noAllergiesRelatedText);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mActivity = this;

        if (Build.VERSION.SDK_INT >= 24) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary, getTheme()));
        }

        arrayAdapter = new ArrayAdapter<>(this, R.layout.simplerow, relatedAllergies);
        relatedAllergiesList.setAdapter(arrayAdapter);

        ingredientName = getIntent().getStringExtra("ingredientName");
        ingredientNameView.setText(ingredientName);

        AllergioApiClient apiClient = new AllergioApiClient();
        apiClient.invokeAllAllergies(new IngredientAllergyCallback() {
            @Override
            public void onSuccess(@NonNull List<String> allergies) {
                for (String allergy :
                        allergies) {
                    apiClient.invokeGetAllergy(allergy, new AllergyCallback() {
                        @Override
                        public void onSuccess(Allergy allergy) {

                            List<Ingredient> ingredientsRelated = allergy.getRelatedIngredients();
                            List<String> stringIngredientsRelated = new ArrayList<>();
                            ingredientsRelated.forEach(ingredient -> stringIngredientsRelated.add(ingredient.getName()));

                            if (stringIngredientsRelated.contains(ingredientName)) {
                                relatedAllergies.add(allergy.getName());
                                arrayAdapter.notifyDataSetChanged();

                                noRelatedAllergies.setVisibility(View.INVISIBLE);
                                relatedAllergiesList.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onFailure(Throwable t) {

                        }
                    });
                }
                noRelatedAllergies.setText(R.string.no_allergies_found);

            }

            @Override
            public void onFailure(@NonNull Throwable t) {

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
                apiClient.invokeDeleteIngredient(ingredientName, new AddDeleteCallback() {
                    @Override
                    public void onSuccess(Boolean result) {
                        if (result) {
                            Toast.makeText(mActivity, "Ingrediente eliminado con éxito", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(mActivity, "Ha ocurrido un error eliminando el ingrediente", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(mActivity, "Ha ocurrido un fallo, inténtelo de nuevo más tarde", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

}
