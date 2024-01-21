package com.wiredbraincoffee.automate.watch;

import com.wiredbraincoffee.automate.watch.tasks.CSVIngesterTask;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FolderWatchApp {

    public static void main(String[] args) {

        ExecutorService executor = Executors.newCachedThreadPool();
        String sourceFolder = "/Users/prathamlongia/Desktop/Gradle/projects/wiredbraincoffee/filedrop";
        String targetFile = "/Users/prathamlongia/Desktop/Gradle/projects/wiredbraincoffee/datasink/sales-global.dat";

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {

            Path targetPath = Paths.get(targetFile);
            Path sourcePath = Paths.get(sourceFolder);
            System.out.println(sourcePath.toAbsolutePath());
            WatchKey watchKey = sourcePath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            while ((watchKey = watchService.take()) != null) {
                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    if (event.context() instanceof Path) {
                        Path filePath = sourcePath.resolve((Path) event.context());
                        String type = Files.probeContentType(filePath);
                        if (type != null) {
                            IngesterTask task =  new CSVIngesterTask(filePath.toAbsolutePath().toString(), targetPath.toAbsolutePath().toString());
                            executor.submit(task);
                            System.out.println(
                                    "Event kind:" + event.kind() + ". File affected: " + event.context() + ". Type: " + type
                            );
                        }
                    }

                }
            }
        } catch (IOException e) {
            System.exit(1);
            System.out.println("IOException Thrown");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.exit(0);
            System.out.println("Interrupted");
        }
    }
}
