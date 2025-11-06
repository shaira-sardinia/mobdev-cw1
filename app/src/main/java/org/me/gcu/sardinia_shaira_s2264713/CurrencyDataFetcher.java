package org.me.gcu.sardinia_shaira_s2264713;

import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class CurrencyDataFetcher {
    private String urlSource;
    private DataFetchListener listener;

    public interface DataFetchListener {
        void onDataFetched(ArrayList<CurrencyItem> currencyList);
        void onFetchError(String error);
    }

    public CurrencyDataFetcher(String urlSource, DataFetchListener listener) {
        this.urlSource = urlSource;
        this.listener = listener;
    }

    public void fetchData() {
        try {
            String xmlData = downloadXml();
            ArrayList<CurrencyItem> currencyList = parseXml(xmlData);

            if (listener != null) {
                listener.onDataFetched(currencyList);
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onFetchError(e.getMessage());
            }
        }
    }

    private String downloadXml() throws IOException {
        StringBuilder result = new StringBuilder();

        URL url = new URL(urlSource);
        URLConnection connection = url.openConnection();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line); // just modify
        }
        reader.close();

        String xmlData = result.toString();
        int start = xmlData.indexOf("<?");
        int end = xmlData.indexOf("</rss>") + 6;

        return xmlData.substring(start, end);
    }

    private ArrayList<CurrencyItem> parseXml(String xmlData)
            throws XmlPullParserException, IOException {

        ArrayList<CurrencyItem> currencyList = new ArrayList<>();
        CurrencyItem currentItem = null;
        boolean insideItem = false;

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new StringReader(xmlData));

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {

            String tagName = parser.getName();

            if (eventType == XmlPullParser.START_TAG) {
                if (tagName.equalsIgnoreCase("item")) {
                    currentItem = new CurrencyItem();
                    insideItem = true;
                }
                else if (insideItem) {
                    String text = parser.nextText();

                    if (tagName.equalsIgnoreCase("title")) {
                        currentItem.setTitle(text);
                    } else if (tagName.equalsIgnoreCase("category")) {
                        currentItem.setCategory(text);
                    } else if (tagName.equalsIgnoreCase("pubDate")) {
                        currentItem.setPubDate(text);
                    } else if (tagName.equalsIgnoreCase("description")) {
                        currentItem.setDescription(text);
                    }
                }
            }
            else if (eventType == XmlPullParser.END_TAG) {
                if (tagName.equalsIgnoreCase("item") && currentItem != null) {
                    currencyList.add(currentItem);
                    insideItem = false;
                }
            }

            eventType = parser.next();
        }

        return currencyList;
    }
}

