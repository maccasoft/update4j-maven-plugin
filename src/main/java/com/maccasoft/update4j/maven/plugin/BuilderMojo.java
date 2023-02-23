/*
 * Copyright (c) 2023 Marco Maccaferri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.maccasoft.update4j.maven.plugin;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;
import org.update4j.Configuration;
import org.update4j.Configuration.Builder;
import org.update4j.FileMetadata;
import org.update4j.FileMetadata.Reference;
import org.update4j.OS;

@Mojo(name = "build", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class BuilderMojo extends AbstractMojo {

    private static final String[] DEFAULT_INCLUDES = {
        "**/**"
    };

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "${project.build.directory}")
    private java.io.File outputDir;

    @Parameter(property = "update4j.configName", defaultValue = "config.xml")
    private String fileName;

    @Parameter(property = "update4j.baseUri", required = true)
    private String baseUri;

    @Parameter(property = "update4j.basePath", required = true)
    private String basePath;

    @Parameter
    private List<DependencySet> dependencySets;

    @Parameter
    private List<FileSet> fileSets;

    @Parameter
    private List<File> files;

    @Parameter
    private Map<String, String> properties;

    @Parameter
    private Map<String, String> dynamicProperties;

    @Parameter
    private String updateHandler;

    @Parameter(property = "update4j.launcher")
    private String launcher;

    @Override
    public void execute() throws MojoExecutionException {
        Builder builder = Configuration.builder();

        builder.baseUri(baseUri);
        builder.basePath(basePath);

        addDependency(builder, project.getArtifact());
        for (Artifact artifact : project.getArtifacts()) {
            addDependency(builder, artifact);
        }

        if (files != null) {
            addFiles(builder);
        }

        if (fileSets != null) {
            addFileSets(builder);
        }

        if (properties != null) {
            for (Entry<String, String> entry : properties.entrySet()) {
                builder.property(entry.getKey(), entry.getValue());
            }
        }

        if (dynamicProperties != null) {
            for (Entry<String, String> entry : dynamicProperties.entrySet()) {
                builder.dynamicProperty(entry.getKey(), entry.getValue());
            }
        }

        if (updateHandler != null) {
            builder.updateHandler(updateHandler);
        }

        if (launcher != null) {
            builder.launcher(launcher);
        }

        Configuration config = builder.build();

        java.io.File configFile = new java.io.File(outputDir, fileName);
        try {
            Files.createDirectories(configFile.toPath().getParent());
            try (Writer out = Files.newBufferedWriter(configFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                config.write(out);
                out.close();
            }
            project.getProperties().setProperty("update4j.configFile", configFile.getAbsolutePath());
            getLog().info("Created " + configFile);
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating file " + configFile.getAbsolutePath(), e);
        }
    }

    private void addDependency(Builder builder, Artifact artifact) {
        java.io.File artifactFile = artifact.getFile();
        if (artifactFile == null) {
            return;
        }

        if (dependencySets == null) {
            getLog().debug("Adding " + artifactFile.getAbsolutePath());
            builder.file(FileMetadata.readFrom(artifactFile.toPath()).osFromFilename().classpath());
        }
        else {
            String id = artifact.getGroupId() + ":" + artifact.getArtifactId();
            for (DependencySet set : dependencySets) {
                String path = set.getOutputDirectory();
                if (path == null) {
                    path = "";
                }
                if (set.getIncludes() != null) {
                    if (set.getIncludes().contains(id)) {
                        getLog().debug("Adding " + artifactFile.getAbsolutePath());
                        Reference ref = FileMetadata.readFrom(artifactFile.toPath()).path(Paths.get(path, artifactFile.getName())).osFromFilename().classpath(set.isClasspath());
                        if (set.getOs() != null) {
                            ref.os(OS.fromShortName(set.getOs()));
                        }
                        ref.modulepath(set.isModulepath());
                        ref.ignoreBootConflict(set.isIgnoreBootConflict());
                        if (set.getComment() != null) {
                            ref.comment(set.getComment());
                        }
                        builder.file(ref);
                        break;
                    }
                }
                else if (set.getExcludes() == null || (set.getExcludes() != null && !set.getExcludes().contains(id))) {
                    getLog().debug("Adding " + artifactFile.getAbsolutePath());
                    Reference ref = FileMetadata.readFrom(artifactFile.toPath()).path(Paths.get(path, artifactFile.getName())).osFromFilename().classpath(set.isClasspath());
                    if (set.getOs() != null) {
                        ref.os(OS.fromShortName(set.getOs()));
                    }
                    ref.modulepath(set.isModulepath());
                    ref.ignoreBootConflict(set.isIgnoreBootConflict());
                    if (set.getComment() != null) {
                        ref.comment(set.getComment());
                    }
                    builder.file(ref);
                    break;
                }
            }
        }
    }

    private void addFiles(Builder builder) {
        for (File file : files) {
            String path = file.getOutputDirectory();
            if (path == null) {
                path = "";
            }
            OS os = file.getOs() != null ? OS.fromShortName(file.getOs()) : null;

            java.io.File source = file.getSource();
            getLog().debug("Adding " + source.getAbsolutePath());

            Reference ref = FileMetadata.readFrom(source.toPath()).path(Paths.get(path, source.getName()));
            if (os != null) {
                ref.os(os);
            }
            ref.classpath(file.isClasspath());
            ref.modulepath(file.isModulepath());
            ref.ignoreBootConflict(file.isIgnoreBootConflict());
            if (file.getComment() != null) {
                ref.comment(file.getComment());
            }
            builder.file(ref);
        }
    }

    private List<String> scanFileSet(java.io.File sourceDirectory, FileSet fileSet) {
        final String[] emptyStringArray = {};

        DirectoryScanner scanner = new DirectoryScanner();

        scanner.setBasedir(sourceDirectory);
        if (fileSet.getIncludes() != null && !fileSet.getIncludes().isEmpty()) {
            scanner.setIncludes(fileSet.getIncludes().toArray(emptyStringArray));
        }
        else {
            scanner.setIncludes(DEFAULT_INCLUDES);
        }

        if (fileSet.getExcludes() != null && !fileSet.getExcludes().isEmpty()) {
            scanner.setExcludes(fileSet.getExcludes().toArray(emptyStringArray));
        }

        if (fileSet.isUseDefaultExcludes()) {
            scanner.addDefaultExcludes();
        }

        scanner.scan();

        return Arrays.asList(scanner.getIncludedFiles());
    }

    private void addFileSets(Builder builder) {
        for (FileSet fileSet : fileSets) {
            java.io.File sourceDirectory = fileSet.getDirectory();
            if (!sourceDirectory.isAbsolute()) {
                sourceDirectory = new java.io.File(project.getBasedir(), sourceDirectory.getPath());
            }

            if (!sourceDirectory.exists()) {
                getLog().warn("Specified source directory " + sourceDirectory.getPath() + " does not exist.");
                continue;
            }

            String outDir = fileSet.getOutputDirectory();
            if (outDir == null) {
                outDir = "";
            }

            Path sourcePath = sourceDirectory.toPath();
            OS os = fileSet.getOs() != null ? OS.fromShortName(fileSet.getOs()) : null;

            List<String> includedFiles = scanFileSet(sourceDirectory, fileSet);
            for (String destination : includedFiles) {
                java.io.File source = new java.io.File(sourceDirectory, destination);
                getLog().debug("Adding " + source.getAbsolutePath());

                Reference ref = FileMetadata.readFrom(source.toPath()).path(sourcePath.relativize(source.toPath()));
                if (os != null) {
                    ref.os(os);
                }
                ref.classpath(fileSet.isClasspath());
                ref.modulepath(fileSet.isModulepath());
                ref.ignoreBootConflict(fileSet.isIgnoreBootConflict());
                if (fileSet.getComment() != null) {
                    ref.comment(fileSet.getComment());
                }
                builder.file(ref);
            }
        }
    }
}
