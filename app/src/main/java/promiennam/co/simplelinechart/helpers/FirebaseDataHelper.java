package promiennam.co.simplelinechart.helpers;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import promiennam.co.simplelinechart.models.Portfolio;
import promiennam.co.simplelinechart.tasks.SaveDataTask;

/**
 * Created by Phuong on 16-May-17.
 */

public class FirebaseDataHelper {

    private static final String TAG = FirebaseDataHelper.class.getSimpleName();

    private DatabaseReference mDatabase;

    public FirebaseDataHelper() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void savePortfolios(List<Portfolio> portfolioList) {
        if (portfolioList != null) {
            new SaveDataTask(mDatabase).execute(portfolioList);
        }
    }
}
