package es.uca.allergioapp.AsyncTasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import es.uca.allergioapp.ApiRest.Callback.OnTessOCRCompleted;

/**
 * Created by Juan on 13/07/2018.
 */

public class TextOCR extends AsyncTask<Bitmap, Integer, String> {
    private final TextRecognizer textRecognizer;
    private OnTessOCRCompleted listener;

    public TextOCR(TextRecognizer textRecognizer, OnTessOCRCompleted listener) {
        this.textRecognizer = textRecognizer;
        this.listener = listener;
    }


    @Override
    protected String doInBackground(Bitmap... params) {
        return getOCRResult(params[0]);
    }

    private String getOCRResult(Bitmap bitmap) {
        StringBuilder ingredients = new StringBuilder();
        Frame outputFrame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<TextBlock> items = textRecognizer.detect(outputFrame);
        for (int i = 0; i < items.size(); i++) {
            TextBlock item = items.valueAt(i);
            if (item != null && item.getValue() != null) {
                Log.d("Processor", "Text detected! " + item.getValue());
                ingredients.append(item.getValue());
            }
        }
        return ingredients.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        listener.onTaskCompleted(result);
    }
}
