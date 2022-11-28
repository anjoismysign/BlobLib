package us.mytheria.bloblib.managers.fillermanager;

import java.util.HashMap;

public class FillerManager {
    private final HashMap<String, Object> variableFillers;

    public FillerManager() {
        variableFillers = new HashMap<>();
        variableFillers.put("Material", new MaterialFiller());
        variableFillers.put("BlockMaterial", new BlockMaterialFiller());
        variableFillers.put("SpawnableEntityType", new SpawnableEntityTypeFiller());
    }

    public MaterialFiller getMaterialFiller() {
        return (MaterialFiller) variableFillers.get("Material");
    }

    public BlockMaterialFiller getBlockMaterialFiller() {
        return (BlockMaterialFiller) variableFillers.get("BlockMaterial");
    }

    public SpawnableEntityTypeFiller getSpawnableEntityTypeFiller() {
        return (SpawnableEntityTypeFiller) variableFillers.get("SpawnableEntityType");
    }
}