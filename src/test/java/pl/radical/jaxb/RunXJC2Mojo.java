package pl.radical.jaxb;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.jvnet.jaxb2.maven2.AbstractXJC2Mojo;
import org.jvnet.mjiip.v_2.XJC2Mojo;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:lukasz.rzanek@radical.com.pl">Łukasz Rżanek</a>
 * @since 05.10.2017
 */
@Slf4j
public class RunXJC2Mojo {
    public void prepareMojo() throws MojoFailureException, MojoExecutionException, IOException {
        if (generatedDirectory.exists()) {
            FileUtils.deleteDirectory(getGeneratedDirectory());
        }
        Mojo mojo = initMojo();
        mojo.execute();
    }

    @Getter
    private static File baseDir, generatedDirectory;

    public RunXJC2Mojo() {
        try {
            baseDir = (new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI())).getParentFile().getParentFile().getAbsoluteFile();
            generatedDirectory = new File(getBaseDir(), "target/generated-sources/xjc");
        } catch (Exception ex) {
            throw new AssertionError(ex);
        }
    }

    public File getSchemaDirectory() {
        return new File(getBaseDir(), "src/main/resources");
    }

    public List<String> getArgs() {
        return Collections.emptyList();
    }

    public String getGeneratePackage() {
        return null;
    }

    public boolean isWriteCode() {
        return true;
    }

    public AbstractXJC2Mojo initMojo() {
        AbstractXJC2Mojo mojo = createMojo();
        configureMojo(mojo);
        return mojo;
    }

    protected AbstractXJC2Mojo createMojo() {
        return new XJC2Mojo();
    }

    protected void configureMojo(AbstractXJC2Mojo mojo) {
        mojo.setProject(new MavenProject());
        mojo.setSchemaDirectory(getSchemaDirectory());
        mojo.setGenerateDirectory(getGeneratedDirectory());
        mojo.setGeneratePackage(getGeneratePackage());
        mojo.setArgs(getArgs());
        mojo.setVerbose(true);
        mojo.setDebug(true);
        mojo.setWriteCode(isWriteCode());
    }
}
