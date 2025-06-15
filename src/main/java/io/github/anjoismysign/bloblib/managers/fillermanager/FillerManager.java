package io.github.anjoismysign.bloblib.managers.fillermanager;

import io.github.anjoismysign.bloblib.entities.VariableFiller;

import java.util.HashMap;

public class FillerManager {
    private final HashMap<String, VariableFiller<?>> variableFillers;

    public FillerManager() {
        variableFillers = new HashMap<>();
        variableFillers.put("Material", new MaterialFiller());
        variableFillers.put("ItemMaterial", new ItemMaterialFiller());
        variableFillers.put("BlockMaterial", new BlockMaterialFiller());
        variableFillers.put("SpawnableEntityType", new SpawnableEntityTypeFiller());
    }

    public MaterialFiller getMaterialFiller() {
        return (MaterialFiller) variableFillers.get("Material");
    }

    public ItemMaterialFiller getItemMaterialFiller() {
        return (ItemMaterialFiller) variableFillers.get("ItemMaterial");
    }

    public BlockMaterialFiller getBlockMaterialFiller() {
        return (BlockMaterialFiller) variableFillers.get("BlockMaterial");
    }

    public SpawnableEntityTypeFiller getSpawnableEntityTypeFiller() {
        return (SpawnableEntityTypeFiller) variableFillers.get("SpawnableEntityType");
    }
}