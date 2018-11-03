package es.uca.allergioapp.Activities.Admin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

import static es.uca.allergioapp.R.layout.simplerow;

public class ManageIngredientsActivity extends AppCompatActivity {

    List<String> ingredientsList;
    ListView listIngredientsOnUi;
    EditText searchText;
    FloatingActionButton addIngredientButton;
    Activity mActivity;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        listIngredientsOnUi = findViewById(R.id.ingredientsList);
        addIngredientButton = findViewById(R.id.addIngredientButton);
        searchText = findViewById(R.id.searchText);
        ingredientsList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, simplerow, ingredientsList);
        listIngredientsOnUi.setAdapter(arrayAdapter);
        mActivity = this;

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

        navigationView = findViewById(R.id.navigation);
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
                                Toast.makeText(mActivity, "Ya estás en la gestión de ingredientes", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.manageAllergies:
                                Intent manageAllergies = new Intent(mActivity, ManageAllergiesActivity.class);
                                startActivity(manageAllergies);
                                break;
                            case R.id.logout:
                                LogoutFragment logoutFragment = new LogoutFragment();
                                logoutFragment.setmActivity(ManageIngredientsActivity.this);
                                logoutFragment.show(getSupportFragmentManager(), "LOGOUT");
                                break;
                        }
                        return true;
                    }
                }
        );

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence searchText, int start, int before, int count) {
                arrayAdapter.getFilter().filter(searchText.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        retrieveAllIngredients();
        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddIngredientFragment addIngredientFragment = new AddIngredientFragment();
                addIngredientFragment.setMainActivity(mActivity);
                addIngredientFragment.show(getSupportFragmentManager(), "ADD");
            }
        });

        listIngredientsOnUi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String ingredientSelected = (String) adapterView.getItemAtPosition(position);
                Intent ingredientDetails = new Intent(mActivity, IngredientDetails.class);
                ingredientDetails.putExtra("ingredientName", ingredientSelected);
                startActivity(ingredientDetails);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDrawerLayout.isDrawerOpen(navigationView))
            mDrawerLayout.closeDrawers();
    }

    public void retrieveAllIngredients() {

        arrayAdapter.clear();

        AllergioApiClient apiClient = new AllergioApiClient();
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

    public static class AddIngredientFragment extends DialogFragment {

        Activity mActivity;

        public Activity getMainActivity() {
            return mActivity;
        }

        public void setMainActivity(Activity mActivity) {
            this.mActivity = mActivity;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_MaterialComponents_Dialog_Alert);
            LayoutInflater inflater = getActivity().getLayoutInflater();

            final View dialogView = inflater.inflate(R.layout.dialog_add_ingredient, null);

            builder.setView(dialogView)
                    .setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            final AlertDialog dialog = new SpotsDialog.Builder()
                                    .setContext(mActivity)
                                    .setMessage("Añadiendo ingrediente")
                                    .setCancelable(false)
                                    .build();

                            dialog.show();

                            EditText ingredientNameText = dialogView.findViewById(R.id.ingredientName);
                            if (ingredientNameText == null) Log.d("EditText-NULL", "Null");
                            else {
                                String ingredientName = ingredientNameText.getText().toString();
                                AllergioApiClient apiClient = new AllergioApiClient();
                                apiClient.invokeAddIngredient(ingredientName, new AddDeleteCallback() {
                                    @Override
                                    public void onSuccess(Boolean result) {
                                        if (result) {
                                            Toast.makeText(mActivity, "Ingrediente añadido con éxito", Toast.LENGTH_LONG).show();
                                            ((ManageIngredientsActivity) mActivity).retrieveAllIngredients();
                                        } else {
                                            Toast.makeText(mActivity, "El ingrediente ya existe", Toast.LENGTH_LONG).show();
                                        }
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        Toast.makeText(mActivity, "Ha ocurrido un fallo, inténtelo de nuevo más tarde", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }
                                });
                            }
                        }
                    });

            return builder.create();
        }
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
