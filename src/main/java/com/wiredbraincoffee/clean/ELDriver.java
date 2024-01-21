package com.wiredbraincoffee.clean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import com.wiredbraincoffee.clean.DataCleaning;

public class ELDriver implements DataCleaning {
    public static void main(String[] args) {

        String fileName = "wiredbraincoffee/datasink/sales-global.dat";
        try(Stream<String> stream = Files.lines(Paths.get(fileName))){
            stream
                    .map(applyRegex)
                    .filter(onlyMatchingLines)
                    .map(composeHashMap)
                    .map(properNulls)
                    .filter(discountAndOfferOnlyWithUserID)
                    .forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
