package promiennam.co.simplelinechart.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Phuong on 13-May-17.
 */

public class Portfolio {

    @SerializedName("portfolioId")
    private String id;

    @SerializedName("navs")
    private List<Nav> navList;

    public Portfolio() {
    }

    public Portfolio(String id, List<Nav> navList) {
        this.id = id;
        this.navList = navList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Nav> getNavList() {
        return navList;
    }

    public void setNavList(List<Nav> navList) {
        this.navList = navList;
    }
}
