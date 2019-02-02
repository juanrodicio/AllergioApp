package es.uca.allergioapp.ApiRest;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import es.uca.allergioapp.ApiRest.Callback.AddDeleteCallback;
import es.uca.allergioapp.ApiRest.Callback.AllergyCallback;
import es.uca.allergioapp.ApiRest.Callback.IngredientAllergyCallback;
import es.uca.allergioapp.ApiRest.Callback.LoginCallback;
import es.uca.allergioapp.ApiRest.Callback.SignUpCallback;
import es.uca.allergioapp.POJOs.Allergy;
import es.uca.allergioapp.POJOs.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Juan on 12/07/2018.
 */

public class AllergioApiClient {

    private static final String BASE_URL = "http://34.238.162.219:8081/api/";
    private static Retrofit retrofit = null;

    public AllergioApiClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void invokeGetUser(String username, final LoginCallback loginCallback) {
        AllergioApiService apiService = retrofit.create(AllergioApiService.class);
        Call<User> call = apiService.getUser(username);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful())
                    loginCallback.onSuccess(response.body());
                else
                    Log.e("ERROR-RESPONSE", String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("FAILED-RESTAPI", t.toString());
                loginCallback.onFailure(t);
            }
        });
    }

    public void invokeRegister(String name, String surname, String username, String password,
                               @Nullable final SignUpCallback signUpCallback) {

        AllergioApiService apiService = retrofit.create(AllergioApiService.class);
        Call<Boolean> call = apiService.register(name, surname, username, password);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Boolean signUpResponse = response.body();
                    if (signUpCallback != null)
                        signUpCallback.onSuccess(signUpResponse);
                } else {
                    Log.e("ERROR-RESPONSE", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("FAILED-RESTAPI", t.toString());
                if (signUpCallback != null)
                    signUpCallback.onFailure(t);
            }
        });
    }

    public void invokeLogin(String username, String password, @Nullable final LoginCallback loginCallback) {

        AllergioApiService apiService = retrofit.create(AllergioApiService.class);
        Call<User> call = apiService.login(username, password);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (response.isSuccessful()) {
                    User loginResponse = response.body();
                    if (loginCallback != null)
                        loginCallback.onSuccess(loginResponse);
                } else {
                    Log.e("ERROR-RESPONSE", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("FAILED-RESTAPI", t.toString());
                if (loginCallback != null)
                    loginCallback.onFailure(t);
            }
        });
    }

    public void invokeConvertIngredients(String ingredients, @Nullable final IngredientAllergyCallback ingredientAllergyCallback) {

        AllergioApiService apiService = retrofit.create(AllergioApiService.class);
        Call<List<String>> call = apiService.convertIngredients(ingredients);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {

                if (response.isSuccessful()) {
                    List<String> ingredients = response.body();
                    ingredientAllergyCallback.onSuccess(ingredients);
                } else {
                    Log.e("ERROR-RESPONSE", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("FAILED-RESTAPI", t.toString());
                ingredientAllergyCallback.onFailure(t);
            }
        });
    }

    public void invokeAddIngredient(String ingredientName, final AddDeleteCallback addDeleteCallback) {
        AllergioApiService apiService = retrofit.create(AllergioApiService.class);
        Call<Boolean> call = apiService.addIngredient(ingredientName);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Boolean result = response.body();
                    addDeleteCallback.onSuccess(result);
                } else {
                    Log.e("ERROR-RESPONSE", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                addDeleteCallback.onFailure(t);
                Log.e("FAILED-RESTAPI", t.toString());
            }
        });
    }

    public void invokeDeleteIngredient(String ingredientName, final AddDeleteCallback addDeleteCallback) {
        AllergioApiService apiService = retrofit.create(AllergioApiService.class);
        Call<Boolean> call = apiService.deleteIngredient(ingredientName);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Boolean result = response.body();
                    addDeleteCallback.onSuccess(result);
                } else {
                    Log.e("ERROR-RESPONSE", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("FAILED-RESTAPI", t.toString());
                addDeleteCallback.onFailure(t);
            }
        });
    }

    public void invokeAllIngredients(final IngredientAllergyCallback ingredientAllergyCallback) {
        AllergioApiService apiService = retrofit.create(AllergioApiService.class);
        Call<List<String>> call = apiService.allIngredients();

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    ingredientAllergyCallback.onSuccess(response.body());
                } else {
                    Log.e("ERROR-RESPONSE", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("FAILED-RESTAPI", t.toString());
            }
        });
    }

    public void invokeAllAllergies(final IngredientAllergyCallback allergyCallback) {

        AllergioApiService apiService = retrofit.create(AllergioApiService.class);
        Call<List<String>> call = apiService.allAllergies();

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    allergyCallback.onSuccess(response.body());
                } else {
                    Log.e("ERROR-RESPONSE", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("FAILED-RESTAPI", t.toString());
            }
        });

    }

    public void invokeGetAllergy(String allergyName, final AllergyCallback allergyCallback) {
        AllergioApiService apiService = retrofit.create(AllergioApiService.class);
        Call<Allergy> call = apiService.getAllergy(allergyName);

        call.enqueue(new Callback<Allergy>() {
            @Override
            public void onResponse(Call<Allergy> call, Response<Allergy> response) {
                if (response.isSuccessful()) {
                    allergyCallback.onSuccess(response.body());
                } else {
                    Log.e("ERROR-RESPONSE", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Allergy> call, Throwable t) {
                Log.e("FAILED-RESTAPI", t.toString());
            }
        });
    }

    public void invokeAddAllergy(String allergyName, String allergyDesc,
                                 final AddDeleteCallback addDeleteCallback) {
        AllergioApiService apiService = retrofit.create(AllergioApiService.class);
        Call<Boolean> call = apiService.addAllergy(allergyName, allergyDesc);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    addDeleteCallback.onSuccess(response.body());
                } else {
                    Log.e("ERROR-RESPONSE", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("FAILED-RESTAPI", t.toString());
            }
        });

    }

    public void invokeUpdateAllergy(String allergyName, String allergyDesc,
                                    final AddDeleteCallback addDeleteCallback) {
        AllergioApiService apiService = retrofit.create(AllergioApiService.class);
        Call<Boolean> call = apiService.updateAllergy(allergyName, allergyDesc);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    addDeleteCallback.onSuccess(response.body());
                } else {
                    Log.e("ERROR-RESPONSE", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("FAILED-RESTAPI", t.toString());
            }
        });
    }

    public void invokeDeleteAllergy(String allergyName, final AddDeleteCallback addDeleteCallback) {
        AllergioApiService apiService = retrofit.create(AllergioApiService.class);
        Call<Boolean> call = apiService.deleteAllergy(allergyName);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    addDeleteCallback.onSuccess(response.body());
                } else {
                    Log.e("ERROR-RESPONSE", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("FAILED-RESTAPI", t.toString());
            }
        });
    }

    public void invokeAddAllergyToIngredient(String ingredientName, String allergyName) {
        AllergioApiService apiService = retrofit.create(AllergioApiService.class);
        Call<Boolean> call = apiService.addAllergyToIngredient(ingredientName, allergyName);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {

                } else {
                    Log.e("ERROR-RESPONSE", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("FAILED-RESTAPI", t.toString());
            }
        });
    }

    public void invokeDeleteAllergyFromIngredient(String ingredientName, String allergyName) {
        AllergioApiService apiService = retrofit.create(AllergioApiService.class);
        Call<Boolean> call = apiService.deleteAllergyFromIngredient(ingredientName, allergyName);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {

                } else
                    Log.e("ERROR-RESPONSE", String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("FAILED-RESTAPI", t.toString());
            }
        });
    }

    public void invokeAddAllergyToUser(String username, String allergyName, final AddDeleteCallback addDeleteCallback) {
        AllergioApiService apiService = retrofit.create(AllergioApiService.class);
        Call<Boolean> call = apiService.addAllergyToUser(username, allergyName);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful())
                    addDeleteCallback.onSuccess(response.body());
                else
                    Log.e("FAILED_RESTAPI", toString());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                addDeleteCallback.onFailure(t);
            }
        });
    }

    public void invokeDeleteAllergyFromUser(String username, String allergyName, final AddDeleteCallback addDeleteCallback) {
        AllergioApiService apiService = retrofit.create(AllergioApiService.class);
        Call<Boolean> call = apiService.deleteAllergyFromUser(username, allergyName);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful())
                    addDeleteCallback.onSuccess(response.body());
                else {
                    Log.e("FAILED_RESTAPI", toString());
                    addDeleteCallback.onSuccess(response.body());
                }

            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                addDeleteCallback.onFailure(t);
            }
        });
    }
}
