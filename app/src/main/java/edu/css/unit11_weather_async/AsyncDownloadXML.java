package edu.css.unit11_weather_async;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static org.xmlpull.v1.XmlPullParser.TYPES;

public class AsyncDownloadXML extends AsyncTask<MainActivity, String, String> {

    MainActivity mainActivityLink;

    //Implementation of AsyncTask used to download XML feed
    // This method is run in a separate thread.  Do not do any UI stuff here.
    // Calls onPostExecute when done and passes it the return value or String
    @Override
    protected String doInBackground(MainActivity... new_actWeather) {
        try {
            Log.v("== CIS 3334 ==","AsyncDownloadXML doInBackground");
            // Save a pointer to the main Weather Activity which is passed in as a parameter
            mainActivityLink = new_actWeather[0];

            // create the XML Pull Parser
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            // get the weather api
            String  weatherStrURL =  "http://api.openweathermap.org/data/2.5/weather?zip=" + mainActivityLink.getLocation() + "us&appid=5aa6c40803fbb300fe98c6728bdafce7&mode=xml&units=imperial";
            //create the url from the string you already created
            URL weatherURL =  new URL(weatherStrURL);
            // read the data from the url
            InputStream stream = weatherURL.openStream();

            xpp.setInput(stream, null);
            //set up the event type of the parser
            int eventType = xpp.getEventType();

            String tempStr = "Updating...";			// Temperature Update String
            String windStr = "Updating...";			// Wind Update String
            publishProgress(tempStr,windStr);
            // log message for the AsyncDownload
            Log.v("== CIS 3334 ==","AsyncDownloadXML repeat until end of document arrives");
            // loop while the download is going
            while (eventType != XmlPullParser.END_DOCUMENT) {
                Log.v("== CIS 3334 ==","AsyncDownloadXML eventType = "+TYPES[eventType]);
                // look for a start tag
                if(eventType == XmlPullParser.START_TAG) {

                    // get the tag name and process it
                    String tag = xpp.getName();
                    Log.v("== CIS 3334 ==","Start tag found with name = "+tag);
                    if (tag.equals("speed")){
                        // XML should look like: <speed value="11.41" name="Strong breeze"/>

                        // ======= CIS 3334 add code here to process wing speed =======

                        windStr = xpp.getAttributeValue(null, "value");
                        Log.v("== CIS 3334 ==","Wind =" + windStr);
                        publishProgress(tempStr,windStr);	// Update the display

                    }
                    if (tag.equals("temperature")){
                        // XML should look like: <temperature value="37.47" min="33.8" max="41" unit="fahrenheit"/>
                        tempStr = xpp.getAttributeValue(null, "value");
                        Log.v("== CIS 3334 ==","Temp =" + tempStr);
                        publishProgress(tempStr,windStr);	// Update the display
                    }
                }
                eventType = xpp.next();
            }
           return "Successfully updated weather";
          // error checking
        } catch (IOException e) {
            Log.v("== CIS 3334 -- ERROR ==","AsyncDownloadXML doInBackground IOException");
            Log.v("== CIS 3334 -- ERROR ==",e.getMessage());
            return(e.getMessage());
        } catch (XmlPullParserException e) {
            Log.v("== CIS 3334 -- ERROR ==","AsyncDownloadXML doInBackground XmlPullParserException");
            Log.v("== CIS 3334 -- ERROR ==",e.getMessage());
            return(e.getMessage());
        }  catch (Exception e) {
            Log.v("== CIS 3334 -- ERROR ==","AsyncDownloadXML doInBackground Exception");
            Log.v("== CIS 3334 -- ERROR ==",e.getMessage());
            return(e.getMessage());
        }
    }

    @Override
    protected void onProgressUpdate(String... update) {
        Log.v("== CIS 3334 ==","in onProgressUpdate");
       // send the temp and wind to the main to update the text fields
        mainActivityLink.setTemp(update[0]);
        mainActivityLink.setWind(update[1]);
    }

    @Override
    protected void onPostExecute(String result) {
        Log.v("== CIS 3334 ==", "in onPostExecute");
        // send the status to the main for it to be updated.
        mainActivityLink.setStatus(result);
    }

}


