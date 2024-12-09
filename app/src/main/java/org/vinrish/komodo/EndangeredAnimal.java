package org.vinrish.komodo;

public class EndangeredAnimal {
    private String name;
    private int population;
    private String imageUrl;
    private String documentId;

    public EndangeredAnimal(String documentId, String name, int population, String imageUrl) {
        this.documentId = documentId;
        this.name = name;
        this.population = population;
        this.imageUrl = imageUrl;
    }

    public EndangeredAnimal(String name, int population, String imageUrl) {
        this.name = name;
        this.population = population;
        this.imageUrl = imageUrl;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getName() {
        return name;
    }

    public int getPopulation() {
        return population;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

