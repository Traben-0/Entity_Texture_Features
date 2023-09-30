package traben.entity_texture_features.property_reading.properties.generic_properties;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.entity_handlers.ETFEntity;
import traben.entity_texture_features.property_reading.properties.RandomProperty;

import java.util.regex.Matcher;

import static traben.entity_texture_features.property_reading.ETFTexturePropertiesUtils.groupByQuotationPattern;

public abstract class StringArrayOrRegexProperty extends RandomProperty {


//    protected GenericStringArrayOrRegexProperty(String[] array) throws RandomPropertyException {
//
//        if(array == null || array.length == 0) throw new RandomPropertyException(getPropertyId() + " property was broken");
//        ARRAY = new ObjectOpenHashSet<String>();
//        for (String str:
//             array) {
//            ARRAY.add(shouldForceLowerCaseCheck() ? str.toLowerCase() : str);
//        }
//    }
    protected StringArrayOrRegexProperty(String string) throws RandomPropertyException {
        if(string == null || string.isBlank())
            throw new RandomPropertyException(getPropertyId() + " property was broken");
        if(string.startsWith("regex:") || string.startsWith("pattern:")
            ||string.startsWith("iregex:") || string.startsWith("ipattern:")){
            MATCHER = getStringMatcher_Regex_Pattern_List_Single(string);
            ARRAY = ObjectOpenHashSet.of(string);
        }else {
            String[] array = string.trim().split("\\s+");

            if (array.length == 0)
                throw new RandomPropertyException(getPropertyId() + " property was broken");

            ARRAY = new ObjectOpenHashSet<String>();
            for (String str :
                    array) {
                ARRAY.add(shouldForceLowerCaseCheck() ? str.toLowerCase() : str);
            }
            MATCHER = ARRAY::contains;
        }
    }
    protected final ObjectOpenHashSet<String> ARRAY;
    protected final RegexAndPatternPropertyMatcher MATCHER;


    @Override
    public boolean testEntityInternal(ETFEntity entity, boolean isUpdate, Object2BooleanOpenHashMap<String> spawnConditions){

        String entityString = getValueFromEntity(entity);
        if(entityString != null) {
            if (!isPropertyUpdatable()) {
                spawnConditions.put(getPropertyId(), MATCHER.testString(shouldForceLowerCaseCheck() ? entityString.toLowerCase() : entityString));
            }
            return MATCHER.testString(shouldForceLowerCaseCheck() ? entityString.toLowerCase() : entityString);
        }
        return false;
    }



    protected abstract boolean shouldForceLowerCaseCheck();

    @Nullable
    protected abstract String getValueFromEntity(ETFEntity entity);


    private interface RegexAndPatternPropertyMatcher {
        boolean testString(String currentEntityValue);
    }

    @Nullable
    private static StringArrayOrRegexProperty.RegexAndPatternPropertyMatcher getStringMatcher_Regex_Pattern_List_Single(@Nullable String propertyLineToBeMatchedPossiblyRegex) {
        if (propertyLineToBeMatchedPossiblyRegex == null || propertyLineToBeMatchedPossiblyRegex.isBlank())
            return null;
        String stringToMatch = propertyLineToBeMatchedPossiblyRegex.trim();
        boolean invert;
        //boolean check = false;
        //should not happen in nbt
        if (stringToMatch.startsWith("!")) {
            stringToMatch = stringToMatch.replaceFirst("!", "");
            invert = true;
        } else {
            invert = false;
        }

        if (stringToMatch.contains("regex:")) {
            if (stringToMatch.contains("iregex:")) {
                stringToMatch = stringToMatch.replaceFirst("iregex:", "");
                String finalStringToMatch = stringToMatch;
                return (string) -> invert != string.matches("(?i)" + finalStringToMatch);
            } else {
                stringToMatch = stringToMatch.replaceFirst("regex:", "");
                String finalStringToMatch = stringToMatch;
                return (string) -> invert != string.matches(finalStringToMatch);
            }
        } else if (stringToMatch.contains("pattern:")) {
            stringToMatch = "\\Q" + stringToMatch;
            stringToMatch = stringToMatch.replace("*", "\\E.*\\Q").replace("?", "\\E.+\\Q");
            if (stringToMatch.contains("ipattern:")) {
                stringToMatch = stringToMatch.replace("ipattern:", "");
                String finalStringToMatch = stringToMatch;
                return (string) -> invert != string.matches("(?i)" + finalStringToMatch);
            } else {
                stringToMatch = stringToMatch.replace("pattern:", "");
                String finalStringToMatch = stringToMatch;
                return (string) -> invert != string.matches(finalStringToMatch);
            }
        } else {//direct comparison
            String finalStringToMatch1 = stringToMatch;
            boolean finalDoPattern = finalStringToMatch1.contains("\"");
            String[] finalSplitMatches = stringToMatch.split("\\s+");
            return (string) -> {
                boolean check = false;
                if (string.equals(finalStringToMatch1)) {
                    check = true;
                } else {
                    for (String singleValue : finalSplitMatches) {
                        if (string.equals(singleValue)) {
                            check = true;
                            break;
                        }
                    }
                    //if still needed try a quotation check cause why not
                    if (finalDoPattern && !check) {
                        Matcher m = groupByQuotationPattern.matcher(finalStringToMatch1);
                        while (m.find()) {
                            String foundInBrackets = m.group(1).replace("\"", "").trim();
                            if (string.equals(foundInBrackets)) {
                                check = true;
                                break;
                            }
                        }
                    }
                }
                return invert != check;
            };
        }
    }


}
