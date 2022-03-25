package org.me.gcu.labstuff.cwk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity implements OnClickListener
{
    private TextView rawDataDisplay;
    private Button startButton;
    private String result = "";
    private String url1="";
    // Traffic Scotland Planned Roadworks XML link
    private String urlSource="https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("MyTag","in onCreate");
        // Set up the raw links to the graphical components
        rawDataDisplay = (TextView)findViewById(R.id.rawDataDisplay);
        startButton = (Button)findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

        Log.e("MyTag","after the startButton");
        // More Code goes here

    }

    public void startProgress()
    {
        // Run network access on a separate thread;
        new Thread(new Task(urlSource)).start();


    } //

    @Override
    public void onClick(View v)
    {
        Log.e("MyTag","in onClick");
        startProgress();
        Log.e("MyTag","after startProgress");
    }

    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class Task implements Runnable
    {
        private String url;

        public Task(String aurl)
        {
            url = aurl;
        }
        @Override
        public void run()
        {

            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";


            Log.e("MyTag","in run");

            try
            {
                Log.e("MyTag","in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                Log.e("MyTag","after ready");
                //
                // Now read the data. Make sure that there are no specific hedrs
                // in the data file that you need to ignore.
                // The useful data that you need is in each of the item entries
                //
                while ((inputLine = in.readLine()) != null)
                {
                    result = result + inputLine;
                    Log.e("MyTag",inputLine);

                }
                in.close();
            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception in run");
            }

            //
            // Now that you have the xml data you can parse it
            //

            // Now update the TextView to display raw XML data
            // Probably not the best way to update TextView
            // but we are just getting started !

            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");

                   parseData(result);
                }
            });
        }

    }

    private void parseData(String dataToParse)
    {   ArrayList<trafficInfo> trafficInfos = new ArrayList<>();
        trafficInfo tInfo = null;
        try
        {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput( new StringReader( dataToParse ) );
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                // Found a start tag
                if(eventType == XmlPullParser.START_TAG)
                {
                    // Check which Tag we have
                    if (xpp.getName().equalsIgnoreCase("title"))
                    {
                        // Now just get the associated text
                        String title = xpp.nextText();
                        // Do something with text
                        tInfo.setTitle(title);
                    }
                    else
                        // Check which Tag we have
                        if (xpp.getName().equalsIgnoreCase("description"))
                        {
                            // Now just get the associated text
                            String desc = xpp.nextText();
                            // Do something with text
                            tInfo.setDescription(desc);
                        }
                        else
                            // Check which Tag we have
                            if (xpp.getName().equalsIgnoreCase("link"))
                            {
                                // Now just get the associated text
                                String link = xpp.nextText();
                                // Do something with text
                                tInfo.setLink(link);
                            }
                            else
                                // Check which Tag we have
                                if (xpp.getName().equalsIgnoreCase("georss:point"))
                                {
                                    // Now just get the associated text
                                    String georss = xpp.nextText();
                                    // Do something with text
                                    tInfo.setGeorss(georss);
                                }
                                else
                                    // Check which Tag we have
                                    if (xpp.getName().equalsIgnoreCase("pubDate"))
                                    {
                                        // Now just get the associated text
                                        String pubDate = xpp.nextText();
                                        // Do something with text
                                        tInfo.setPubDate(pubDate);
                                    }
                }

                // Get the next event
                eventType = xpp.next();

            } // End of while

        }
        catch (XmlPullParserException ae1)
        {
            Log.e("MyTag","Parsing error" + ae1.toString());
        }
        catch (IOException ae1)
        {
            Log.e("MyTag","IO error during parsing");
        }

        Log.e("MyTag","End document");


        printInfo(trafficInfos);
    }

    private void printInfo(ArrayList<trafficInfo> trafficInfos) {
StringBuilder builder = new StringBuilder();

for(trafficInfo tInfo: trafficInfos){
    builder.append(tInfo.getTitle()).
            append("\n").
            append(tInfo.getDescription()).
            append("\n").
            append(tInfo.getLink()).
            append("\n").
            append(tInfo.getGeorss()).
            append("\n").
            append(tInfo.getPubDate());


}
        rawDataDisplay.setText(builder.toString());
    }


}