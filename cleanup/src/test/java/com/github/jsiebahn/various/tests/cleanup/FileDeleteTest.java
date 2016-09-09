package com.github.jsiebahn.various.tests.cleanup;

import org.apache.commons.io.FileCleaningTracker;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 09.09.16 07:21
 */
public class FileDeleteTest {

    private String path;

    private boolean init;

    private List<String> memoryAllocator = new ArrayList<>();

    @Test
    public void shouldDeleteTempFile() throws Exception {

        new Thread(this::createAndReadFile).start();

        StopWatch watch = new StopWatch();
        watch.start();
        while (!init || Files.exists(Paths.get(path))) {
            if (path == null) {
                System.out.println("Path is still null.");
            }
            // allocate memory to trigger garbage collection without calling System.gc()
            memoryAllocator.add(StringUtils.repeat(UUID.randomUUID().toString(), 1000000));
            Thread.sleep(20);
        }
        watch.stop();

        System.out.println("Deleted after " + Duration.ofNanos(watch.getNanoTime()));

    }



    private String createAndReadFile() {

        try {

            File f = File.createTempFile("cleanupTest", ".tmp");
            this.path = f.getAbsolutePath();
            f.deleteOnExit();
            FileCleaningTracker tracker = new FileCleaningTracker();
            tracker.track(f, f);

            try (FileOutputStream out = new FileOutputStream(f)) {
                IOUtils.write(StringUtils.repeat("Test", 10000), out);
            }

            init = true;

            try (FileInputStream in = new ReferenceHoldingFileInputStream(f)) {
                Assert.assertEquals(StringUtils.repeat("Test", 10000), IOUtils.toString(in));
            }

            AtomicReference<FileInputStream> in = new AtomicReference<>();
            try {
                in.set(new ReferenceHoldingFileInputStream(f));
                Assert.assertEquals(StringUtils.repeat("Test", 10000), IOUtils.toString(in.get()));
            }
            catch (Exception ignored) {

            }
            finally {
                new Thread(() -> {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ignored) {

                    }
                    finally {
                        IOUtils.closeQuietly(in.get());
                    }

                }).start();
            }

            return f.getAbsolutePath();
        }
        catch (Exception e) {
            Assert.fail();
        }

        return null;
    }

    private static class ReferenceHoldingFileInputStream extends FileInputStream {

        @SuppressWarnings("unused")
        private File file;

        public ReferenceHoldingFileInputStream(File file) throws FileNotFoundException {
            super(file);
            this.file = file;
        }

        @Override
        public void close() throws IOException {
            this.file = null;
            super.close();
        }

    }
}
