package pl.radical.jaxb.actions;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author <a href="mailto:lukasz.rzanek@radical.com.pl">Łukasz Rżanek</a>
 * @since 07.10.2017
 */
@Slf4j
public class RemoveSettersTest extends LombokPluginActionTestBase {
    @Test
    public void execute() throws Exception {
        super.testMethodRemoval(log, aClass, new RemoveSetters(), 2);
    }
}