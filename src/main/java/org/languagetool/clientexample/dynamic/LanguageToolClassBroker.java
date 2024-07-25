package org.languagetool.clientexample.dynamic;

import org.languagetool.broker.ClassBroker;

import java.net.URLClassLoader;

public class LanguageToolClassBroker implements ClassBroker {

    private final URLClassLoader classLoader;

    public LanguageToolClassBroker(URLClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public Class<?> forName(String qualifiedName) throws ClassNotFoundException {
        Class<?> clazz;
        try {
            // load using custom class loader.
            clazz = classLoader.loadClass(qualifiedName);
        } catch (ClassNotFoundException e) {
            // fallback to class loader of languagetool-core
            clazz = LanguageToolClassBroker.class.getClassLoader().loadClass(qualifiedName);
        }
        return clazz;
    }
}
