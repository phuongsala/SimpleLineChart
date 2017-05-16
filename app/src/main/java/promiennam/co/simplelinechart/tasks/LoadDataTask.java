package promiennam.co.simplelinechart.tasks;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import promiennam.co.simplelinechart.interfaces.ILoadDataListener;
import promiennam.co.simplelinechart.models.Portfolio;

/**
 * Created by Phuong on 15-May-17.
 */

public class LoadDataTask extends AsyncTask<InputStream, Void, List<Portfolio>> {

    private ILoadDataListener mCallback;

    public LoadDataTask(ILoadDataListener callback) {
        mCallback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<Portfolio> doInBackground(InputStream... inputStreams) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int index;
        try {
            index = inputStreams[0].read();
            while (index != -1) {
                output.write(index);
                index = inputStreams[0].read();
            }
            inputStreams[0].close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (output.toString() != null) {
            return new Gson().fromJson(output.toString(), new TypeToken<ArrayList<Portfolio>>() {
            }.getType());
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Portfolio> portfolioList) {
        super.onPostExecute(portfolioList);
        if (mCallback != null) {
            mCallback.onCompleted(portfolioList);
        }
    }
}