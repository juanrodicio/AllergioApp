package es.uca.allergioapp.Activities.Admin;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import es.uca.allergioapp.ApiRest.AllergioApiClient;
import es.uca.allergioapp.ApiRest.Callback.AddDeleteCallback;
import es.uca.allergioapp.ApiRest.Callback.IngredientAllergyCallback;
import es.uca.allergioapp.Fragments.LogoutFragment;
import es.uca.allergioapp.POJOs.PreferenceManager;
import es.uca.allergioapp.R;

public class AddAllergyActivity extends AppCompatActivity {

    TextView allergyNameText;
    TextView allergyDescText;
    ListView listIngredientsOnUi;
    Button addAllergyButton;

    String allergyName, allergyDesc;
    List<String> ingredientsList;
    AllergioApiClient apiClient;

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_allergy);

        allergyNameText = findViewById(R.id.allergyNameText);
        allergyDescText = findViewById(R.id.allergyDescText);
        listIngredientsOnUi = findViewById(R.id.ingredientsList);
        addAllergyButton = findViewById(R.id.addButton);

        if (Build.VERSION.SDK_INT >= 24) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary, getTheme()));
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.navigation);
        mDrawerLayout = findViewById(R.id.drawerLayout);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.baseline_menu_white_24);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        PreferenceManager preferenceManager = new PreferenceManager(this);

        View headerView = navigationView.getHeaderView(0);
        TextView usernameText = headerView.findViewById(R.id.usernameText);
        usernameText.setText(preferenceManager.getUsername());

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.manageIngredients:
                                setResult(1);
                                finish();
                                break;
                            case R.id.manageAllergies:
                                finish();
                                break;
                            case R.id.logout:
                                LogoutFragment logoutFragment = new LogoutFragment();
                                logoutFragment.setmActivity(AddAllergyActivity.this);
                                logoutFragment.show(getSupportFragmentManager(), "LOGOUT");
                                break;
                        }
                        return true;
                    }
                }
        );
    }

    public void retrieveAllIngredients() {

        ingredientsList = new ArrayList<>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, ingredientsList);
        listIngredientsOnUi.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listIngredientsOnUi.setAdapter(arrayAdapter);


        apiClient = new AllergioApiClient();
        apiClient.invokeAllIngredients(new IngredientAllergyCallback() {
            @Override
            public void onSuccess(@NonNull List<String> ingredients) {
                arrayAdapter.addAll(ingredients);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                Log.e("FAIL-RESPONSE", "onFailure throwed");
            }
        });
    }

    public void retrieveAllCheckedIngredients(final List<String> relatedIngredients) {

        ingredientsList = new ArrayList<>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, ingredientsList);
        listIngredientsOnUi.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listIngredientsOnUi.setAdapter(arrayAdapter);

        apiClient = new AllergioApiClient();
        apiClient.invokeAllIngredients(new IngredientAllergyCallback() {
            @Override
            public void onSuccess(@NonNull List<String> ingredients) {
                arrayAdapter.addAll(ingredients);

                for (String ingredient :
                        relatedIngredients) {
                    listIngredientsOnUi.setItemChecked(ingredients.indexOf(ingredient), true);
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                Log.e("FAIL-RESPONSE", "onFailure throwed");
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (getIntent().hasExtra("allergyName")) {
            allergyNameText.setText(getIntent().getStringExtra("allergyName"));
            allergyDescText.setText(getIntent().getStringExtra("allergyDesc"));

            retrieveAllCheckedIngredients(getIntent().getStringArrayListExtra("allergyRelatedIngredients"));

        } else
            retrieveAllIngredients();
    }

    public void addAllergy(View view) {
        allergyName = allergyNameText.getText().toString();
        allergyDesc = allergyDescText.getText().toString();

        if (!validate(allergyName, allergyDesc)) {
            onAddFailed();
            return;
        }

        addAllergyButton.setEnabled(false);

        final AlertDialog dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Añadiendo alergia")
                .setCancelable(false)
                .build();
        dialog.show();

        apiClient = new AllergioApiClient();

        if (!getIntent().hasExtra("allergyName")) {
            apiClient.invokeAddAllergy(allergyName, allergyDesc, new AddDeleteCallback() {
                @Override
                public void onSuccess(Boolean result) {
                    if (result) {
                        onAddSuccess();

                    } else {
                        onAddFailed();
                    }
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Throwable t) {
                    dialog.dismiss();
                    Log.e("FAIL-RESPONSE", "onFailure throwed");
                }
            });
        } else { //Estamos editando la alergia
            apiClient.invokeUpdateAllergy(allergyName, allergyDesc, new AddDeleteCallback() {
                @Override
                public void onSuccess(Boolean result) {
                    if (result) {
                        onAddSuccess();
                    } else {
                        onAddFailed();
                    }
                    dialog.dismiss();
                }

                @Override
                public void onFailure(Throwable t) {
                    dialog.dismiss();
                    Log.e("FAIL-RESPONSE", "onFailure throwed");
                }
            });
        }
    }

    private void onAddSuccess() {

        apiClient = new AllergioApiClient();

        SparseBooleanArray sparseBooleanArray = listIngredientsOnUi.getCheckedItemPositions();

        for (int i = 0; i < listIngredientsOnUi.getCount(); i++) {
            if (sparseBooleanArray.get(i)) {
                apiClient.invokeAddAllergyToIngredient(listIngredientsOnUi.getItemAtPosition(i).toString(),
                        allergyName);
            } else {
                apiClient.invokeDeleteAllergyFromIngredient(listIngredientsOnUi.getItemAtPosition(i).toString(),
                        allergyName);
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Alergia añadida correctamente", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void onAddFailed() {
        addAllergyButton.setEnabled(true);
        Toast.makeText(this, "Ha ocurrido un error, inténtelo de nuevo.", Toast.LENGTH_SHORT).show();
    }

    private boolean validate(String allergyName, String allergyDesc) {
        boolean valid = true;

        if (allergyName.isEmpty()) {
            allergyNameText.setError("Complete este campo");
            valid = false;
        } else
            allergyNameText.setError(null);

        if (allergyDesc.isEmpty() || allergyDesc.length() < 10) {
            allergyDescText.setError("Proporcione una descripción válida");
            valid = false;
        } else
            allergyDescText.setError(null);

        return valid;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
