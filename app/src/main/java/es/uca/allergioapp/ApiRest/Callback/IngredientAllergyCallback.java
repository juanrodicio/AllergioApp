package es.uca.allergioapp.ApiRest.Callback;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Juan on 28/07/2018.
 */

public interface IngredientAllergyCallback {

    void onSuccess(@NonNull List<String> elements);

    void onFailure(@NonNull Throwable t);
}
