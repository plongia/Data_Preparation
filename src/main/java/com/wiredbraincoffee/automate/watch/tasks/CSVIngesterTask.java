package com.wiredbraincoffee.automate.watch.tasks;

import com.wiredbraincoffee.SaleTransaction;
import com.wiredbraincoffee.automate.watch.IngesterTask;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class CSVIngesterTask extends IngesterTask {
    private CSVRecord record;

    public CSVIngesterTask(String sourceFilename, String targetFilename) {
        super(sourceFilename, targetFilename);
    }

    private void recordToSale() {
        SaleTransaction sale = new SaleTransaction();
        sale.setUuid(record.get("txid"));
        sale.setTimestamp(record.get("txts"));
        sale.setType(record.get("coffee"));
        sale.setSize(record.get("size"));
        sale.setPrice(record.get("price"));
        sale.setDiscount(record.get("discount"));
        sale.setOffer(record.get("offer"));
        sale.setUserId(Long.parseLong(record.get("userid")));
        sale.setCountry(SaleTransaction.Country.UK);
        sale.setCity("London");
        setSale(sale);
    }

    @Override
    public void run() {

        System.out.println("Came to runnable method and SourceFileName is " + getSourceFilename());

        try (Reader in = new FileReader(getSourceFilename())) {
            Iterable<CSVRecord> records = CSVFormat.Builder.create(CSVFormat.RFC4180)
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build().parse(in);
            for (CSVRecord localRecord : records) {
                System.out.println("Records found!");
                record = localRecord;
                recordToSale();

                System.out.println("Sale record: "
                        + getSale().toString());

                storeSaleInDataSink();
            }
        } catch (IOException e) {
            System.out.println("IOError");
            //e.printStackTrace();ß
        }
    }


}
