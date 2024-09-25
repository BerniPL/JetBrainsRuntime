/* Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2024 JetBrains s.r.o.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @summary Verifies that CDS works with jar located in directories
 *          with names that need escaping
 * @bug 8339460
 * @requires vm.cds
 * @requires vm.cds.custom.loaders
 * @requires vm.flagless
 * @library /test/lib /test/hotspot/jtreg/runtime/cds/appcds
 * @compile mypackage/Main.java mypackage/Another.java
 * @run main/othervm ComplexURITest
 */

import jdk.test.lib.process.OutputAnalyzer;
import jdk.test.lib.process.ProcessTools;
import jdk.test.lib.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ComplexURITest {
    final static String listFileName = "test-classlist.txt";
    final static String archiveName  = "test-dynamic.jsa";
    final static String moduleName = "mymodule";
    public static void main(String[] args) throws Exception {
        System.setProperty("test.noclasspath", "true");
        String jarFile = JarBuilder.build(moduleName, "mypackage/Main", "mypackage/Another");

        Path subDir = Path.of(".", "dir with space");
        Files.createDirectory(subDir);
        Path newJarFilePath = subDir.resolve(moduleName + ".jar");
        Files.move(Path.of(jarFile), newJarFilePath);
        jarFile = newJarFilePath.toString();

        File fileList = new File(listFileName);
        delete(fileList.toPath());
        File fileArchive = new File(archiveName);
        delete(fileArchive.toPath());

        createClassList(jarFile);
        if (!fileList.exists()) {
            throw new RuntimeException("No class list created at " + fileList);
        }

        createArchive(jarFile);
        if (!fileArchive.exists()) {
            throw new RuntimeException("No shared classes archive created at " + fileArchive);
        }

        useArchive(jarFile);
        delete(fileArchive.toPath());

        createDynamicArchive(jarFile);
        if (!fileArchive.exists()) {
            throw new RuntimeException("No dynamic archive created at " + fileArchive);
        }
        testDynamicArchive(jarFile);
    }

    private static void delete(Path path) throws IOException {
        if (Files.exists(path)) {
            if (Platform.isWindows()) {
                Files.setAttribute(path, "dos:readonly", false);
            }
            Files.delete(path);
        }
    }

    private static void createClassList(String jarFile) throws Exception {
        String[] launchArgs  = {
                "-XX:DumpLoadedClassList=" + listFileName,
                "--module-path",
                jarFile,
                "--module",
                moduleName + "/mypackage.Main"};
        ProcessBuilder pb = ProcessTools.createLimitedTestJavaProcessBuilder(launchArgs);
        OutputAnalyzer output = TestCommon.executeAndLog(pb, "create-list");
        output.shouldHaveExitValue(0);
    }

    private static void createArchive(String jarFile) throws Exception {
        String[] launchArgs  = {
                "-Xshare:dump",
                "-XX:SharedClassListFile=" + listFileName,
                "-XX:SharedArchiveFile=" + archiveName,
                "--module-path",
                jarFile,
                "--module",
                moduleName + "/mypackage.Main"};
        ProcessBuilder pb = ProcessTools.createLimitedTestJavaProcessBuilder(launchArgs);
        OutputAnalyzer output = TestCommon.executeAndLog(pb, "dump-archive");
        output.shouldHaveExitValue(0);
    }

    private static void useArchive(String jarFile) throws Exception {
        String[] launchArgs  = {
                "-Xshare:on",
                "-XX:SharedArchiveFile=" + archiveName,
                "--module-path",
                jarFile,
                "--module",
                moduleName + "/mypackage.Main"};
        ProcessBuilder pb = ProcessTools.createLimitedTestJavaProcessBuilder(launchArgs);
        OutputAnalyzer output = TestCommon.executeAndLog(pb, "use-archive");
        output.shouldHaveExitValue(0);
    }

    private static void createDynamicArchive(String jarFile) throws Exception {
        String[] launchArgs  = {
                "-XX:ArchiveClassesAtExit=" + archiveName,
                "--module-path",
                jarFile,
                "--module",
                moduleName + "/mypackage.Main"};
        ProcessBuilder pb = ProcessTools.createLimitedTestJavaProcessBuilder(launchArgs);
        OutputAnalyzer output = TestCommon.executeAndLog(pb, "dynamic-archive");
        output.shouldHaveExitValue(0);
    }

    private static void testDynamicArchive(String jarFile) throws Exception {
        String[] launchArgs  = {
                "-XX:SharedArchiveFile=" + archiveName,
                "-XX:+PrintSharedArchiveAndExit",
                "--module-path",
                jarFile,
                "--module",
                moduleName + "/mypackage.Main"};
        ProcessBuilder pb = ProcessTools.createLimitedTestJavaProcessBuilder(launchArgs);
        OutputAnalyzer output = TestCommon.executeAndLog(pb, "dynamic-archive");
        output.shouldHaveExitValue(0);
        output.shouldContain("archive is valid");
        output.shouldContain(": mypackage.Main app_loader");
        output.shouldContain(": mypackage.Another unregistered_loader");
    }
}
