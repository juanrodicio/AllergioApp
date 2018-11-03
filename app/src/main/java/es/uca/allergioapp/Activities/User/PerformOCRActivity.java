package es.uca.allergioapp.Activities.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import es.uca.allergioapp.ApiRest.AllergioApiClient;
import es.uca.allergioapp.ApiRest.Callback.IngredientAllergyCallback;
import es.uca.allergioapp.AsyncTasks.TextOCR;
import es.uca.allergioapp.R;

public class PerformOCRActivity extends AppCompatActivity {

    ImageView imagePreview;
    TextOCR mTextOCR;
    AlertDialog dialog;
    String OCRresult = null;
    Bitmap image;
    TextRecognizer textRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perform_ocr);

        if (Build.VERSION.SDK_INT >= 24) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary, getTheme()));
        }

        imagePreview = findViewById(R.id.imagePreview);

        textRecognizer = new TextRecognizer.Builder(this).build();

        if (!textRecognizer.isOperational()) {
            Log.w("PerfomOCRActivity", "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w("PerformOCRActivity", getString(R.string.low_storage_error));
            }
        }

        Uri imageUri = Uri.parse(getIntent().getStringExtra("uriImage"));
        imagePreview.setImageURI(imageUri);
        try {
            image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void goBack(View view) {
        finish();
    }

    public void processImage(View view) {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Procesando imagen...")
                .setCancelable(false)
                .build();
        dialog.show();

        final Activity mContext = this;

        mTextOCR = new TextOCR(textRecognizer, result -> {
            OCRresult = limpiarEntrada(result);

            AllergioApiClient apiClient = new AllergioApiClient();
            apiClient.invokeConvertIngredients(OCRresult, new IngredientAllergyCallback() {
                @Override
                public void onSuccess(@NonNull List<String> ingredients) {
                    Intent listScannedIngredients = new Intent(mContext, ListIngredientsActivity.class);
                    listScannedIngredients.putExtra("ingredients", new ArrayList<>(ingredients));
                    startActivity(listScannedIngredients);
                    if (dialog.isShowing())
                        dialog.dismiss();
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    dialog.dismiss();
                    Log.e("FAIL-RESPONSE", "onFailure throwed");
                }
            });
        });
        mTextOCR.execute(image);

    }

    private String limpiarEntrada(String result) {
        result = result.toLowerCase();
        result = result.replaceAll("[0-9]*((,[0-9]*)?%)?", "");
        result = result.replaceAll("\n", " ");
        result = result.replaceAll("contiene", "");
        result = result.replaceAll("puede contener trazas de", "");
        result = result.replaceAll("ingredientes:", "");
        result = result.replace("(", ",");
        result = result.replace(")", "");
        result = result.replace(".", ",");
        result = result.replaceAll(" y ", ",");
        result = result.replaceAll(",,", ",");
        return result;
    }
}
