package org.languagetool.clientexample.dynamic;

import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.Languages;
import org.languagetool.rules.RuleMatch;

import java.io.IOException;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        loadLanguageModule();
       // Example of usage
        System.out.println("This example will test a short string with dynamic loaded languages.");
        List<Language> realLanguages = Languages.get();
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

    private static Path getModuleDir() {
        Path path = null;
        try {
            URI sourceUri = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            if (sourceUri.getScheme().equals("file")) {
                Path uriPath = Paths.get(sourceUri);
                if (uriPath.endsWith(".jar")) {
                    if (uriPath.getParent().endsWith("libs")
                            && uriPath.getParent().getParent().resolve("classes").toFile().exists()) {
                        // a. assumes developer launch
                        path = uriPath.getParent().getParent().resolve("modules");
                    } else if (uriPath.getParent().resolve("modules").toFile().exists()) {
                        // b. assumes standard installation
                        path = uriPath.getParent().resolve("modules");
                    }
                } else if (uriPath.endsWith("main")) {
                    // c. assumes run Main class from IDE
                    path = uriPath.getParent().getParent().getParent().resolve("modules");
                }
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        if (path == null) {
            path = Paths.get(".", "modules");
        }
        return path;
    }

    private static void loadLanguageModule() throws MalformedURLException {
        Path moduleDir = getModuleDir();
        URL[] urls = new URL[1];
        urls[0] = moduleDir.resolve("language-en.jar").toFile().toURI().toURL();
        // build custom class loader for dynamic loading of language-* module.
        URLClassLoader customClassLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
        // register LT languages
        LanguageManager.registerLTLanguage("org.languagetool.language.English");
        LanguageManager.registerLTLanguage("org.languagetool.language.AustralianEnglish");
        LanguageManager.registerLTLanguage("org.languagetool.language.BritishEnglish");
        LanguageManager.registerLTLanguage("org.languagetool.language.AmericanEnglish");
        // register LT brokers
        JLanguageTool.setClassBrokerBroker(new LanguageClassBroker(customClassLoader));
        JLanguageTool.setDataBroker(new LanguageDataBroker(customClassLoader));
        // load all registered languages
        LanguageManager.getLTLanguage("xx", null);
    }

}