package core.project.tools;

import java.io.*;

public class FileUtils {
    public static void writeToFile(String json, String path) throws IOException {
        //System.out.println("Saving data");
        FileWriter writer = new FileWriter(path);
        writer.write(json);
        writer.flush();
        writer.close();
    }

    public static String readFromFile(String path) throws IOException {
        //System.out.println("Loading data");
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String json = reader.readLine();
        return json;
    }

    public String readTextFromFile(String filePath, String textCoding){
        StringBuffer buffer = new StringBuffer();
        try (
                FileInputStream myFile = new FileInputStream(filePath);
                InputStreamReader inputStreamReader =
                        new InputStreamReader(myFile, textCoding);
                Reader reader = new BufferedReader(inputStreamReader);){
            int ch; // the code of one character
            while ((ch = reader.read()) > -1) {
                buffer.append((char)ch);
            }
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Unable to read from file " + filePath);
        }

        return buffer.toString();
    }

    public void saveTextToFile(String stringToWrite, String filePath, String textCoding){
        try (FileOutputStream myFile = new FileOutputStream(filePath);
             Writer out = new BufferedWriter(
                     new OutputStreamWriter(myFile, textCoding));) {
            out.write(stringToWrite);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public String getApplicationPath(){
        String result = "N/A";
        try {
            result = new File(".").getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean fileExists(String filePathString){
        File f = new File(filePathString);
        if(f.exists() && !f.isDirectory()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean dirExists(String filePathString){
        File f = new File(filePathString);
        if(f.exists() && f.isDirectory()) {
            return true;
        } else {
            return false;
        }
    }

    public void createFile(String filePathString) throws IOException {
        File f = new File(filePathString);
        f.getParentFile().mkdirs();
        f.createNewFile();
    }

    public void createDir(String dirPathString) throws IOException {
        File f = new File(dirPathString);
        f.mkdirs();
    }
}
