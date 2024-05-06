package traben.entity_texture_features.features.property_reading.properties.optifine_properties;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.SimpleIntegerArrayProperty;
import traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty;
import traben.entity_texture_features.utils.ETFEntity;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;

import static traben.entity_texture_features.features.property_reading.properties.generic_properties.StringArrayOrRegexProperty.getStringMatcher_Regex_Pattern_List_Single;

public class NBTProperty extends RandomProperty {

    private final Map<String, String> NBT_MAP;

    protected NBTProperty(Properties properties, int propertyNum) throws RandomPropertyException {

        final String keyPrefix = "nbt." + propertyNum + '.';
        NBT_MAP = new Object2ObjectLinkedOpenHashMap<>();
        properties.forEach((key, value) -> {
            if (key != null && ((String) key).startsWith(keyPrefix)) {
                String nbtName = ((String) key).replaceFirst(keyPrefix, "");
                String instruction = ((String) value).trim();
                if (!nbtName.isBlank() && !instruction.isBlank())
                    NBT_MAP.put(nbtName, instruction);
            }
        });
        if (NBT_MAP.isEmpty()) throw new RandomPropertyException("NBT failed");
    }

    public static NBTProperty getPropertyOrNull(Properties properties, int propertyNum) {
        try {
            return new NBTProperty(properties, propertyNum);
        } catch (RandomPropertyException e) {
            return null;
        }
    }

    private static boolean isStringValidInt(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            // e.printStackTrace();
            return false;
        }
    }

    @Override
    protected boolean testEntityInternal(ETFEntity entity) {

        CompoundTag entityNBT;
        if (entity instanceof Entity internal) {
            entityNBT = NbtPredicate.getEntityTagToCompare(internal);
        } else {
            entityNBT = entity.etf$writeNbt(new CompoundTag());
        }

        boolean doesEntityMeetThisCaseTest = true;
        if (!entityNBT.isEmpty()) {
            for (Map.Entry<String, String> nbtPropertyEntry : NBT_MAP.entrySet()) {

                String nbtIdentifier = nbtPropertyEntry.getKey();
                String nbtTestInstruction = nbtPropertyEntry.getValue();

                boolean invertFinalResult = nbtTestInstruction.startsWith("!");
                nbtTestInstruction = nbtTestInstruction.replaceFirst("!", "");

                if (nbtTestInstruction.startsWith("print:")) {
                    ETFUtils2.logMessage("NBT entity data print: \n" + entityNBT.getAsString());
                    nbtTestInstruction = nbtTestInstruction.replaceFirst("print:", "");
                }

                //first find the required nbt data
                Tag finalNBTElementOrNullIfFailed = null;
                boolean listIndexInstructionWasWildCard = false;
                Tag lastIterationNBTElement = entityNBT;
                Iterator<String> nbtPathInstructionIterator = Arrays.stream(nbtIdentifier.split("\\.")).iterator();
                while (nbtPathInstructionIterator.hasNext()) {
                    if (lastIterationNBTElement == null) {
                        ETFUtils2.logWarn("null nbt in ETF");
                        break;
                    }
                    String nextPathInstruction = nbtPathInstructionIterator.next();

                    //find out how to handle this instruction based on what element we have
                    if (lastIterationNBTElement instanceof CompoundTag nbtCompound) {
                        if (nbtCompound.contains(nextPathInstruction)) {
                            lastIterationNBTElement = nbtCompound.get(nextPathInstruction);
                        } else {
                            //not found so break
                            break;
                        }
                    } else if (lastIterationNBTElement instanceof CollectionTag<?> nbtList) {
                        if ("*".equals(nextPathInstruction)) {
                            listIndexInstructionWasWildCard = true;
                        } else if (isStringValidInt(nextPathInstruction)) {
                            //possibly further nested elements to read from
                            try {
                                int index = Integer.parseInt(nextPathInstruction);
                                lastIterationNBTElement = nbtList.get(index);
                            } catch (IndexOutOfBoundsException e) {
                                break;
                            }

                        } else {
                            ETFUtils2.logWarn("cannot parse list index of [" + nextPathInstruction + "] in nbt property: " + nbtIdentifier);
                            break;
                        }
                    } else {
                        //here this means we have an nbt element without children yet have received an additional instruction???
                        //throw a fit if there are further instructions
                        ETFUtils2.logError("cannot parse next nbt instruction of [" + nextPathInstruction + "] in nbt property: " + nbtIdentifier + ", as this nbt is not a list or compound and cannot have further instructions");
                        break;

                    }
                    //here if there are no further instructions then send the final result
                    if (!nbtPathInstructionIterator.hasNext()) {
                        finalNBTElementOrNullIfFailed = lastIterationNBTElement;
                    }
                }

                boolean doesTestPass = false;

                //test if was found
                if (finalNBTElementOrNullIfFailed != null) {
                    if (nbtTestInstruction.startsWith("print_raw:")) {
                        String rawStringFromNBT = finalNBTElementOrNullIfFailed.getAsString();
                        String rawMatchString = nbtTestInstruction.replaceFirst("print_raw:", "");
                        ETFUtils2.logMessage("NBT RAW data of: " + nbtIdentifier + "=" + rawStringFromNBT);
                        StringArrayOrRegexProperty.RegexAndPatternPropertyMatcher matcher = getStringMatcher_Regex_Pattern_List_Single(rawMatchString);
                        doesTestPass = matcher != null && matcher.testString(rawStringFromNBT);
                        //doesTestPass = rawMatchString.equals(rawStringFromNBT);
                    } else if (nbtTestInstruction.startsWith("raw:")) {
                        String rawStringFromNBT = finalNBTElementOrNullIfFailed.getAsString();
                        String rawMatchString = nbtTestInstruction.replaceFirst("raw:", "");
                        StringArrayOrRegexProperty.RegexAndPatternPropertyMatcher matcher = getStringMatcher_Regex_Pattern_List_Single(rawMatchString);
                        doesTestPass = matcher != null && matcher.testString(rawStringFromNBT);
                        //doesTestPass = rawMatchString.equals(rawStringFromNBT);
                    } else if (nbtTestInstruction.startsWith("exists:")) {
                        doesTestPass = nbtTestInstruction.contains("exists:true");
                    } else if (nbtTestInstruction.startsWith("range:")) {
                        if (finalNBTElementOrNullIfFailed instanceof NumericTag nbtNumber) {
                            String rawRangeString = nbtTestInstruction.replaceFirst("range:", "");
                            SimpleIntegerArrayProperty.IntRange range = SimpleIntegerArrayProperty.getIntRange(rawRangeString);
                            doesTestPass = range.isWithinRange(nbtNumber.getAsNumber().intValue());
                        } else {
                            ETFUtils2.logWarn("NBT range is not valid for non number nbt types: " + nbtIdentifier + "=" + nbtTestInstruction);
                        }
                        // }else  if (finalNBTElementOrNullIfFailed instanceof NbtCompound nbtCompound) {
                    } else if (finalNBTElementOrNullIfFailed instanceof CollectionTag<?> nbtList) {
                        if (listIndexInstructionWasWildCard) {
                            for (Tag element :
                                    nbtList) {
                                StringArrayOrRegexProperty.RegexAndPatternPropertyMatcher matcher = getStringMatcher_Regex_Pattern_List_Single(nbtTestInstruction);
                                doesTestPass = matcher != null && matcher.testString(element.getAsString());
                                if (doesTestPass) break;
                            }
                        } else {
                            ETFUtils2.logWarn("NBT list error with: " + nbtIdentifier + "=" + nbtTestInstruction);
                        }
//                            }else if(finalNBTElementOrNullIfFailed instanceof AbstractNbtNumber nbtNumber) {
//                                doesTestPass = doesStringMatch(nbtTestInstruction,nbtNumber.asString());
//                            }else if(finalNBTElementOrNullIfFailed instanceof NbtString nbtString) {
//                                doesTestPass = doesStringMatch(nbtTestInstruction,nbtString.asString());
                    } else if (finalNBTElementOrNullIfFailed instanceof NumericTag) {
                        //strip non digit chars
                        StringArrayOrRegexProperty.RegexAndPatternPropertyMatcher matcher = getStringMatcher_Regex_Pattern_List_Single(nbtTestInstruction);
                        doesTestPass = matcher != null && matcher.testString(finalNBTElementOrNullIfFailed.getAsString().replaceAll("[^\\d.]", ""));
                    } else {
                        StringArrayOrRegexProperty.RegexAndPatternPropertyMatcher matcher = getStringMatcher_Regex_Pattern_List_Single(nbtTestInstruction);
                        doesTestPass = matcher != null && matcher.testString(finalNBTElementOrNullIfFailed.getAsString());
                    }
                } else {
                    //did not find
                    if (nbtTestInstruction.startsWith("print_raw:")) {
                        String rawStringFromNBT = "";
                        String rawMatchString = nbtTestInstruction.replaceFirst("print_raw:", "");
                        ETFUtils2.logMessage("NBT RAW data of: " + nbtIdentifier + "=" + rawStringFromNBT);
                        doesTestPass = rawMatchString.equals(rawStringFromNBT);
                    } else if (nbtTestInstruction.startsWith("raw:")) {
                        String rawStringFromNBT = "";
                        String rawMatchString = nbtTestInstruction.replaceFirst("raw:", "");
                        doesTestPass = rawMatchString.equals(rawStringFromNBT);
                    } else if (nbtTestInstruction.startsWith("exists:")) {
                        doesTestPass = nbtTestInstruction.contains("exists:false");
                    }
                }
                //simplified from invertFinalResult? !doesTestPass : doesTestPass;
                doesEntityMeetThisCaseTest = invertFinalResult != doesTestPass;
                if (!doesEntityMeetThisCaseTest) break;
            }
            return doesEntityMeetThisCaseTest;
        } else {
            ETFUtils2.logError("NBT test failed, as could not read entity NBT");
        }
        return false;
    }


    @Override
    public @NotNull String[] getPropertyIds() {
        return new String[]{"nbt"};
    }

    @Override
    protected String getPrintableRuleInfo() {
        return null;
    }
}
