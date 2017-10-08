package pl.radical.jaxb.actions;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;

import pl.radical.jaxb.MethodSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:lukasz.rzanek@radical.com.pl">Łukasz Rżanek</a>
 * @since 07.10.2017
 */
public interface LombokPluginAction {
    void execute(JDefinedClass generatedClass);

    default void removeMethods(JDefinedClass generatedClass, MethodSelector... methods) {
        List<JMethod> removableMethods = new ArrayList<>();
        // find methods to remove

        for (JMethod method : generatedClass.methods()) {
            Stream.of(methods).forEach(ms -> {
                for (String methodPrefix : ms.getMethodPrefixes()) {
                    if (method.name().startsWith(methodPrefix)) {
                        // TODO check return type void?
                        removableMethods.add(method);
                    }
                }
            });
        }

        // remove methods
        for (JMethod method : removableMethods) {
            generatedClass.methods().remove(method);
        }

    }
}
