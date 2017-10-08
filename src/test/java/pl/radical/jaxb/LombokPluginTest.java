package pl.radical.jaxb;

import junitparams.JUnitParamsRunner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import pl.radical.jaxb.actions.LombokPluginActionTestBase;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:lukasz.rzanek@radical.com.pl">Łukasz Rżanek</a>
 * @since 04.10.2017
 */
@RunWith(JUnitParamsRunner.class)
@Slf4j
public class LombokPluginTest extends LombokPluginActionTestBase {

    @Test
    public void testUsage() {
        String usage = plugin.getUsage();
        log.info("Usage: \n{}", usage);

        assertNotNull(usage);
        assertTrue(usage.startsWith(LombokPlugin.OPTION_NAME));

        for (LombokCommand lombokCommand : LombokCommand.class.getEnumConstants()) {
            assertTrue(StringUtils.contains(usage, lombokCommand.getUsage()));
        }
    }
}