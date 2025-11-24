package org.me.gcu.sardinia_shaira_s2264713.data;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;

import org.me.gcu.sardinia_shaira_s2264713.R;

public class CurrencyItem {

    private static final String TAG = "CurrencyItem";
    private String title;
    private String category;
    private String pubDate;
    private String description;

    private String sourceCurrencyName;
    private String sourceCurrencyCode;
    private String targetCurrencyName;
    private String targetCurrencyCode;
    private double exchangeRate;
    private boolean isParsed = false;

    public CurrencyItem() {
    }

    /**
     * Getter Methods
     */
    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getDescription() {
        return description;
    }

    public String getSourceCurrencyName() {
        parseData();
        return sourceCurrencyName;
    }

    public String getSourceCurrencyCode() {
        parseData();
        return sourceCurrencyCode;
    }

    public String getTargetCurrencyName() {
        parseData();
        return targetCurrencyName;
    }

    public String getTargetCurrencyCode() {
        parseData();
        return targetCurrencyCode;
    }

    public double getExchangeRate() {
        parseData();
        return exchangeRate;
    }

    /**
     * Setter Methods
     */
    public void setTitle(String temp) {
        this.title = temp;
        this.isParsed = false;
    }

    public void setCategory(String temp) {
        this.category = temp;
    }

    public void setPubDate(String temp) {
        this.pubDate = temp;
    }

    public void setDescription(String temp) {
        this.description = temp;
        this.isParsed = false;
    }

    /**
     * Extract currency information from data
     */
    private void parseData() {
        if (isParsed) return;

        try {
            if (title != null && title.contains("(") && title.contains(")")) {
                String[] parts = title.split("/");

                String sourcePart = parts[0].trim();
                int sourceCodeStart = sourcePart.lastIndexOf("(");
                int sourceCodeEnd = sourcePart.lastIndexOf(")");

                sourceCurrencyName = sourcePart.substring(0, sourceCodeStart).trim();
                sourceCurrencyCode = sourcePart.substring(sourceCodeStart + 1, sourceCodeEnd).trim();

                if (parts.length > 1) {
                    String targetPart = parts[1].trim();
                    int targetCodeStart = targetPart.lastIndexOf("(");
                    int targetCodeEnd = targetPart.lastIndexOf(")");

                    targetCurrencyName = targetPart.substring(0, targetCodeStart).trim();
                    targetCurrencyCode = targetPart.substring(targetCodeStart + 1, targetCodeEnd).trim();
                }
            }

            if (description != null && description.contains("=")) {
                String[] descParts = description.split("=");
                if (descParts.length > 1) {
                    String ratePart = descParts[1].trim();
                    String rateStr = ratePart.split(" ")[0].trim();
                    exchangeRate = Double.parseDouble(rateStr);
                }
            }

            Log.d(TAG, "Original title: " + title);
            Log.d(TAG, "Parsed sourceName: " + sourceCurrencyName);
            Log.d(TAG, "Parsed targetName: " + targetCurrencyName);

            isParsed = true;

        } catch (Exception e) {
            sourceCurrencyCode = "GBP";
            targetCurrencyCode = "UNKNOWN";
            exchangeRate = 0.0;
            isParsed = true;
        }
    }

    /**
     *  Formatted String Methods
     */
    public String getFormattedTitle() {
        parseData();
        return sourceCurrencyName + " / " + targetCurrencyName;
    }

    @SuppressLint("DefaultLocale")
    public String getFormattedDescription() {
        parseData();
        return String.format("1 %s = %.4f %s", sourceCurrencyCode, exchangeRate, targetCurrencyCode);
    }

    @SuppressLint("DefaultLocale")
    public String getHomePageFormat() {
        parseData();
        return String.format("1 %s -> %.4f %s", sourceCurrencyCode, exchangeRate, targetCurrencyCode);
    }

    @SuppressLint("DefaultLocale")
    public String getRateWithCode() {
        parseData();
        return String.format("%.4f %s", exchangeRate, targetCurrencyCode);
    }

    public int getRateColor() {
        parseData();

        if (exchangeRate >= 100) {
            return Color.rgb(34, 139, 34);
        } else if (exchangeRate >= 10) {
            return Color.rgb(255, 140, 0);
        } else if (exchangeRate >= 1) {
            return Color.rgb(220, 20, 60);
        } else {
            return Color.rgb(30, 144, 255);
        }
    }

    public int getFlagResourceId(android.content.Context context) {
        parseData();
        if (targetCurrencyCode == null || context == null) {
            return 0;
        }
        String drawableName = targetCurrencyCode.toLowerCase();

        try {
            Class<?> drawableClass = R.drawable.class;
            java.lang.reflect.Field field = drawableClass.getField(drawableName);
            return field.getInt(null);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Search methods
     */
    public boolean matchesSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            return true;
        }

        parseData();
        String lowerQuery = query.toLowerCase().trim();

        /* Check currency codes */
        if (sourceCurrencyCode != null && sourceCurrencyCode.toLowerCase().contains(lowerQuery)) {
            return true;
        }
        if (targetCurrencyCode != null && targetCurrencyCode.toLowerCase().contains(lowerQuery)) {
            return true;
        }

        /* Check full currency names */
        if (sourceCurrencyName != null && sourceCurrencyName.toLowerCase().contains(lowerQuery)) {
            return true;
        }
        if (targetCurrencyName != null && targetCurrencyName.toLowerCase().contains(lowerQuery)) {
            return true;
        }

        return false;
    }

    public String getSearchableString() {
        parseData();
        return String.format(
                sourceCurrencyCode, targetCurrencyCode,
                sourceCurrencyName, targetCurrencyName).toLowerCase();
    }

    @NonNull
    @Override
    public String toString() {
        return "CurrencyItem{" +
                "title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", rate=" + getExchangeRate() +
                ", codes=" + getSourceCurrencyCode() + "/" + getTargetCurrencyCode() +
                '}';
    }
}
