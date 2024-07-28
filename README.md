# Example of LanguageTool client with dynamic language loading

This is an example client project that support dynamic language module loading with URLClassLoader
and demonstrate an application that has a capable for language as a plugin.

## License

This project is distributed under the GNU General Public License 3.0 or later.

## Tricks

This project has three classes and one subproject to support dynamic loading.

###  LanguageManager class

`LanguageManager` class is an entry point of language modules.
Each language modules should register their owned LT library class through `registerLTLanguage` function.
It takes Full Qualified Class Name such as `org.languagetool.language.English`.

When `LanguageManager.getLTLanguage` is called, it try to load the modules through LT's `Languages.getOrAddLanguageByClassName` function.

`Main` class of the example client uses the trick to try loading "xx" language that will force all the language modules into the application.

### LanguageClassBroker class

`LanguageClassBroker` class implement `ClassBroker` interface that use custome class loader to load classes.
When the application loads plugins/modules, it uses its own custom URLClassLoader, so the language classes are out-of-scope of languagetool-core library's class loader.
The class assist the LT core library to load a specified class by a proper class loader.

Because `Languages.getOrAddLanguageByClassName` uses `ClassBroker` then the modules are loaded properly.

### LanguageDataBroker class

When language classes loading resources, it uses LT-core library methods such as `AbstractSimpleReplaceRule#loadFromPath("/en/contructions.txt")` and
it uses `DataBroker` to load resources.
It is tricky when runtime is Java 9+, class and resource loading are strictly checked permissons and support Java Platform Module System(JPMS).
It is why the broker should use a class of specified language, not a class loader in the data broker methods.

### languages sub-project

The exmaple client project has a subproject `languages` in a folder `languages`. It only has a `build.gradle` build script that configured to genearate FatJar/UberJar under `<rootProject>/build/modules/` that name is `language-en.jar`.
These jar file act as language module which the exmaple client loads dynamic way.

### `Main#loadLanguageModule`

The function `loadLanguageModule` loads `language-en.jar` from `build/modules/` folder, register language classes and load specified languages in a memory.
It also register `LanguageClassBroker` and `LanguageDataBroker` classes into LT core library.
After loading and registering tricks, application works as usual.

## initialization of language classes

1. Application started
2. Application calls LanguageTool's `Languages.get`
3. `Languages.get` calls `LanguageDataBroker` to load LT `properties` file.
4. `LanguageDataBroker`  ask `LanguageManager` to return LT language AmericanEnglish
5. `LanguageManager` loops all the FQCN registered from language modules by searching "xx" language.
6. `LanguageManager` calls  `Languages.getOrAddLanguageByClassName(fqcn)`
7. `Languages.getOrAddLanguageByClassName(fqcn)`  calls `LanguageClassBroker` to load specified FQCN.
8. `LanguageClassBroker`  uses a custom class loader which can be obtained with `PluginUtil.getLaungageClassLoader`
9. `LanguageClassBroker` load LT language class
10.  `LanguageManager`  compares LT language  Lang-Code and Contry-code. If not a target, try another FQCN.
11. When `LanguageManager` satisfied to find a LT language class, it returns it to `LanguageDataBroker` (Do you remember Step 3?)
12.  LanguageTool's `Languages.get()` can obtains a target LT Language classes from a dynamic registry because of step 7  triggers it.
13. Application obtains a LT language class list finally by `Languages.get`.
14. Application show a number of languages.
     
