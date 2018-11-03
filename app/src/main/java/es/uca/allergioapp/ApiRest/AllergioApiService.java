package es.uca.allergioapp.ApiRest;

import java.util.List;

import es.uca.allergioapp.POJOs.Allergy;
import es.uca.allergioapp.POJOs.User;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Juan on 12/07/2018.
 */

public interface AllergioApiService {

    @GET("convertIngredients")
    Call<List<String>> convertIngredients(@Query("ingredients") String ingredients);

    @POST("addIngredient")
    Call<Boolean> addIngredient(@Query("ingredientName") String ingredientName);

    @DELETE("deleteIngredient/{ingredientName}")
    Call<Boolean> deleteIngredient(@Path("ingredientName") String ingredientName);

    @GET("allIngredients")
    Call<List<String>> allIngredients();

    @GET("allAllergies")
    Call<List<String>> allAllergies();

    @GET("getAllergy")
    Call<Allergy> getAllergy(@Query("allergyName") String allergyName);

    @POST("addAllergy")
    Call<Boolean> addAllergy(@Query("allergyName") String allergyName,
                             @Query("allergyDesc") String allergyDesc);

    @PUT("updateAllergy")
    Call<Boolean> updateAllergy(@Query("allergyName") String allergyName,
                                @Query("allergyDesc") String allergyDesc);

    @DELETE("deleteAllergy")
    Call<Boolean> deleteAllergy(@Query("allergyName") String allergyName);

    @POST("addAllergyToIngredient")
    Call<Boolean> addAllergyToIngredient(@Query("ingredientName") String ingredientName,
                                         @Query("allergyName") String allergyName);

    @DELETE("deleteAllergyFromIngredient")
    Call<Boolean> deleteAllergyFromIngredient(@Query("ingredientName") String ingredientName,
                                              @Query("allergyName") String allergyName);

    @GET("login")
    Call<User> login(@Query("username") String username, @Query("password") String password);

    @GET("register")
    Call<Boolean> register(@Query("name") String name,
                           @Query("surname") String surname,
                           @Query("username") String username,
                           @Query("password") String password);

    @GET("getUser")
    Call<User> getUser(@Query("username") String username);

    @POST("addAllergyToUser")
    Call<Boolean> addAllergyToUser(@Query("username") String username, @Query("allergyName") String allergyName);

    @DELETE("deleteAllergyFromUser")
    Call<Boolean> deleteAllergyFromUser(@Query("username") String username, @Query("allergyName") String allergyName);
}
