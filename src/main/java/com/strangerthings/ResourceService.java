package com.strangerthings;

public class ResourceService {

    public boolean isValidResource(
            String name,
            String description,
            String category,
            String collectionMethod) {

        return name != null && !name.isBlank()
                && description != null && !description.isBlank()
                && category != null && !category.isBlank()
                && collectionMethod != null && !collectionMethod.isBlank();
    }
}