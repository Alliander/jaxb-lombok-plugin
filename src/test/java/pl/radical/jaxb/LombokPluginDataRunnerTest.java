package pl.radical.jaxb;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.AnnotationExpr;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.jaxb2.maven2.AbstractXJC2Mojo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static pl.radical.jaxb.LombokCommand.*;

/**
 * @author <a href="mailto:lukasz.rzanek@radical.com.pl">Łukasz Rżanek</a>
 * @since 05.10.2017
 */
@RunWith(JUnitParamsRunner.class)
@Slf4j
public class LombokPluginDataRunnerTest extends RunXJC2Mojo {
    private List<String> args;

    @Override
    public File getSchemaDirectory() {
        return new File(getBaseDir(), "src/test/resources");
    }

    @Override
    protected void configureMojo(AbstractXJC2Mojo mojo) {
        super.configureMojo(mojo);
        mojo.setProject(new MavenProject());
        mojo.setForceRegenerate(true);
        mojo.setExtension(true);
    }

    @Override
    public List<String> getArgs() {
        return args;
    }

    private static String FULL_CLASS = "de/invesdwin/metaproject/test/schema/JavaTypesTestPayload.java";

    private CompilationUnit runTestClasses(String file, LombokCommand... commands) throws MojoFailureException, MojoExecutionException, IOException {
        args = new ArrayList<>();
        args.add(LombokPlugin.OPTION_NAME);
        if (commands.length > 0) {
            Stream.of(commands).forEach(lombokCommand -> args.add(lombokCommand.getCommandName()));
        }

        prepareMojo();

        File javaFile = new File(getGeneratedDirectory(), file);

        // parse the file
        return JavaParser.parse(javaFile);
    }

    private boolean isAnnotationPresent(CompilationUnit cu, String annotation) {
        boolean success = false;

        for (AnnotationExpr annotationExpr : cu.getType(0).getAnnotations()) {
            if (annotationExpr.getName().getIdentifier().equals(annotation)) {
                success = true;
            }
        }
        return success;
    }

    @Test
    public void testDefaultAction() throws IOException, MojoExecutionException, MojoFailureException {
        CompilationUnit cu = runTestClasses(FULL_CLASS);

        assertTrue(isAnnotationPresent(cu, Data.class.getSimpleName()));

        log.info("{}", cu.getType(0).getMethods().size());
        assertEquals(0, cu.getType(0).getMethods().size());
    }

    @Test
    public void testRemovingGettersAndSetters() throws IOException, MojoExecutionException, MojoFailureException {
        CompilationUnit cu = runTestClasses(FULL_CLASS, GETTERSETTER);

        assertTrue(isAnnotationPresent(cu, Getter.class.getSimpleName()));
        assertTrue(isAnnotationPresent(cu, Setter.class.getSimpleName()));

        log.info("{}", cu.getType(0).getMethods().size());
        assertEquals(0, cu.getType(0).getMethods().size());
    }

    @Parameters({
            "Getter",
            "Setter"
    })
    @TestCaseName("checking {0} annotation and method removal")
    @Test
    public void testRemovingMethodsOfOneType(String annotation) throws IOException, MojoExecutionException, MojoFailureException {
        CompilationUnit cu = runTestClasses(FULL_CLASS, LombokCommand.valueOf(annotation.toUpperCase()));

        assertTrue(isAnnotationPresent(cu, annotation));

        log.info("{}", cu.getType(0).getMethods().size());
        assertEquals(cu.getType(0).getFields().size(), cu.getType(0).getMethods().size());
    }

    @Parameters({
            "EqualsAndHashCode",
            "ToString",
            "NoArgsConstructor",
            "AllArgsConstructor"
    })
    @TestCaseName("checking {0} annotation")
    @Test
    public void testSimpleAnnotation(String annotation) throws IOException, MojoExecutionException, MojoFailureException {
        CompilationUnit cu = runTestClasses(FULL_CLASS, LombokCommand.valueOf(annotation.toUpperCase()));

        assertTrue(isAnnotationPresent(cu, annotation));
    }

    @Test
    public void testBuilder() throws IOException, MojoExecutionException, MojoFailureException {
        CompilationUnit cu = runTestClasses(FULL_CLASS, BUILDER);

        assertTrue(isAnnotationPresent(cu, Builder.class.getSimpleName()));
        assertTrue(isAnnotationPresent(cu, AllArgsConstructor.class.getSimpleName()));
        assertTrue(isAnnotationPresent(cu, NoArgsConstructor.class.getSimpleName()));
    }

    @Test
    public void testDataCollision() throws IOException, MojoExecutionException, MojoFailureException {
        CompilationUnit cu = runTestClasses(FULL_CLASS, GETTER, SETTER, EQUALSANDHASHCODE, TOSTRING);

        assertTrue(isAnnotationPresent(cu, Data.class.getSimpleName()));

        log.info("{}", cu.getType(0).getMethods().size());
        assertEquals(0, cu.getType(0).getMethods().size());
    }

    @Test
    public void testDataCollision2() throws IOException, MojoExecutionException, MojoFailureException {
        CompilationUnit cu = runTestClasses(FULL_CLASS, GETTERSETTER, EQUALSANDHASHCODE, TOSTRING);

        assertTrue(isAnnotationPresent(cu, Data.class.getSimpleName()));

        log.info("{}", cu.getType(0).getMethods().size());
        assertEquals(0, cu.getType(0).getMethods().size());
    }
}