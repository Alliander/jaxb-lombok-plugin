
JAXB / XJC Plugin to add [Lombok](https://projectlombok.org/features/index.html) annotations in classes generated
from an XML Schema. Annotations supported:
* [https://projectlombok.org/features/Data](@Data)
* [https://projectlombok.org/features/GetterSetter](@Setter)
* [https://projectlombok.org/features/GetterSetter](@Getter)
* [https://projectlombok.org/features/EqualsAndHashCode](@EqualsAndHashCode)
* [https://projectlombok.org/features/ToString](@ToString)
* [https://projectlombok.org/features/constructor](@AllArgsConstructor)
* [https://projectlombok.org/features/constructor](@NoArgsConstructor)
* [https://projectlombok.org/features/constructor](@Builder)

### Usage on the Command Line

XJC Plugin options:
* -Xlombok - enable the plugin and use [https://projectlombok.org/features/Data](@Data) annotation on all classes (see below)
* -Xlombok:Data - add [https://projectlombok.org/features/Data](@Data) annotation and remove all getters and setters
* -Xlombok:Setter - add [https://projectlombok.org/features/GetterSetter](@Setter) annotation
* -Xlombok:Getter - add [https://projectlombok.org/features/GetterSetter](@Getter) annotation
* -Xlombok:GetterSetter - add [https://projectlombok.org/features/GetterSetter](@Getter and @Setter) annotations, remove
all getters and setters in the process
* -Xlombok:EqualsAndHashCode - add [https://projectlombok.org/features/EqualsAndHashCode](@EqualsAndHashCode) annotation
* -Xlombok:ToString - add [https://projectlombok.org/features/ToString](@ToString) annotation
* -Xlombok:AllArgsConstructor - add [https://projectlombok.org/features/constructor](@AllArgsConstructor) annotation
* -Xlombok:NoArgsConstructor - add [https://projectlombok.org/features/constructor](@NoArgsConstructor) annotation
* -Xlombok:Builder - add [https://projectlombok.org/features/constructor](@Builder) annotation, which implies adding 
[https://projectlombok.org/features/constructor](@NoArgsConstructor) and 
[https://projectlombok.org/features/constructor](@AllArgsConstructor) annotations

### Usage with Maven
Minimal configuration is just to enable this plugin:
```xml
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>jaxb2-maven-plugin</artifactId>
                <version>2.2</version>
                <dependencies>
                    <dependency>
                        <groupId>it.yobibit</groupId>
                        <artifactId>jaxb-lombok-plugin</artifactId>
                        <version>1.0.0</version>
                    </dependency>
                </dependencies>
                <executions>
                    <execution>
                        <id>xjc</id>
                        <goals>
                            <goal>xjc</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <packageName>com.company.model</packageName>
                    <arguments>
                        <argument>-Xlombok</argument> <!-- That is always required annotation -->
                    </arguments>
                </configuration>
            </plugin>
```

### Inspired by:

* https://github.com/mplushnikov/xjc-lombok-plugin
* https://github.com/danielwegener/xjc-guava-plugin
