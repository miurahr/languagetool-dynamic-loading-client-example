package org.languagetool.clientexample.dynamic;

import org.languagetool.broker.ClassBroker;

import java.net.URLClassLoader;

public class LanguageClassBroker implements ClassBroker {

    private final URLClassLoader classLoader;

    public LanguageClassBroker(URLClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public Class<?> forName(String qualifiedName) throws ClassNotFoundException {
        Class<?> clazz;
        try {
            clazz = classLoader.loadClass(qualifiedName);
        } catch (ClassNotFoundException e) {
            clazz = LanguageClassBroker.class.getClassLoader().loadClass(qualifiedName);
        }
        return clazz;
    }
}
