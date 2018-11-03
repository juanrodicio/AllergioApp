package es.uca.allergioapp.Activities.User;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import es.uca.allergioapp.Fragments.LogoutFragment;
import es.uca.allergioapp.POJOs.PreferenceManager;
import es.uca.allergioapp.R;

public class ManageAccountActivity extends AppCompatActivity {

    ListView accountMenu;

    final int ACCOUNT_INFORMATION = 0;
    final int MY_ALLERGIES = 1;

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        accountMenu = findViewById(R.id.accountMenu);

        accountMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == ACCOUNT_INFORMATION) {
                    Intent accountInformation = new Intent(ManageAccountActivity.this, AccountInformationActivity.class);
                    startActivity(accountInformation);
                }

                if (position == MY_ALLERGIES) {
                    Intent myAllergiesConfig = new Intent(ManageAccountActivity.this, MyAllergiesActivity.class);
                    startActivity(myAllergiesConfig);
                }
            }
        });

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
                            case R.id.doOCR:
                                finish();
                                break;
                            case R.id.manageAccount:
                                Toast.makeText(ManageAccountActivity.this, "Ya estás gestionando tu cuenta", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.logout:
                                LogoutFragment logoutFragment = new LogoutFragment();
                                logoutFragment.setmActivity(ManageAccountActivity.this);
                                logoutFragment.show(getSupportFragmentManager(), "LOGOUT");
                                break;
                        }
                        return true;
                    }
                }
        );
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
