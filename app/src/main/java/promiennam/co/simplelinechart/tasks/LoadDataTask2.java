package promiennam.co.simplelinechart.tasks;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import promiennam.co.simplelinechart.Constants;
import promiennam.co.simplelinechart.interfaces.ILoadDataListener;
import promiennam.co.simplelinechart.models.Portfolio;

/**
 * Created by Phuong on 16-May-17.
 */

public class LoadDataTask2 {

    private ILoadDataListener mCallback;

    public LoadDataTask2(ILoadDataListener callback) {
        mCallback = callback;
    }

    public void load() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference(Constants.PORTFOLIOS_BY_DAY);
        if (databaseReference != null) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Portfolio> portfolioList =  new ArrayList<>();

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Portfolio portfolio = data.getValue(Portfolio.class);
                        portfolioList.add(portfolio);
                    }
                    if (mCallback != null) {
                        mCallback.onComplete(portfolioList);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
