package org.languagetool.clientexample.dynamic;

import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.Languages;
import org.languagetool.rules.RuleMatch;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        // Please construct URL of language-en.jar.
        URL[] urls = new URL[1];
        urls[0] = Paths.get("modules", "language-en.jar").toFile().toURI().toURL();
        // build custom class loader for dynamic loading of language-* module.
        URLClassLoader customClassLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
        // register LT brokers
        JLanguageTool.setClassBrokerBroker(new LanguageToolClassBroker(customClassLoader));
        JLanguageTool.setDataBroker(new LanguageToolDataBroker(customClassLoader));
        // Example of usage
        List<Language> realLanguages = Languages.get();
        System.out.println("This example will test a short string with dynamic loaded languages.");
        System.out.println("Supported languages: " + realLanguages.size());
        for (Language language : realLanguages) {
            JLanguageTool lt = new JLanguageTool(language);
            String input = "And the the";
            List<RuleMatch> result = lt.check(input);
            System.out.println("Checking '" + input + "' with " + language + ":");
            for (RuleMatch ruleMatch : result) {
                System.out.println("    " + ruleMatch);
            }
        }

    }

}