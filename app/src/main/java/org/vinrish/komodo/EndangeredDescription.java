package org.vinrish.komodo;

public class EndangeredDescription {
    private String description;

    public EndangeredDescription() {
        // Default constructor needed for Firestore
    }

    public EndangeredDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
