package promiennam.co.simplelinechart.interfaces;

import java.util.List;

import promiennam.co.simplelinechart.models.Portfolio;

/**
 * Created by Phuong on 15-May-17.
 */

public interface ILoadDataListener {

    void onCompleted(List<Portfolio> portfolioList);

    void onError();
}
