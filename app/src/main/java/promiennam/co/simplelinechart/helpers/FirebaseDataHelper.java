package promiennam.co.simplelinechart.helpers;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import promiennam.co.simplelinechart.interfaces.ISaveDataListener;
import promiennam.co.simplelinechart.models.Portfolio;
import promiennam.co.simplelinechart.tasks.SaveDataTask;

/**
 * Created by Phuong on 16-May-17.
 */

public class FirebaseDataHelper implements ISaveDataListener{

    private static final String TAG = FirebaseDataHelper.class.getSimpleName();

    private DatabaseReference mDatabase;

    public FirebaseDataHelper() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void savePortfolios(List<Portfolio> portfolioList) {
        if (portfolioList != null) {
            new SaveDataTask(mDatabase, this).execute(portfolioList);
        }
    }

    @Override
    public void onSuccess() {
        Log.d(TAG, "save data to firebase successfully");
    }

    @Override
    public void onError() {

    }
}
