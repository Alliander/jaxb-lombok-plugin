package pl.radical.jaxb;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:lukasz.rzanek@radical.com.pl">Łukasz Rżanek</a>
 * @since 06.10.2017
 */
@AllArgsConstructor
public enum MethodSelector {
    GETTERS(Arrays.asList("get", "is")),
    SETTERS(Arrays.asList("set"));

    @Getter
    private List<String> methodPrefixes;
}
