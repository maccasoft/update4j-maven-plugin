## Update4J Maven Plugin

Maven plugin that creates an [update4j](https://github.com/update4j/update4j) configuration file containing all your projects dependencies.

**Example:**

```xml
<project>
    [...]
    <build>
        [...]
        <plugins>
            <plugin>
                <groupId>com.maccasoft</groupId>
                <artifactId>update4j-maven-plugin</artifactId>
                <version>1.0.0</version>
                <executions>
                    <execution>
                        <id>create-update4j-config</id>
                        <phase>package</phase>
                        <goals>
                            <goal>create</goal>
                        </goals>
                        <configuration>
                            <baseUri>https://example.com/</baseUri>
                            <basePath>/home/myapp/</basePath>
                            <properties>
                                <app-name>MyApplication</app.name>
                                <user.location>$${user.home}/myapp/</user.location>
                            </properties>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

**Configuration**

```xml
    [...]
    <build>
        [...]
        <plugins>
            <plugin>
                <groupId>com.maccasoft</groupId>
                <artifactId>update4j-maven-plugin</artifactId>
                <version>1.0.0</version>
                <executions>
                    <execution>
                        <id>create-update4j-config</id>
                        <phase>package</phase>
                        <goals>
                            <goal>create</goal>
                        </goals>
                        <configuration>
                            <fileName>config.xml</fileName>
                            <baseUri>...</baseUri>
                            <basePath>...</basePath>
                            <dependencySets>
                                <dependencySet>
                                    <includes>...</includes>
                                    <excludes>...</excludes>
                                    <outputDirectory>...</outputDirectory>
                                    <classpath>true</classpath>
                                    <modulepath>false</modulepath>
                                    <ignoreBootConflict>false</ignoreBootConflict>
                                    <os>...</os>
                                    <comment>...</comment>
                                </dependencySet>
                            </dependencySets>
                            <fileSets>
                                <fileSet>
                                    <directory>...</directory>
                                    <outputDirectory>...</outputDirectory>
                                    <includes>...</includes>
                                    <excludes>...</excludes>
                                    <useDefaultExcludes>true</useDefaultExcludes>
                                    <classpath>false</classpath>
                                    <modulepath>false</modulepath>
                                    <ignoreBootConflict>false</ignoreBootConflict>
                                    <os>...</os>
                                    <comment>...</comment>
                                </fileSet>
                            </fileSets>
                            <files>
                                <file>
                                    <source>...</source>
                                    <outputDirectory>...</outputDirectory>
                                    <classpath>false</classpath>
                                    <modulepath>false</modulepath>
                                    <ignoreBootConflict>false</ignoreBootConflict>
                                    <os>...</os>
                                    <comment>...</comment>
                                </file>
                            </files>
                            <properties>...</properties>
                            <dynamicProperties>...</dynamicProperties>
                            <updateHandler>...</updateHandler>
                            <launcher>...</launcher>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```
