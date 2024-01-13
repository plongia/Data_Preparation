package com.wiredbraincoffee;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class CSVDataIngester {

    public static SaleTransaction recordToSale(CSVRecord record) {
        SaleTransaction sale = new SaleTransaction();
        sale.setUuid(record.get("txid"));
        sale.setTimestamp(record.get("txts"));
        sale.setType(record.get("coffee"));
        sale.setSize(record.get("size"));
        sale.setPrice(record.get("price"));
        sale.setDiscount(record.get("discount"));
        sale.setOffer(record.get("offer"));
        sale.setUserId(Long.parseLong(record.get("userid")));
        return sale;
    }

    public static void main(String[] args){

        try(Reader in = new FileReader("/Users/prathamlongia/Desktop/Gradle/projects/wiredbraincoffee/src/main/java/data_files/salesdata-uk.csv")) {
            Iterable<CSVRecord> records = CSVFormat.Builder.create(CSVFormat.RFC4180)
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(in);
            for (CSVRecord record : records) {
                System.out.println(record);
                SaleTransaction saleObj = recordToSale(record);
                System.out.println(saleObj);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
