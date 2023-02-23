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

import org.apache.maven.plugins.annotations.Parameter;

public class File {

    @Parameter
    private java.io.File source;

    @Parameter(alias = "path")
    private String outputDirectory;

    @Parameter
    private boolean classpath;

    @Parameter
    private boolean modulepath;

    @Parameter
    private boolean ignoreBootConflict;

    @Parameter
    private String os;

    @Parameter
    private String comment;

    public File() {

    }

    public java.io.File getSource() {
        return source;
    }

    public void setSource(java.io.File source) {
        this.source = source;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public boolean isClasspath() {
        return classpath;
    }

    public void setClasspath(boolean classpath) {
        this.classpath = classpath;
    }

    public boolean isModulepath() {
        return modulepath;
    }

    public void setModulepath(boolean modulepath) {
        this.modulepath = modulepath;
    }

    public boolean isIgnoreBootConflict() {
        return ignoreBootConflict;
    }

    public void setIgnoreBootConflict(boolean ignoreBootConflict) {
        this.ignoreBootConflict = ignoreBootConflict;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
