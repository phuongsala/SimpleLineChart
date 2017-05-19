package promiennam.co.simplelinechart.models;

import java.util.List;

/**
 * Created by Phuong on 13-May-17.
 */

public class Portfolio {

    private String portfolioId;
    private List<Nav> navs;

    public Portfolio() {
    }

    public Portfolio(String portfolioId, List<Nav> navs) {
        this.portfolioId = portfolioId;
        this.navs = navs;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public List<Nav> getNavs() {
        return navs;
    }

    public void setNavs(List<Nav> navs) {
        this.navs = navs;
    }
}
