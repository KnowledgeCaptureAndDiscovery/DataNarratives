package Elements;

/**
 * @author dgarijo
 */
public class Resource {
    String name;
    String uri;
    String description; //optional. The description is a general description of the resource
    String location; //optional. The location indicates where is the file for this resource physically stored.
    String value; //optional. The resources may have an associated value.

    public Resource() {
    }

    public Resource(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    
    
}
