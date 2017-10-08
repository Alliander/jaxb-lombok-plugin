package it.yobibit.jaxb;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import lombok.*;
import org.xml.sax.ErrorHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LombokPlugin extends Plugin {

    public static final String OPTION_NAME = "Xlombok";
    private final Command defaultCommand;
    private final Map<String, Command> commands = new HashMap<>();

    public LombokPlugin() {
        defaultCommand = new LombokCommand("Data", Data.class);

        addCommand(defaultCommand);
        addLombokCommand("Getter", Getter.class);
        addLombokCommand("Setter", Setter.class);
        addLombokCommand("GetterSetter", Getter.class, Setter.class);
        addLombokCommand("ToString", ToString.class);
        addLombokCommand("EqualsAndHashCode", EqualsAndHashCode.class);
        addLombokCommand("NoArgsConstructor", NoArgsConstructor.class);
        addLombokCommand("AllArgsConstructor", AllArgsConstructor.class);

        addCommand(new LombokCommand("Builder", Builder.class) {
            @Override
            public void editGeneratedClass(JDefinedClass generatedClass) {
                generatedClass.annotate(NoArgsConstructor.class);
                generatedClass.annotate(AllArgsConstructor.class);
                generatedClass.annotate(Builder.class).param("builderMethodName", "builderFor" + generatedClass.name());
            }
        });

        addCommand(new Command("removeGeneratedSourceSetters", "remove Setters from JAXB generated sources") {
            @Override
            public void editGeneratedClass(JDefinedClass generatedClass) {
                removeGeneratedMethods(generatedClass, "set");
                generatedClass.annotate(Setter.class);
            }
        });

        addCommand(new Command("removeGeneratedSourceGetters", "remove Setters from JAXB generated sources") {
            @Override
            public void editGeneratedClass(JDefinedClass generatedClass) {
                removeGeneratedMethods(generatedClass, "get");
                generatedClass.annotate(Getter.class);
            }
        });

        addCommand(new Command("removeGeneratedSourceGettersSetters", "remove Setters from JAXB generated sources") {
            @Override
            public void editGeneratedClass(JDefinedClass generatedClass) {
                removeGeneratedMethods(generatedClass, "get");
                generatedClass.annotate(Getter.class);

                removeGeneratedMethods(generatedClass, "set");
                generatedClass.annotate(Setter.class);
            }
        });
    }

    private void addLombokCommand(String name, Class... lombokAnnotation) {
        addCommand(new LombokCommand(name, lombokAnnotation));
    }

    private void addCommand(Command command) {
        commands.put(command.getParameter(), command);
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    @Override
    public String getUsage() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("  -").append(OPTION_NAME).append(":  add Lombok annotations \n");
        for (Command command : commands.values()) {
            stringBuilder.append(command.getUsage()).append("\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public int parseArgument(Options opt, String[] args, int i) {
        String arg = args[i].trim();
        Command command = commands.get(arg);
        if (command != null) {
            command.setEnabled(true);
            return 1;
        }
        return 0;
    }

    private boolean isAbstractClass(JDefinedClass generatedClass) {
        return generatedClass.isAbstract() || generatedClass.isInterface() || generatedClass.isAnonymous();
    }

    @Override
    public boolean run(Outline outline, Options options, ErrorHandler errorHandler) {
        // for each generated classes
        for (ClassOutline generatedClassOutline : outline.getClasses()) {
            JDefinedClass generatedClass = generatedClassOutline.implClass;
            processClass(generatedClass);
        }
        return true;
    }

    protected void processClass(JDefinedClass generatedClass) {
        if (commands.get("-" + OPTION_NAME + ":" + "Data").isEnabled()) {
            commands.get("-" + OPTION_NAME + ":" + "Getter").setEnabled(false);
            commands.get("-" + OPTION_NAME + ":" + "Setter").setEnabled(false);
            commands.get("-" + OPTION_NAME + ":" + "EqualsAndHashCode").setEnabled(false);
            commands.get("-" + OPTION_NAME + ":" + "ToString").setEnabled(false);
        } else if (commands.get("-" + OPTION_NAME + ":" + "Getter").isEnabled() && commands.get("-" + OPTION_NAME + ":" + "Setter").isEnabled() && commands.get("-" + OPTION_NAME + ":" + "EqualsAndHashCode").isEnabled() && commands.get("-" + OPTION_NAME + ":" + "ToString").isEnabled()) {
            commands.get("-" + OPTION_NAME + ":" + "Getter").setEnabled(false);
            commands.get("-" + OPTION_NAME + ":" + "Setter").setEnabled(false);
            commands.get("-" + OPTION_NAME + ":" + "EqualsAndHashCode").setEnabled(false);
            commands.get("-" + OPTION_NAME + ":" + "ToString").setEnabled(false);

            commands.get("-" + OPTION_NAME + ":" + "Data").setEnabled(true);
        }

        if (!isAbstractClass(generatedClass) && !generatedClass.fields().isEmpty()) {
            boolean commandExecuted = false;
            for (Command command : commands.values()) {
                if (command.isEnabled()) {
                    command.editGeneratedClass(generatedClass);
                    commandExecuted = true;
                }
            }

            if (!commandExecuted) {
                defaultCommand.editGeneratedClass(generatedClass);
            }
        }
    }

    protected void removeGeneratedMethods(JDefinedClass generatedClass, String methodPrefix) {
        List<JMethod> setters = new ArrayList<>();
        // find methods to remove
        for (JMethod method : generatedClass.methods()) {
            if (method.name().startsWith(methodPrefix)) {
                // TODO check return type void?
                setters.add(method);
            }
        }

        // remove methods
        for (JMethod method : setters) {
            generatedClass.methods().remove(method);
        }
    }
}