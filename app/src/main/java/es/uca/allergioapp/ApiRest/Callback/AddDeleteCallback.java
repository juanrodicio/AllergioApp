package es.uca.allergioapp.ApiRest.Callback;

public interface AddDeleteCallback {

    void onSuccess(Boolean result);

    void onFailure(Throwable t);
}
