package es.uca.allergioapp.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import es.uca.allergioapp.POJOs.PreferenceManager;
import es.uca.allergioapp.R;

public class LogoutFragment extends DialogFragment {

    Activity mActivity;
    PreferenceManager preferenceManager;

    public Activity getmActivity() {
        return mActivity;
    }

    public void setmActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        preferenceManager = new PreferenceManager(getmActivity());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_MaterialComponents_Dialog_Alert);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_logout, null);

        builder.setView(dialogView)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        preferenceManager.setLogged(false);
                        mActivity.finishAffinity();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        return builder.create();
    }
}
