package pl.radical.jaxb;

import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.ErrorHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static pl.radical.jaxb.LombokCommand.*;

public class LombokPlugin extends Plugin {
    public static final String OPTION_NAME = "-Xlombok";

    private HashSet<LombokCommand> enabledCommands = new HashSet<>();

    @Override
    public String getOptionName() {
        return OPTION_NAME.replace("-", "");
    }

    @Override
    public String getUsage() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StringUtils.rightPad(OPTION_NAME, 28)).append("  add default Lombok annotation, synonym to ").append(DATA.getCommandName());
        for (LombokCommand lombokCommands : LombokCommand.class.getEnumConstants()) {
            stringBuilder.append("\n").append(lombokCommands.getUsage());
        }

        return stringBuilder.toString();
    }

    @Override
    public int parseArgument(Options opt, String[] args, int i) {
        String arg = args[i].replace(OPTION_NAME, "").replace(":", "").trim().toUpperCase();

        try {
            LombokCommand command = LombokCommand.valueOf(arg);
            enabledCommands.add(command);
            return 1;
        } catch (IllegalArgumentException e) {
        }
        return 0;
    }

    @Override
    public void onActivated(Options opts) throws BadCommandLineException {
        super.onActivated(opts);
    }

    @Override
    public void postProcessModel(Model model, ErrorHandler errorHandler) {
        super.postProcessModel(model, errorHandler);
    }

    @Override
    public boolean run(Outline outline, Options options, ErrorHandler errorHandler) {
        if (enabledCommands.size() == 0) {
            enabledCommands.add(DATA);
        }

        List<LombokCommand> dataCollisionSimple = Arrays.asList(GETTER, SETTER, EQUALSANDHASHCODE, TOSTRING);
        List<LombokCommand> dataCollisionComplex = Arrays.asList(GETTERSETTER, EQUALSANDHASHCODE, TOSTRING);
        if (enabledCommands.containsAll(dataCollisionSimple) || enabledCommands.containsAll(dataCollisionComplex)) {
            enabledCommands.removeAll(dataCollisionSimple);
            enabledCommands.removeAll(dataCollisionComplex);
            enabledCommands.add(DATA);
        }

        // for each generated classes
        for (ClassOutline generatedClassOutline : outline.getClasses()) {
            JDefinedClass generatedClass = generatedClassOutline.implClass;
            enabledCommands.forEach(lombokCommand -> lombokCommand.exec(generatedClass));
        }
        return true;
    }
}