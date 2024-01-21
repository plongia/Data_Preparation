package com.wiredbraincoffee.clean;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface DataCleaning {

    Pattern saleTxRegex = Pattern.compile(
            "SaleTransaction\\{uuid='?([A-Fa-f0-9\\-]+)'?,timestamp='(.*)',type='?(.*?)'?,size='?(.{1,3}?)'?,price='?(.*?)'?,offer='?(.*?)'?,discount='?(.*?)'?,userId=(\\d*),country='?(.*?)'?,city='?(.*?)'?}"
    );

    Function<String, Matcher> applyRegex = line -> saleTxRegex.matcher(line);

    Predicate<Matcher> onlyMatchingLines = matcher -> {
        if(!matcher.matches()){
            System.out.println("No Match!");
            return false;
        }
        return true;
    };

    Function<Matcher, Map<String,Object>> composeHashMap = matcher -> {
        Map<String,Object> map = new HashMap<>();
        map.put("uuid", matcher.group(1));
        map.put("timestamp", matcher.group(2));
        map.put("type", matcher.group(3));
        map.put("size", matcher.group(4));
        map.put("price", matcher.group(5));
        map.put("offer", matcher.group(6));
        map.put("discount", matcher.group(7));
        map.put("userId", Long.parseLong(matcher.group(8)));
        map.put("country", matcher.group(9));
        map.put("city", matcher.group(10));
        return map;
    };

    Function<Map<String,Object>,Map<String,Object>> properNulls = sale -> {
        String offer = sale.get("offer").toString();
        if (offer != null && (offer.isBlank() || "null".equals(offer))) {
            sale.remove("offer");
        }
        String discount = sale.get("discount").toString();
        if (discount != null && (discount.isBlank() || "null".equals(discount))) {
            sale.remove("discount");
        }
        return sale;
    };

    Predicate<Map<String,Object>> discountAndOfferOnlyWithUserID = sale -> {
        if ((sale.get("discount") != null || sale.get("offer") != null) && (long) sale.get("userId") <= 0) {
            System.out.println("Transaction " + sale.get("uuid") + " has offer or discount with no valid user");
            return false;
        } else {
            return true;
        }
    };
}
