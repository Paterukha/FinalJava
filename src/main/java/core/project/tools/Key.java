package core.project.tools;

import java.io.*;
import java.util.Properties;


public class Key {
    private static final String PATH_TO_KEY = "settings/config.properties";
    public static String myKey;

    public static void store(){
        Properties prop = new Properties();
        OutputStream output = null;

        try {

            output = new FileOutputStream(PATH_TO_KEY);

            // set the properties value
            prop.setProperty("key", "AIzaSyDwu_AH-9_PNHCKIiIzJ-uqXGwNWOfAURw");

            // save properties to project root folder
            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    public static void load(){
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream(PATH_TO_KEY);

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            myKey = prop.getProperty("key");

        } catch (IOException ex) {
            //ex.printStackTrace();
            System.out.println("The system cannot find the file with the key (" + PATH_TO_KEY + ")");
            System.out.println("It's impossible to retrieve youtube statistics without key.");
            System.out.println("Please provide the missing file " + PATH_TO_KEY);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
