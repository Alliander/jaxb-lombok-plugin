package pl.radical.jaxb;

import com.sun.codemodel.JDefinedClass;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import pl.radical.jaxb.actions.LombokPluginAction;
import pl.radical.jaxb.actions.RemoveGetters;
import pl.radical.jaxb.actions.RemoveGettersAndSetters;
import pl.radical.jaxb.actions.RemoveSetters;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:lukasz.rzanek@radical.com.pl">Łukasz Rżanek</a>
 * @since 07.10.2017
 */
@AllArgsConstructor
public enum LombokCommand {
    DATA(Arrays.asList(Data.class), Arrays.asList(new RemoveGettersAndSetters())),
    GETTER(Arrays.asList(Getter.class), Arrays.asList(new RemoveGetters())),
    SETTER(Arrays.asList(Setter.class), Arrays.asList(new RemoveSetters())),
    GETTERSETTER(Arrays.asList(Getter.class, Setter.class), Arrays.asList(new RemoveGettersAndSetters())),
    TOSTRING(Arrays.asList(ToString.class), null),
    EQUALSANDHASHCODE(Arrays.asList(EqualsAndHashCode.class), null),
    NOARGSCONSTRUCTOR(Arrays.asList(NoArgsConstructor.class), null),
    ALLARGSCONSTRUCTOR(Arrays.asList(AllArgsConstructor.class), null),
    BUILDER(Arrays.asList(Builder.class, AllArgsConstructor.class, NoArgsConstructor.class), null);

    @Getter
    private List<Class<? extends Annotation>> annotations;

    private List<LombokPluginAction> actions;

    @Getter
    private String usage;

    @Getter
    private String commandName;

    LombokCommand(List<Class<? extends Annotation>> annotations, List<LombokPluginAction> actions) {
        this.annotations = annotations;
        this.actions = actions;

        commandName = LombokPlugin.OPTION_NAME + ":" + name().toLowerCase();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StringUtils.rightPad(commandName, 30));
        stringBuilder.append("add Lombok ").append(annotations.stream().map(aClass -> aClass.getSimpleName()).map(s -> "@".concat(s)).collect(Collectors.joining(", ")).toString());
        if (annotations.size() == 1) {
            stringBuilder.append(" annotation");
        } else {
            stringBuilder.append(" annotations");
        }
        usage = stringBuilder.toString();
    }

    public void exec(JDefinedClass generatedClass) {
        for (Class<? extends Annotation> lombokAnnotation : annotations) {
            generatedClass.annotate(lombokAnnotation);
        }

        if (actions != null && !actions.isEmpty()) {
            actions.forEach(lombokPluginAction -> lombokPluginAction.execute(generatedClass));
        }
    }
}
