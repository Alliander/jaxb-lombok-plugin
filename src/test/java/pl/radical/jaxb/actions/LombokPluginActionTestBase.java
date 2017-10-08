package pl.radical.jaxb.actions;

import com.sun.codemodel.*;
import org.junit.Before;
import org.slf4j.Logger;

import pl.radical.jaxb.LombokPlugin;

import java.util.stream.Collectors;

import static com.sun.codemodel.JMod.*;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:lukasz.rzanek@radical.com.pl">Łukasz Rżanek</a>
 * @since 07.10.2017
 */
public class LombokPluginActionTestBase {
    protected final LombokPlugin plugin = new LombokPlugin();

    protected JDefinedClass aClass;

    @Before
    public void setUpTestClass() throws Exception {
        JCodeModel aModel = new JCodeModel();
        JPackage aPackage = aModel._package("test");
        aClass = aPackage._class("AClass");


        aClass.field(STATIC | PUBLIC, aModel.SHORT, "staticField");

        {
            JFieldVar bField = aClass.field(PRIVATE, aModel.BOOLEAN, "booleanField");
            JMethod bGetter = aClass.method(PUBLIC, aModel.BOOLEAN, "isBooleanField");
            bGetter.body()._return(bField);

            JMethod bSettter = aClass.method(PUBLIC, aModel.VOID, "setBooleanField");
            JVar bSetterParam = bSettter.param(aModel.BOOLEAN, "booleanField");
            bSettter.body().assign(bField, bSetterParam);
        }

        {
            JFieldVar aField = aClass.field(PRIVATE, aModel.INT, "field");
            JMethod aGetter = aClass.method(PUBLIC, aModel.INT, "getField");
            aGetter.body()._return(aField);

            JMethod aSetter = aClass.method(PUBLIC, aModel.VOID, "setField");
            JVar aSetterParam = aSetter.param(aModel.INT, "field");
            aSetter.body().assign(aField, aSetterParam);
        }

        JDefinedClass aSuperClass = aPackage._class("ASuperClass");
        aClass._extends(aSuperClass);
        aSuperClass.field(PRIVATE, aModel.DOUBLE, "superClassField");
    }

    public void testMethodRemoval(Logger log, JDefinedClass aClass, LombokPluginAction action, int expectedMethodCount) {
        assertEquals(4, aClass.methods().size());

        log.info("Class methods before: {}", aClass.methods().stream().map(jMethod -> jMethod.name()).collect(Collectors.joining(", ")));

        action.execute(aClass);

        log.info("Class methods after: {}", aClass.methods().stream().map(jMethod -> jMethod.name()).collect(Collectors.joining(", ")));
        assertEquals("Number of setter methods in the class different than expected", expectedMethodCount, aClass.methods().size());

    }

//    @Test
//    public void testGetterSetterAnnotationAndRemoveGettersAndSetters() {
//        final String[] args = {"-Xlombok:GetterSetter"};
//        plugin.removeGeneratedMethods(aClass, GETTERS, SETTERS);
//        plugin.parseArgument(null, args, 0);
//        plugin.processClass(aClass);
//
//        assertEquals("Number of annotations is not as it should be: 2, @Getter and @Setter" ,2, aClass.annotations().size());
//        assertTrue(aClass.methods().isEmpty());
//    }
//
//    @Test
//    public void testDataAndToStringAndRemoveGettersAndSetters() {
//        final String[] args = {"-Xlombok:Data", "-Xlombok:ToString"};
//        plugin.removeGeneratedMethods(aClass, GETTERS, SETTERS);
//        plugin.parseArgument(null, args, 0);
//        plugin.parseArgument(null, args, 1);
//        plugin.processClass(aClass);
//
//        assertEquals(1, aClass.annotations().size());
//        assertTrue(aClass.methods().isEmpty());
//    }
//
//    @Parameters({
//            "SETTERS, 2",
//            "GETTERS, 2"
//    })
//    @TestCaseName("Removing {0} methods")
//    @Test
//    public void testRemoveGeneratedSourceMethods(String method, int result) {
//        assertEquals(4, aClass.methods().size());
//
//        log.info("Class methods before: {}", aClass.methods().stream().map(jMethod -> jMethod.name()).collect(Collectors.joining(", ")));
//        plugin.removeGeneratedMethods(aClass, MethodSelector.valueOf(method));
//        log.info("Class methods after: {}", aClass.methods().stream().map(jMethod -> jMethod.name()).collect(Collectors.joining(", ")));
//        assertEquals("Number of '" + method + "' methods in the class different than expected", result, aClass.methods().size());
//    }
//
//    @Test
//    public void testRemoveGeneratedSourceGettersAndSetters() {
//        assertEquals(4, aClass.methods().size());
//
//        plugin.removeGeneratedMethods(aClass, GETTERS, SETTERS);
//        assertTrue("Number of methods in the class different than expected", aClass.methods().isEmpty());
//    }
}