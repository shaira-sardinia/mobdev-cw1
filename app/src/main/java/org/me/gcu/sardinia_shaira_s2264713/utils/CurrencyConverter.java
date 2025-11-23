package org.me.gcu.sardinia_shaira_s2264713.utils;

import org.me.gcu.sardinia_shaira_s2264713.data.CurrencyItem;

import java.util.ArrayList;
import java.util.Locale;

public class CurrencyConverter {

    public static CurrencyItem findCurrencyByCode(String currencyCode, ArrayList<CurrencyItem> currencyList) {
        if (currencyCode == null || currencyList == null) {
            return null;
        }

        String searchCode = currencyCode.trim().toUpperCase(Locale.US);

        for (CurrencyItem item : currencyList) {
            if (searchCode.equals(item.getTargetCurrencyCode())) {
                return item;
            }
        }
        return null;
    }

    /**
     * Convert Method
     */
    public static double convert(String fromCurrencyCode, String toCurrencyCode,
                                 double amount, ArrayList<CurrencyItem> currencyList) {

        // validation
        if (amount < 0 || currencyList == null || currencyList.isEmpty()) {
            return 0;
        }

        if (fromCurrencyCode != null && fromCurrencyCode.equalsIgnoreCase(toCurrencyCode)) {
            return amount;
        }

        if ("GBP".equalsIgnoreCase(fromCurrencyCode)) {
            return convertFromGBP(toCurrencyCode, amount, currencyList);
        }

        if ("GBP".equalsIgnoreCase(toCurrencyCode)) {
            return convertToGBP(fromCurrencyCode, amount, currencyList);
        }

        return convertBetweenCurrencies(fromCurrencyCode, toCurrencyCode, amount, currencyList);
    }

    /**
     * Convert from GBP to another currency
     */
    private static double convertFromGBP(String toCurrencyCode, double amount,
                                         ArrayList<CurrencyItem> currencyList) {
        CurrencyItem toItem = findCurrencyByCode(toCurrencyCode, currencyList);

        if (toItem == null) {
            return 0;
        }
        return amount * toItem.getExchangeRate();
    }

    /* Convert to GBP from another currency */
    private static double convertToGBP(String fromCurrencyCode, double amount,
                                       ArrayList<CurrencyItem> currencyList) {
        CurrencyItem fromItem = findCurrencyByCode(fromCurrencyCode, currencyList);

        if (fromItem == null || fromItem.getExchangeRate() == 0) {
            return 0;
        }
        return amount / fromItem.getExchangeRate();
    }

    /* Convert between any two currencies */
    private static double convertBetweenCurrencies(String fromCurrencyCode, String toCurrencyCode,
                                                   double amount, ArrayList<CurrencyItem> currencyList) {
        double amountInGBP = convertToGBP(fromCurrencyCode, amount, currencyList);

        if (amountInGBP == 0) {
            return 0;
        }

        return convertFromGBP(toCurrencyCode, amountInGBP, currencyList);
    }

    /* Format conversion result as a readable string */
    public static String formatConversionResult(String fromCode, String toCode,
                                                double fromAmount, double toAmount) {
        return String.format(Locale.US, "%.2f %s = %.2f %s",
                fromAmount, fromCode, toAmount, toCode);
    }

    /* Format result amount with currency code */
    public static String formatAmount(double amount, String currencyCode) {
        return String.format(Locale.US, "%.2f %s", amount, currencyCode);
    }

    /* Get exchange rate between two currencies */
    public static double getExchangeRate(String fromCurrencyCode, String toCurrencyCode,
                                         ArrayList<CurrencyItem> currencyList) {
        if (fromCurrencyCode != null && fromCurrencyCode.equalsIgnoreCase(toCurrencyCode)) {
            return 1.0;
        }
        return convert(fromCurrencyCode, toCurrencyCode, 1.0, currencyList);
    }

    /* Validate if a currency code exists in list */
    public static boolean isValidCurrency(String currencyCode, ArrayList<CurrencyItem> currencyList) {
        if ("GBP".equalsIgnoreCase(currencyCode)) {
            return true;
        }
        return findCurrencyByCode(currencyCode, currencyList) != null;
    }

    /* Get all available currency codes from list */
    public static ArrayList<String> getAllCurrencyCodes(ArrayList<CurrencyItem> currencyList) {
        ArrayList<String> codes = new ArrayList<>();
        codes.add("GBP");

        if (currencyList != null) {
            for (CurrencyItem item : currencyList) {
                String code = item.getTargetCurrencyCode();
                if (code != null && !codes.contains(code)) {
                    codes.add(code);
                }
            }
        }
        java.util.Collections.sort(codes);

        return codes;
    }
}