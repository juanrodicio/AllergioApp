package es.uca.allergioapp.Activities.Admin;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import es.uca.allergioapp.ApiRest.AllergioApiClient;
import es.uca.allergioapp.ApiRest.Callback.IngredientAllergyCallback;
import es.uca.allergioapp.Fragments.LogoutFragment;
import es.uca.allergioapp.POJOs.PreferenceManager;
import es.uca.allergioapp.R;

public class ManageAllergiesActivity extends AppCompatActivity {

    List<String> allergiesList;
    ListView listAllergiesOnUi;
    FloatingActionButton addAllergyButton;
    Activity mActivity;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    final static int GO_TO_MANAGE_INGREDIENTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_allergies);

        listAllergiesOnUi = findViewById(R.id.allergiesList);
        addAllergyButton = findViewById(R.id.addAllergyButton);
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
                                finish();
                                break;
                            case R.id.manageAllergies:
                                Toast.makeText(mActivity, "Ya estás en la gestión de alergias", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.logout:
                                LogoutFragment logoutFragment = new LogoutFragment();
                                logoutFragment.setmActivity(ManageAllergiesActivity.this);
                                logoutFragment.show(getSupportFragmentManager(), "LOGOUT");
                                break;
                        }
                        return true;
                    }
                }
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        retrieveAllAllergies();

        addAllergyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addAllergy = new Intent(mActivity, AddAllergyActivity.class);
                startActivityForResult(addAllergy, 101);
            }
        });

        listAllergiesOnUi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String allergySelected = (String) adapterView.getItemAtPosition(position);
                Intent allergyDetails = new Intent(mActivity, AllergyDetails.class);
                allergyDetails.putExtra("allergyName", allergySelected);
                startActivityForResult(allergyDetails, 100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 100 || requestCode == 101) {
            if (resultCode == GO_TO_MANAGE_INGREDIENTS) {
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDrawerLayout.isDrawerOpen(navigationView))
            mDrawerLayout.closeDrawers();
    }

    private void retrieveAllAllergies() {

        allergiesList = new ArrayList<>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.simplerow, allergiesList);
        listAllergiesOnUi.setAdapter(arrayAdapter);

        AllergioApiClient apiClient = new AllergioApiClient();
        apiClient.invokeAllAllergies(new IngredientAllergyCallback() {
            @Override
            public void onSuccess(@NonNull List<String> elements) {
                arrayAdapter.addAll(elements);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                Log.e("FAIL-RESPONSE", "onFailure throwed");
            }
        });
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
