package it.soluzione1.testzebraemdk.utility;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.util.Xml;

import androidx.core.content.ContextCompat;

import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.StatusPrinter;
import it.soluzione1.testzebraemdk.MyCustomApplication;

public class Utility {
    private static final String TAG = Utility.class.getName();
    private static final org.slf4j.Logger _logger = LoggerFactory.getLogger(Utility.class);

    public  static  boolean isZebraDevice(){
        return (android.os.Build.MANUFACTURER.contains("Zebra Technologies") || android.os.Build.MANUFACTURER.contains("Motorola Solutions") );
    }

    public static boolean isZebraPS20Device(){
        return isZebraDevice() && Build.MODEL.startsWith("PS20");
    }

    public static boolean isZebraMC18Device(){
        return isZebraDevice() && Build.MODEL.startsWith("MC18");
    }

    public static void logException(String tag, Exception e, org.slf4j.Logger logger, String msg) {
        logger.error(msg, e);
        Log.e(tag, msg + ": " + e.getMessage() + " - Stacktrace: " + Arrays.toString(e.getStackTrace()));
        //FirebaseCrashlytics.getInstance().recordException(e);
    }

    public static void log(org.slf4j.Logger logger, String msg){
        logger.debug(msg);
        //FirebaseCrashlytics.getInstance().log(msg);
    }


    public static void configureLogbackDirectly() {
        // reset the default context (which may already have been initialized)
        // since we want to reconfigure it
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.stop();
        final File[] appsDir= ContextCompat.getExternalFilesDirs(MyCustomApplication.getAppContext(),null);

        final String LOG_DIR = appsDir[0] + "/logback/" ;

        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<ILoggingEvent>();
        rollingFileAppender.setAppend(true);
        rollingFileAppender.setContext(context);

        // OPTIONAL: Set an active log file (separate from the rollover files).
        // If rollingPolicy.fileNamePattern already set, you don't need this.
        rollingFileAppender.setFile(LOG_DIR + "/log.txt");

        TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<ILoggingEvent>();
        rollingPolicy.setFileNamePattern(LOG_DIR + "/log.%d.zip");
        rollingPolicy.setMaxHistory(7);
        rollingPolicy.setParent(rollingFileAppender);  // parent and context required!
        rollingPolicy.setContext(context);
        rollingPolicy.start();

        rollingFileAppender.setRollingPolicy(rollingPolicy);

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
        encoder.setContext(context);
        encoder.start();

        rollingFileAppender.setEncoder(encoder);
        rollingFileAppender.start();

        // add the newly created appenders to the root logger;
        // qualify Logger to disambiguate from org.slf4j.Logger
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.DEBUG);
        root.addAppender(rollingFileAppender);

        // print any status messages (warnings, etc) encountered in logback config
        StatusPrinter.print(context);
    }

    public static String[] readProfiles(Context context) throws IOException, XmlPullParserException {
        List<String> profiles = new ArrayList<>();

        // Read the EMDKConfig
        BufferedReader reader = null;
        String xml = "";
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open("EMDKConfig.xml")));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                //process line
                xml += mLine;

            }
        } catch (Exception e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }

        if(xml != null)
            profiles = parseXML(xml);

        return  (String[]) profiles.toArray(new String[profiles.size()]);
    }

    private static List<String> parseXML(String xml) throws XmlPullParserException, IOException {
        List<String> profiles = new ArrayList<>();

        int event;

        XmlPullParser parser = Xml.newPullParser();
        // Provide the string response to the String Reader that reads
        // for the parser
        parser.setInput(new StringReader(xml));

        // Retrieve error details if parm-error/characteristic-error in the response XML
        event = parser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            String name = parser.getName();
            switch (event) {
                case XmlPullParser.START_TAG:

                    if(name.equals("parm") && parser.getAttributeValue(null, "name").equals("ProfileName"))
                        profiles.add(parser.getAttributeValue(null, "value"));

                    break;
                case XmlPullParser.END_TAG:

                    break;
            }

            event = parser.next();
        }


        return  profiles;
    }
}
