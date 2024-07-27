package org.languagetool.clientexample.dynamic;

import org.languagetool.Language;
import org.languagetool.Languages;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public final class LanguageManager {

    private static final Set<String> LT_LANGUAGE_CLASSES = new HashSet<>();

    private LanguageManager() {}

    public static void registerLTLanguage(String fqcn) {
        LT_LANGUAGE_CLASSES.add(fqcn);
    }

    static Language getLTLanguage(String omLang, String omCountry) {
        if (omLang == null) {
            return null;
        }
        // Search for full xx-YY match
        for (String fqcn : LT_LANGUAGE_CLASSES) {
            Language ltLang = Languages.getOrAddLanguageByClassName(fqcn);
            if (omLang.equalsIgnoreCase(ltLang.getShortCode())) {
                List<String> countries = Arrays.asList(ltLang.getCountries());
                if (countries.contains(omCountry)) {
                    return ltLang;
                }
            }
        }

        // Search for just xx match
        for (String fqcn : LT_LANGUAGE_CLASSES) {
            Language ltLang = Languages.getOrAddLanguageByClassName(fqcn);
            if (omLang.equalsIgnoreCase(ltLang.getShortCode())) {
                return ltLang;
            }
        }
        return null;
    }
}
