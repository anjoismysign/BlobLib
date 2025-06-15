package io.github.anjoismysign.bloblib.entities;

import io.github.anjoismysign.anjo.entities.ConventionHelper;

public enum ObjectDirectorDataType {
    DIRECTOR,
    BUILDER,
    DIRECTORY,
    BUILER_FILE;

    public static String getConventionVariableName(String objectName, ObjectDirectorDataType type) {
        switch (type) {
            case DIRECTOR -> {
                return ConventionHelper.from(objectName).toPascalCase() + "Director";
            }
            case BUILDER -> {
                return ConventionHelper.from(objectName).toPascalCase() + "Builder";
            }
            case DIRECTORY -> {
                return ConventionHelper.from(objectName).toPascalCase() + "Directory";
            }
            case BUILER_FILE -> {
                return ConventionHelper.from(objectName).toPascalCase() + "BuilderFile";
            }
            default -> {
                return null;
            }
        }
    }
}