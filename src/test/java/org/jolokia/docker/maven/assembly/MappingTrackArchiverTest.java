package org.jolokia.docker.maven.assembly;/*
 * 
 * Copyright 2014 Roland Huss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author roland
 * @since 02/07/15
 */
public class MappingTrackArchiverTest {


    @Test(expected = IllegalArgumentException.class)
    public void noDirectory() throws Exception {
        MappingTrackArchiver archiver = new MappingTrackArchiver();
        archiver.addDirectory(new File(System.getProperty("user.home")), "tmp");
        AssemblyFiles files = archiver.getAssemblyFiles();
    }

    @Test
    public void simple() throws Exception {
        MappingTrackArchiver archiver = new MappingTrackArchiver();
        File tempFile = File.createTempFile("tracker", "txt");
        archiver.addFile(tempFile, "test.txt");
        AssemblyFiles files = archiver.getAssemblyFiles();
        assertNotNull(files);
        List<AssemblyFiles.Entry> entries = files.getUpdatedEntriesAndRefresh();
        assertEquals(0, entries.size());
        Thread.sleep(1000);
        FileUtils.touch(tempFile);
        entries = files.getUpdatedEntriesAndRefresh();
        assertEquals(1,entries.size());
        AssemblyFiles.Entry entry = entries.get(0);
        assertEquals(tempFile,entry.getSrcFile());
        assertEquals(new File("test.txt"),entry.getDestFile());
    }
}
