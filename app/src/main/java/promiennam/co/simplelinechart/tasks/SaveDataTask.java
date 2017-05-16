package promiennam.co.simplelinechart.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import promiennam.co.simplelinechart.models.Portfolio;

/**
 * Created by Phuong on 16-May-17.
 */

public class SaveDataTask extends AsyncTask<List<Portfolio>, Void, Void> {

    private static final String TAG = SaveDataTask.class.getSimpleName();

    private DatabaseReference mDatabase;

    public SaveDataTask(DatabaseReference database) {
        mDatabase = database;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "prepare save data");
    }

    @Override
    protected Void doInBackground(List<Portfolio>... portfolioList) {

        for (int i = 0; i < portfolioList[0].size(); i++) {
            Portfolio portfolio = portfolioList[0].get(i);
            // add new child
            mDatabase.child("portfolios");
            mDatabase.setValue(portfolio);

            // listener on child value changed
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Portfolio portfolio = dataSnapshot.getValue(Portfolio.class);
                    if (portfolio != null) {
                        Log.d(TAG, "portfolio " + portfolio);
                    } else {
                        Log.d(TAG, "portfolio is null");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d(TAG, "save data completed");
    }
}
