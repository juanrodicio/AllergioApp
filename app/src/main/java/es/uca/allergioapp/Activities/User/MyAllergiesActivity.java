package es.uca.allergioapp.Activities.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;
import es.uca.allergioapp.ApiRest.AllergioApiClient;
import es.uca.allergioapp.ApiRest.Callback.AddDeleteCallback;
import es.uca.allergioapp.ApiRest.Callback.AllergyCallback;
import es.uca.allergioapp.ApiRest.Callback.IngredientAllergyCallback;
import es.uca.allergioapp.ApiRest.Callback.LoginCallback;
import es.uca.allergioapp.POJOs.Allergy;
import es.uca.allergioapp.POJOs.ExpandableListAdapter;
import es.uca.allergioapp.POJOs.PreferenceManager;
import es.uca.allergioapp.POJOs.User;
import es.uca.allergioapp.R;

public class MyAllergiesActivity extends AppCompatActivity {

    private ExpandableListView allergiesList;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHashMap;
    FloatingActionButton addAllergyButton;
    PreferenceManager preferenceManager;
    String allergySelected;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_allergies);
        preferenceManager = new PreferenceManager(this);

        addAllergyButton = findViewById(R.id.addAllergyButton);
        backButton = findViewById(R.id.backButton);

        if (Build.VERSION.SDK_INT >= 24) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary, getTheme()));
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        allergiesList = findViewById(R.id.myAllergiesList);
        allergiesList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    allergiesList.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });

        allergiesList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                allergySelected = (String) expandableListView.getExpandableListAdapter().getGroup(groupPosition);
                return false;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        retrieveAllergyData();

        addAllergyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyAllergiesActivity.AddAllergyFragment addAllergyFragment = new MyAllergiesActivity.AddAllergyFragment();
                addAllergyFragment.setMainActivity(MyAllergiesActivity.this);
                addAllergyFragment.show(getSupportFragmentManager(), "ADD");
            }
        });
    }

    private void retrieveAllergyData() {
        listDataHeader = new ArrayList<>();
        listHashMap = new HashMap<>();

        String username = preferenceManager.getUsername();

        AllergioApiClient apiClient = new AllergioApiClient();
        apiClient.invokeGetUser(username, new LoginCallback() {
            @Override
            public void onSuccess(@NonNull User user) {
                for (Allergy allergy :
                        user.getAllergies()) {
                    listDataHeader.add(allergy.getName());
                }

                listAdapter = new ExpandableListAdapter(MyAllergiesActivity.this, listDataHeader, listHashMap);
                allergiesList.setAdapter(listAdapter);
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                Toast.makeText(MyAllergiesActivity.this, "Ha ocurrido un error al intentar mostrar las alergias, inténtelo de nuevo más tarde", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void deleteAllergyFromUser(View view) {

        final AlertDialog dialog = new SpotsDialog.Builder()
                .setContext(MyAllergiesActivity.this)
                .setMessage("Eliminando alergia")
                .setCancelable(false)
                .build();

        dialog.show();

        AllergioApiClient apiClient = new AllergioApiClient();
        apiClient.invokeDeleteAllergyFromUser(preferenceManager.getUsername(), allergySelected, new AddDeleteCallback() {
            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    Toast.makeText(MyAllergiesActivity.this, "Alergia borrada con éxito", Toast.LENGTH_SHORT).show();
                    retrieveAllergyData();
                } else
                    Toast.makeText(MyAllergiesActivity.this, "Ha ocurrido un error eliminando la alergia, inténtelo de nuevo más tarde", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }

            @Override
            public void onFailure(Throwable t) {
                dialog.dismiss();
                Toast.makeText(MyAllergiesActivity.this, "Ha ocurrido un error eliminando la alergia, inténtelo de nuevo más tarde", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void seeDetailsOfAllergy(View view) {

        MyAllergiesActivity.AllergyDetailsFragment allergyDetailsFragment = new MyAllergiesActivity.AllergyDetailsFragment();
        allergyDetailsFragment.setAllergySelected(allergySelected);
        allergyDetailsFragment.setMainActivity(MyAllergiesActivity.this);
        allergyDetailsFragment.show(getSupportFragmentManager(), "DETAILS");
    }

    public static class AddAllergyFragment extends DialogFragment {

        Activity mActivity;
        String allergySelected;

        public Activity getMainActivity() {
            return mActivity;
        }

        public void setMainActivity(Activity mActivity) {
            this.mActivity = mActivity;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

            final PreferenceManager preferenceManager = new PreferenceManager(mActivity);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_MaterialComponents_Dialog_Alert);
            LayoutInflater inflater = getActivity().getLayoutInflater();

            final View dialogView = inflater.inflate(R.layout.dialog_add_allergy, null);

            final Spinner allAllergies = dialogView.findViewById(R.id.allAllergiesDropdown);
            allAllergies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    allergySelected = (String) adapterView.getItemAtPosition(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            List<String> allAllergiesList = new ArrayList<>();
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mActivity, R.layout.simplerow, allAllergiesList);
            allAllergies.setAdapter(arrayAdapter);

            AllergioApiClient apiClient = new AllergioApiClient();
            apiClient.invokeAllAllergies(new IngredientAllergyCallback() {
                @Override
                public void onSuccess(@NonNull List<String> elements) {
                    arrayAdapter.addAll(elements);
                    arrayAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(@NonNull Throwable t) {

                }
            });

            builder.setView(dialogView)
                    .setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (allergySelected == null)
                                Toast.makeText(mActivity,
                                        "Necesita seleccionar una alergia",
                                        Toast.LENGTH_LONG).show();
                            else {
                                AllergioApiClient apiClient = new AllergioApiClient();
                                apiClient.invokeAddAllergyToUser(preferenceManager.getUsername(), allergySelected, new AddDeleteCallback() {
                                    @Override
                                    public void onSuccess(Boolean result) {
                                        if (result) {
                                            Toast.makeText(mActivity,
                                                    "Alergia añadida con éxito",
                                                    Toast.LENGTH_LONG).show();
                                            ((MyAllergiesActivity) mActivity).retrieveAllergyData();
                                        } else
                                            Toast.makeText(mActivity,
                                                    "Ya ha añadido antes dicha alergia",
                                                    Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        Toast.makeText(mActivity,
                                                "Ha ocurrido un error añadiendo la alergia seleccionada",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });

            return builder.create();
        }
    }

    public static class AllergyDetailsFragment extends DialogFragment {

        Activity mActivity;
        String allergySelected;

        public String getAllergySelected() {
            return allergySelected;
        }

        public void setAllergySelected(String allergySelected) {
            this.allergySelected = allergySelected;
        }

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

            final View dialogView = inflater.inflate(R.layout.dialog_allergy_details, null);

            final TextView allergyDesc = dialogView.findViewById(R.id.allergyDesc);

            AllergioApiClient apiClient = new AllergioApiClient();
            apiClient.invokeGetAllergy(allergySelected, new AllergyCallback() {
                @Override
                public void onSuccess(Allergy allergy) {
                    allergyDesc.setText(allergy.getDescription());
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });

            builder.setView(dialogView)
                    .setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            return builder.create();
        }
    }
}
