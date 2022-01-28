package traben.entity_texture_features.client;


import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static traben.entity_texture_features.client.entity_texture_features_CLIENT.*;

public class randomCase {
    //Integer[] suffixes;
    //Integer[] weights;
    private Integer[] weightedSuffixes = {};
    private String[] biomes= {};
    private Integer[] heights= {};
    private String[] names= {};

    randomCase(Integer[] suffixesX,
               Integer[] weightsX,
               String[] biomesX,
                Integer[] heightsX,
                String[] namesX
               // String[] professions,
               // String[] collarColours,
               // boolean baby
    ){
        //Integer[] suffixes = suffixesX;
        //Integer[] weights = weightsX;
        biomes=biomesX;
        heights=heightsX;
        names=namesX;

        if (weightsX.length > 0) {
            if (weightsX.length == suffixesX.length) {
                ArrayList<Integer> buildWeighted = new ArrayList<>();
                int index = 0;
                for (int suffix :
                        suffixesX) {
                    for (int i = 0; i < weightsX[index]; i++) {
                        //adds the suffix as many times as it is weighted
                        buildWeighted.add(suffix);
                    }
                    index++;
                }
                weightedSuffixes = buildWeighted.toArray(new Integer[0]);

            }else{
                System.out.println("Entity Texture Features - random texture weights dont match");
                weightedSuffixes = suffixesX;
            }
        }else{

            weightedSuffixes = suffixesX;
        }
    }

    @Override
    public String toString() {
        return "randomCase{" +
                "weightedSuffixes=" + Arrays.toString(weightedSuffixes) +
                ", biomes=" + Arrays.toString(biomes) +
                ", heights=" + Arrays.toString(heights) +
                ", names=" + Arrays.toString(names) +
                '}';
    }

    public boolean testEntity(Entity entity){
        if (biomes.length == 0 && names.length == 0 && heights.length == 0){return true;}
        boolean allBoolean = true;
        if (biomes.length > 0){
            String entityBiome = entity.world.getBiome(entity.getBlockPos()).getCategory().getName();//has no caps// desert
            boolean check = false;
            for (String str:
                 biomes) {
                if (str.toLowerCase().equals(entityBiome)){
                    check  = true;
                    break;
                }
            }
            allBoolean = check;
        }
        if (allBoolean && names.length > 0){
            String entityName = entity.getName().asString();
            boolean check = false;
            for (String str:
                    names) {
                if (str.equals(entityName)){
                    check  = true;
                    break;
                }
            }
            allBoolean = allBoolean && check;
        }
        if (allBoolean && heights.length > 0){
            int entityHeight = entity.getBlockY();
            boolean check = false;
            for (int i:
                    heights) {
                if (i == entityHeight){
                    check  = true;
                    break;
                }
            }
            allBoolean = allBoolean && check;
        }
        return allBoolean;
    }
    public int getWeightedSuffix(UUID id){
        int randomReliable = id.hashCode() > 0 ? id.hashCode() : -id.hashCode();
        randomReliable %= weightedSuffixes.length;
        return weightedSuffixes[randomReliable];
    }
}
