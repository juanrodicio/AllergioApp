package es.uca.allergioapp.ApiRest.Callback;

import android.support.annotation.NonNull;

import es.uca.allergioapp.POJOs.User;

/**
 * Created by Juan on 01/08/2018.
 */

public interface LoginCallback {

    void onSuccess(@NonNull User result);

    void onFailure(@NonNull Throwable t);
}
