package org.languagetool.clientexample.dynamic;

import org.jetbrains.annotations.NotNull;
import org.languagetool.Language;
import org.languagetool.broker.ResourceDataBroker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class LanguageDataBroker implements ResourceDataBroker {

    private final String resourceDir;
    private final String rulesDir;
    private final URLClassLoader classLoader;

    public LanguageDataBroker(URLClassLoader classLoader, String resourceDir, String rulesDir) {
        this.classLoader = classLoader;
        this.resourceDir = resourceDir == null ? "" : resourceDir;
        this.rulesDir = rulesDir == null ? "" : rulesDir;
    }

    public LanguageDataBroker(URLClassLoader classLoader) {
        this(classLoader, ResourceDataBroker.RESOURCE_DIR, ResourceDataBroker.RULES_DIR);
    }

    @Override
    public @NotNull URL getFromResourceDirAsUrl(String path) {
        String completePath = getCompleteResourceUrl(path);
        URL resource = getAsURL(completePath);
        return Objects.requireNonNull(resource);
    }

    @Override
    public @NotNull List<URL> getFromResourceDirAsUrls(String path) {
        String completePath = getCompleteResourceUrl(path);
        List<URL> resources = getAsURLs(completePath.substring(1));
        return Objects.requireNonNull(resources);
    }

    private String getCompleteRulesUrl(String path) {
        return this.appendPath(this.rulesDir, path);
    }

    private String getCompleteResourceUrl(String path) {
        return this.appendPath(this.resourceDir, path);
    }

    @Override
    public boolean resourceExists(String path) {
        String completePath = this.getCompleteResourceUrl(path);
        return this.getAsURL(completePath) != null;
    }

    private String appendPath(String baseDir, String path) {
        StringBuilder completePath = new StringBuilder(baseDir);
        if (!rulesDir.endsWith("/") && !path.startsWith("/")) {
            completePath.append('/');
        }

        if (rulesDir.endsWith("/") && path.startsWith("/") && path.length() > 1) {
            completePath.append(path.substring(1));
        } else {
            completePath.append(path);
        }

        return completePath.toString();
    }

    @Override
    public boolean ruleFileExists(String path) {
        String completePath = this.getCompleteRulesUrl(path);
        return this.getAsURL(completePath) != null;
    }

    @Override
    public @NotNull InputStream getFromResourceDirAsStream(String path) {
        String completePath = getCompleteResourceUrl(path);
        return Objects.requireNonNull(getAsStream(completePath));
    }

    @Override
    public @NotNull List<String> getFromResourceDirAsLines(String path) {
        try (InputStream inputStram = getFromResourceDirAsStream(path);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStram, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            return bufferedReader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private @NotNull Class<? extends Language> getLanguageClass() {
        try {
            Language lang = LanguageManager.getLTLanguage("en", "US");
            if (lang != null) {
                return lang.getClass();
            }
            Class<?> clazz = classLoader.loadClass("org.languagetool.language.AmericanEnglish");
            if (Language.class.isAssignableFrom(clazz)) {
                return (Class<? extends Language>) clazz;
            }
        } catch (ClassNotFoundException ignored) {
        }
        throw new RuntimeException();
    }

    @Override
    public InputStream getAsStream(String path) {
        InputStream inputStream = ResourceDataBroker.class.getResourceAsStream(path);
        if (inputStream == null) {
            inputStream = getLanguageClass().getResourceAsStream(path);
        }
        return inputStream;
    }

    @Override
    public URL getAsURL(String path) {
        URL url = ResourceDataBroker.class.getResource(path);
        if (url == null) {
           url = getLanguageClass().getResource(path);
        }
        return url;
    }

    @Override
    public @NotNull List<URL> getAsURLs(String path) {
        Enumeration<URL> enumeration = null;
        try {
            enumeration = ResourceDataBroker.class.getClassLoader().getResources(path);
        } catch (IOException ignored) {
        }
        if (enumeration != null) {
            List<URL> urls = Collections.list(enumeration);
            if (!urls.isEmpty()) {
                return urls;
            }
        }
        try {
            enumeration = getLanguageClass().getClassLoader().getResources(path);
        } catch (IOException ignored) {
            return Collections.emptyList();
        }
        return Collections.list(enumeration);
    }

    @Override
    public @NotNull URL getFromRulesDirAsUrl(String path) {
        String completePath = getCompleteRulesUrl(path);
        return Objects.requireNonNull(getAsURL(completePath));
    }

    @Override
    public @NotNull InputStream getFromRulesDirAsStream(String path) {
        String completePath = getCompleteRulesUrl(path);
        InputStream inputStream = getAsStream(completePath);
        return Objects.requireNonNull(inputStream);
    }

    @Override
    public String getResourceDir() {
        return resourceDir;
    }

    @Override
    public String getRulesDir() {
        return rulesDir;
    }

    @Override
    public ResourceBundle getResourceBundle(String baseName, Locale locale) {
        return ResourceBundle.getBundle(baseName, locale);
    }
}
