package io.github.anjoismysign.bloblib.utility;

public enum DataSerializationFormat {
    YAML("yml", "yaml"),
    JSON("json");

    private final String[] fileExtensions;

    DataSerializationFormat(String... fileExtensions){
        if (fileExtensions.length == 0){
            throw new IllegalArgumentException("'fileExtensions' cannot be empty!");
        }
        this.fileExtensions = fileExtensions;
    }

    public String[] getFileExtensions(){
        return fileExtensions;
    }

    public String getDefaultExtension(){
        return fileExtensions[0];
    }

    public boolean isFileExtension(String string){
        for (String fileExtension : fileExtensions){
            if (!fileExtension.equals(string)){
                continue;
            }
            return true;
        }
        return false;
    }
}
