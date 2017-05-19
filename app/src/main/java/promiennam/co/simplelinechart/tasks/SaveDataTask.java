package promiennam.co.simplelinechart.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import promiennam.co.simplelinechart.enums.ChartViewType;
import promiennam.co.simplelinechart.interfaces.ISaveDataListener;
import promiennam.co.simplelinechart.models.Portfolio;

/**
 * Created by Phuong on 16-May-17.
 */

public class SaveDataTask extends AsyncTask<List<Portfolio>, Void, Void> {

    private static final String TAG = SaveDataTask.class.getSimpleName();

    private ISaveDataListener mCallback;

    public SaveDataTask(ISaveDataListener callback) {
        mCallback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "prepare save data");
    }

    @Override
    protected Void doInBackground(List<Portfolio>... portfolioList) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("portfolios_by_day");

        if (databaseReference != null) {
            // just store all portfolio except the total
            ArrayList<Portfolio> needSavingPortfolioList = new ArrayList<>();
            for (int i = 0; i < portfolioList[0].size() - 1; i++) {
                needSavingPortfolioList.add(new Portfolio(portfolioList[0].get(i).getPortfolioId(),
                        portfolioList[0].get(i).getNavs()));
            }
            databaseReference.setValue(needSavingPortfolioList);
        }

        if (mCallback != null) {
            mCallback.onSuccess();
        }

        return null;
    }
}
