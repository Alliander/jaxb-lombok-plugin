package pl.radical.jaxb;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author <a href="mailto:lukasz.rzanek@radical.com.pl">Łukasz Rżanek</a>
 * @since 07.10.2017
 */
@Slf4j
@RunWith(JUnitParamsRunner.class)
public class LombokCommandTest {

    @Parameters({
            "Data, 1",
            "Getter, 1",
            "Setter, 1",
            "GetterSetter, 2",
            "ToString, 1",
            "EqualsAndHashcode, 1",
            "NoArgsConstructor, 1",
            "AllArgsConstructor, 1",
            "Builder, 3"
    })
    @TestCaseName("case for '{0}' param")
    @Test
    public void generalCheck(String paramName, int expectedAnnotations) throws Exception {
        LombokCommand lombokCommand = LombokCommand.valueOf(paramName.toUpperCase());
        assertNotNull(lombokCommand);

        log.debug("Checking annotation count for [{}] param, expected [{}], got [{}]", paramName, expectedAnnotations, lombokCommand.getAnnotations().size());
        assertEquals(expectedAnnotations, lombokCommand.getAnnotations().size());

        log.info("Checking usage for [{}] param: [{}]", paramName, lombokCommand.getUsage());
        assertEquals(expectedAnnotations, StringUtils.countMatches(lombokCommand.getUsage(), "@"));
    }

}