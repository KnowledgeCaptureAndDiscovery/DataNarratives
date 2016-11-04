package Elements;

import java.util.ArrayList;

/**
 * @author dgarijo
 */
public class Step extends Resource{
    //sometimes it may be useful to have  the step names, code and uri on a single class.
    //motif as well
    // this way it is easier to initialize them from the KB
    String scriptLocation;
    String codeLocation;
    ArrayList<String> motifs;//motifs owned by the Step

    public Step() {
    }
    
    public Step(String name, String uri) {
        super(name, uri);
    }

    @Override
    public String getLocation() {
        return scriptLocation;
    }

    @Override
    public void setLocation(String location) {
        this.scriptLocation = location;
    }

    public String getCodeLocation() {
        return codeLocation;
    }

    public void setCodeLocation(String codeLocation) {
        this.codeLocation = codeLocation;
    }

    public ArrayList<String> getMotifs() {
        return motifs;
    }

    public void setMotifs(ArrayList<String> motifs) {
        this.motifs = motifs;
    }
    
    
    
    
}
