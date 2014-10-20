package client.ui_components;

/**
 * Created by leo on 20/10/14.
 */
public class Result {
    public String username;
    public int totalShots;
    public int misses;
    public int hits;
    public boolean won;

    public Result(String username, int totalShots, int misses, int hits, boolean won) {
        this.username = username;
        this.totalShots = totalShots;
        this.misses = misses;
        this.hits = hits;
        this.won = won;
    }
}
