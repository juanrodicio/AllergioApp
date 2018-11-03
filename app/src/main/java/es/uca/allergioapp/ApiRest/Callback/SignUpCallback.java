package es.uca.allergioapp.ApiRest.Callback;

import android.support.annotation.NonNull;

/**
 * Created by Juan on 28/07/2018.
 */

public interface SignUpCallback {

    void onSuccess(@NonNull Boolean result);

    void onFailure(@NonNull Throwable t);
}
