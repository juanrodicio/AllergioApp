package es.uca.allergioapp.ApiRest.Callback;

import es.uca.allergioapp.POJOs.Allergy;

public interface AllergyCallback {

    void onSuccess(Allergy allergy);

    void onFailure(Throwable t);
}
