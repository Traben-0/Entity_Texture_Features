package traben.entity_texture_features.features.property_reading.properties.generic_properties;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.features.property_reading.properties.RandomProperty;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class StringArrayOrRegexProperty extends RandomProperty {

    protected static final Pattern GROUP_BY_QUOTATION_PATTERN = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");
    protected final String ORIGINAL_INPUT;
    protected final ObjectOpenHashSet<String> ARRAY;
    protected final RegexAndPatternPropertyMatcher MATCHER;


    protected StringArrayOrRegexProperty(String string) throws RandomPropertyException {
        ORIGINAL_INPUT = string;
        if (string == null || string.isBlank())
            throw new RandomPropertyException(getPropertyId() + " property was broken");
        if (string.startsWith("regex:") || string.startsWith("pattern:")
                || string.startsWith("iregex:") || string.startsWith("ipattern:")) {
            MATCHER = getStringMatcher_Regex_Pattern_List_Single(string);
            ARRAY = ObjectOpenHashSet.of(string);
        } else {
            String[] array = string.trim().split("\\s+");

            if (array.length == 0)
                throw new RandomPropertyException(getPropertyId() + " property was broken");

            ARRAY = new ObjectOpenHashSet<>();
            for (String str :
                    array) {
                ARRAY.add(shouldForceLowerCaseCheck() ? str.toLowerCase() : str);
            }
            MATCHER = ARRAY::contains;
        }
    }

    @Nullable
    public static StringArrayOrRegexProperty.RegexAndPatternPropertyMatcher getStringMatcher_Regex_Pattern_List_Single(@Nullable String propertyLineToBeMatchedPossiblyRegex) {
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
                        Matcher m = GROUP_BY_QUOTATION_PATTERN.matcher(finalStringToMatch1);
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

    @Override
    public boolean testEntityInternal(ETFEntity entity) {

        String entityString = getValueFromEntity(entity);
        if (entityString != null) {
            return MATCHER.testString(shouldForceLowerCaseCheck() ? entityString.toLowerCase() : entityString);
        }
        return false;
    }

    protected abstract boolean shouldForceLowerCaseCheck();

    @Nullable
    protected abstract String getValueFromEntity(ETFEntity entity);

    @Override
    protected String getPrintableRuleInfo() {
        return ORIGINAL_INPUT;
    }

    public interface RegexAndPatternPropertyMatcher {
        boolean testString(String currentEntityValue);
    }
}
