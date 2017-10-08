package pl.radical.jaxb.actions;

import com.sun.codemodel.JDefinedClass;

import pl.radical.jaxb.MethodSelector;

/**
 * @author <a href="mailto:lukasz.rzanek@radical.com.pl">Łukasz Rżanek</a>
 * @since 07.10.2017
 */
public class RemoveGettersAndSetters implements LombokPluginAction {
    @Override
    public void execute(JDefinedClass generatedClass) {
        removeMethods(generatedClass, MethodSelector.GETTERS, MethodSelector.SETTERS);
    }
}