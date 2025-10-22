/*  Starter project for Mobile Platform Development - 1st diet 25/26
    You should use this project as the starting point for your assignment.
    This project simply reads the data from the required URL and displays the
    raw data in a TextField
*/

//
// Name                 Shaira Sardinia
// Student ID           S2264713
// Programme of Study   BSc (Hons) Software Development (GA)
//

package org.me.gcu.sardinia_shaira_s2264713;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ListView;
import android.view.View.OnClickListener;
import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity implements OnClickListener {
    private TextView rawDataDisplay;
    private CurrencyAdapter adapter;
    private ListView listView;
    private Button startButton;
    private String result;
    private String url1="";
    private String urlSource="https://www.fx-exchange.com/gbp/rss.xml";
    private ArrayList<CurrencyItem> currencyList;

    private CurrencyItem currencyItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currencyList = new ArrayList<CurrencyItem>();

        // Set up the raw links to the graphical components
//        rawDataDisplay = (TextView)findViewById(R.id.rawDataDisplay);
//        currencyListView = (ListView)findViewById(R.id.currencyListView);

        listView = findViewById(R.id.listView);
        startButton = (Button)findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
    }

    public void onClick(View aview)
    {
        startProgress();
    }

    public void startProgress()
    {
        // Run network access on a separate thread;
        new Thread(new Task(urlSource)).start();
    } //

    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class Task implements Runnable
    {
        private String url;
        public Task(String aurl){
            url = aurl;
        }
        @Override
        public void run(){
            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";


            Log.d("MyTask","in run");

            try
            {
                Log.d("MyTask","in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                while ((inputLine = in.readLine()) != null){
                    result = result + inputLine;
                }
                in.close();
            }
            catch (IOException ae) {
                Log.e("MyTask", "ioexception");
            }

            //Clean up any leading garbage characters
            int i = result.indexOf("<?"); //initial tag
            result = result.substring(i);

            //Clean up any trailing garbage at the end of the file
            i = result.indexOf("</rss>"); //final tag
            result = result.substring(0, i + 6);

            // Now that you have the xml data into result, you can parse it
            try {
                boolean insideItem = false;

                XmlPullParserFactory factory =
                        XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( new StringReader( result ) );
                int eventType = xpp.getEventType();

//              PARSING HERE!!!

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        Log.d("PullParser", "Start of document");
                    } else if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            Log.d("Item", "Currency Item found!");
                            currencyItem = new CurrencyItem();
                            insideItem = true;
                        }
                        else if (xpp.getName().equalsIgnoreCase("title")) {
                            String temp = xpp.nextText();

                            if (insideItem) {
                                Log.d("title", "Currencies: " + temp);
                                currencyItem.setTitle(temp);
                            } else {
                                Log.d("Other Title", "This is some other title: " + temp);
                            }
                        }
                        else if (xpp.getName().equalsIgnoreCase("category")) {
                            String temp = xpp.nextText();
                            Log.d("title", "Category: " + temp);
                            currencyItem.setCategory(temp);
                        }
                        else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                            String temp = xpp.nextText();
                            Log.d("pubDate", "Last updated at: " + temp);
                            currencyItem.setPubDate(temp);
                        }
                        else if (xpp.getName().equalsIgnoreCase("description")) {
                            String temp = xpp.nextText();

                            if (insideItem) {
                                Log.d("description", "Description: " + temp);
                                currencyItem.setDescription(temp);
                            } else {
                                Log.d("Other Description", "This is some other description: " + temp);
                            }
                        }
                    }
                    else if (eventType == XmlPullParser.END_TAG)
                    {
                        if (xpp.getName().equalsIgnoreCase("item"))
                        {
                            currencyList.add(currencyItem);
                            insideItem = false;
                            Log.d("Item","Item parsing completed! Added: " + currencyItem.toString());
                        }
                    }
                    eventType = xpp.next();
                }
            } catch (XmlPullParserException e) {
                Log.e("Parsing","EXCEPTION" + e);
                throw new RuntimeException(e);
            } catch (IOException e) {
                Log.e("Parsing","I/O EXCEPTION" + e);
                throw new RuntimeException(e);
            }

            // Now update the TextView to display raw XML data
            // Probably not the best way to update TextView
            // but we are just getting started !

            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");

                    Log.d("CurrencyList", "Total items: " + currencyList.size());
                    adapter = new CurrencyAdapter(MainActivity.this, currencyList);
                    listView.setAdapter(adapter);

                    Log.d("ListView", "Adapter set!");
                }
            });
        }
    }
}