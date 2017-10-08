package it.yobibit.jaxb;

import com.sun.codemodel.*;
import static org.junit.Assert.*;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lukasz.rzanek@radical.com.pl">Łukasz Rżanek</a>
 * @since 04.10.2017
 */
@RunWith(JUnitParamsRunner.class)
public class LombokPluginTest {
    private final LombokPlugin plugin = new LombokPlugin();

    private JDefinedClass aClass;

    @Before
    public void setUpTestClass() throws Exception {
        JCodeModel aModel = new JCodeModel();
        JPackage aPackage = aModel._package("test");
        aClass = aPackage._class("AClass");

        JMethod aSetter = aClass.method(JMod.PUBLIC, aModel.VOID, "setField");

        JFieldVar aField = aClass.field(JMod.PRIVATE, aModel.INT, "field");
        aClass.field(JMod.PRIVATE, aModel.BOOLEAN, "anotherField");
        aClass.field(JMod.STATIC | JMod.PUBLIC, aModel.SHORT, "staticField");
        JMethod aGetter = aClass.method(JMod.PUBLIC, aModel.INT, "getField");
        aGetter.body()._return(aField);
        final JVar setterParam = aSetter.param(aModel.INT, "field");
        aSetter.body().assign(aField, setterParam);

        JDefinedClass aSuperClass = aPackage._class("ASuperClass");
        aClass._extends(aSuperClass);
        aSuperClass.field(JMod.PRIVATE, aModel.DOUBLE, "superClassField");
    }

    @Test
    public void testUsage() {
        String usage = plugin.getUsage();
        assertNotNull(usage);
        assertTrue(usage.startsWith("  -Xlombok:"));
    }

    @Test
    public void testGetterSetterAnnotationAndRemoveGettersAndSetters() {
        final String[] args = {"-Xlombok:Getter", "-Xlombok:Setter"};
        plugin.removeGeneratedMethods(aClass, "set");
        plugin.removeGeneratedMethods(aClass, "get");
        plugin.parseArgument(null, args, 0);
        plugin.parseArgument(null, args, 1);
        plugin.processClass(aClass);

        assertEquals(2, aClass.annotations().size());
        assertTrue(aClass.methods().isEmpty());
    }

    @Test
    public void testDataAndToStringAndRemoveGettersAndSetters() {
        final String[] args = {"-Xlombok:Data", "-Xlombok:ToString"};
        plugin.removeGeneratedMethods(aClass, "set");
        plugin.removeGeneratedMethods(aClass, "get");
        plugin.parseArgument(null, args, 0);
        plugin.parseArgument(null, args, 1);
        plugin.processClass(aClass);

        assertEquals(1, aClass.annotations().size());
        assertTrue(aClass.methods().isEmpty());
    }

    @Parameters({
            "set, 1",
            "get, 1"
    })
    @TestCaseName("Removing {0} methods")
    @Test
    public void testRemoveGeneratedSourceMethods(String method, int result) {
        plugin.removeGeneratedMethods(aClass, method);
        assertEquals(result, aClass.methods().size());
    }

    @Test
    public void testRemoveGeneratedSourceGettersAndSetters() {
        plugin.removeGeneratedMethods(aClass, "get");
        plugin.removeGeneratedMethods(aClass, "set");
        assertTrue(aClass.methods().isEmpty());
    }
}