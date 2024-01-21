package com.wiredbraincoffee.automate.watch;

import com.wiredbraincoffee.SaleTransaction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public abstract class IngesterTask implements Runnable {
    private final String sourceFilename;
    private final String targetFilename;
    private SaleTransaction sale;

    public IngesterTask(String sourceFilename, String targetFilename) {
        this.sourceFilename = sourceFilename;
        this.targetFilename = targetFilename;
    }

    public String getSourceFilename() {
        return sourceFilename;
    }

    public String getTargetFilename() {
        return targetFilename;
    }

    protected void setSale(SaleTransaction sale) {
        this.sale = sale;
    }

    protected SaleTransaction getSale() {
        return sale;
    }

    protected void storeSaleInDataSink() throws IOException {
        try {
            System.out.println("[" + Thread.currentThread().getName() + "] Storing "
                    + getSale().toString());
            String saleString = getSale().toString() + "\n";
            System.out.println("target file name: " + Paths.get(getTargetFilename()));
            Files.write(
                    Paths.get(getTargetFilename()),
                    saleString.getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND
            );

        } catch (IOException e) {
            System.out.println("IOError: IngesterTask.java");
            e.printStackTrace();
        }

    }
}
