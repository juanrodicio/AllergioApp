package es.uca.allergioapp.Activities.User;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import es.uca.allergioapp.ApiRest.AllergioApiClient;
import es.uca.allergioapp.ApiRest.Callback.LoginCallback;
import es.uca.allergioapp.POJOs.Allergy;
import es.uca.allergioapp.POJOs.Ingredient;
import es.uca.allergioapp.POJOs.PreferenceManager;
import es.uca.allergioapp.POJOs.User;
import es.uca.allergioapp.R;

public class ListIngredientsActivity extends AppCompatActivity {

    ListView ingredientsListOnUi;
    List<String> ingredientsList;
    Button finishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_ingredients);

        finishButton = findViewById(R.id.finishButton);
        ingredientsListOnUi = findViewById(R.id.ingredientsList);

        if (Build.VERSION.SDK_INT >= 24) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary, getTheme()));
        }

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToStart = new Intent(ListIngredientsActivity.this, MainOCRActivity.class);
                startActivity(goToStart);
            }
        });
    }

    @Override
    protected void onStart() {

        super.onStart();

        final ArrayList<String> ingredients = (ArrayList<String>) getIntent().getSerializableExtra("ingredients");
        ingredientsList = new ArrayList<>();

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, ingredientsList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                PreferenceManager preferenceManager = new PreferenceManager(ListIngredientsActivity.this);
                AllergioApiClient apiClient = new AllergioApiClient();

                apiClient.invokeGetUser(preferenceManager.getUsername(), new LoginCallback() {
                    @Override
                    public void onSuccess(@NonNull User user) {

                        List<Allergy> allergiesOfUser = user.getAllergies();
                        List<Ingredient> ingredientsRelated = new ArrayList<>();

                        List<String> allergicIngredients = new ArrayList<>();

                        Stream.of(allergiesOfUser)
                                .map(Allergy::getRelatedIngredients)
                                .forEach(ingredientsRelated::addAll);

                        Stream.of(ingredientsRelated)
                                .map(Ingredient::getName)
                                .forEach(allergicIngredients::add);

                        if (allergicIngredients.contains(ingredients.get(position))) {
                            view.setBackgroundColor(getResources().getColor(R.color.red_ingredient, getTheme()));
                        } else {
                            view.setBackgroundColor(getResources().getColor(R.color.bg_slider_screen2, getTheme()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Throwable t) {
                        Toast.makeText(ListIngredientsActivity.this, "Ha ocurrido un error inesperado, por favor contacte con un administrador", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

                return view;
            }
        };
        ingredientsListOnUi.setAdapter(arrayAdapter);
        arrayAdapter.addAll(ingredients);
        arrayAdapter.notifyDataSetChanged();
    }
}
