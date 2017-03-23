package ictsimulationpackage;

/**
 * Created by jaga on 2/10/17.
 */
public enum Path {
    SIMULATION_HOME("/Users/jaga/Documents/domain_project/"),
    DATA_PATH(SIMULATION_HOME.get() + "data/"),
    OUTPUT_PATH(DATA_PATH.get() + "output/");

    private String path;

    Path(String path) {
        this.path = path;
    }

    public String get() {
        return path;
    }
}
