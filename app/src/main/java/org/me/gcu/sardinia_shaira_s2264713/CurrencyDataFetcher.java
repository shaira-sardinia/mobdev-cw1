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

//    @Override
//    public void run(){
//        URL aurl;
//        URLConnection yc;
//        BufferedReader in = null;
//        String inputLine = "";
//
//
//        Log.d("MyTask","in run");
//
//        try
//        {
//            Log.d("MyTask","in try");
//            aurl = new URL(url);
//            yc = aurl.openConnection();
//            in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
//            while ((inputLine = in.readLine()) != null){
//                result = result + inputLine;
//            }
//            in.close();
//        }
//        catch (IOException ae) {
//            Log.e("MyTask", "ioexception");
//        }
//
//        //Clean up any leading garbage characters
//        int i = result.indexOf("<?"); //initial tag
//        result = result.substring(i);
//
//        //Clean up any trailing garbage at the end of the file
//        i = result.indexOf("</rss>"); //final tag
//        result = result.substring(0, i + 6);
//
//        // Now that you have the xml data into result, you can parse it
//        try {
//            boolean insideItem = false;
//
//            XmlPullParserFactory factory =
//                    XmlPullParserFactory.newInstance();
//            factory.setNamespaceAware(true);
//            XmlPullParser xpp = factory.newPullParser();
//            xpp.setInput( new StringReader( result ) );
//            int eventType = xpp.getEventType();
//
//            while (eventType != XmlPullParser.END_DOCUMENT) {
//                if (eventType == XmlPullParser.START_DOCUMENT) {
//                    Log.d("PullParser", "Start of document");
//                } else if (eventType == XmlPullParser.START_TAG) {
//                    if (xpp.getName().equalsIgnoreCase("item")) {
//                        Log.d("Item", "Currency Item found!");
//                        currencyItem = new CurrencyItem();
//                        insideItem = true;
//                    }
//                    else if (xpp.getName().equalsIgnoreCase("title")) {
//                        String temp = xpp.nextText();
//
//                        if (insideItem) {
//                            Log.d("title", "Currencies: " + temp);
//                            currencyItem.setTitle(temp);
//                        } else {
//                            Log.d("Other Title", "This is some other title: " + temp);
//                        }
//                    }
//                    else if (xpp.getName().equalsIgnoreCase("category")) {
//                        String temp = xpp.nextText();
//                        Log.d("title", "Category: " + temp);
//                        currencyItem.setCategory(temp);
//                    }
//                    else if (xpp.getName().equalsIgnoreCase("pubDate")) {
//                        String temp = xpp.nextText();
//                        Log.d("pubDate", "Last updated at: " + temp);
//                        currencyItem.setPubDate(temp);
//                    }
//                    else if (xpp.getName().equalsIgnoreCase("description")) {
//                        String temp = xpp.nextText();
//
//                        if (insideItem) {
//                            Log.d("description", "Description: " + temp);
//                            currencyItem.setDescription(temp);
//                        } else {
//                            Log.d("Other Description", "This is some other description: " + temp);
//                        }
//                    }
//                }
//                else if (eventType == XmlPullParser.END_TAG)
//                {
//                    if (xpp.getName().equalsIgnoreCase("item"))
//                    {
//                        currencyList.add(currencyItem);
//                        insideItem = false;
//                        Log.d("Item","Item parsing completed! Added: " + currencyItem.toString());
//                    }
//                }
//                eventType = xpp.next();
//            }
//        } catch (XmlPullParserException e) {
//            Log.e("Parsing","EXCEPTION" + e);
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            Log.e("Parsing","I/O EXCEPTION" + e);
//            throw new RuntimeException(e);
//        }
//
//        // Now update the TextView to display raw XML data
//        // Probably not the best way to update TextView
//        // but we are just getting started !
//
//        MainActivity.this.runOnUiThread(new Runnable()
//        {
//            public void run() {
//                Log.d("UI thread", "I am the UI thread");
//
//                Log.d("CurrencyList", "Total items: " + currencyList.size());
//                adapter = new CurrencyAdapter(MainActivity.this, currencyList);
//                listView.setAdapter(adapter);
//
//                Log.d("ListView", "Adapter set!");
//            }
//        });
//    }
