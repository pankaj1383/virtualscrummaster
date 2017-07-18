package com.aws.lambda.schedular.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;




public class Utils
{
  public Utils() {}
  
  public static String readFile(String fileName)
  {
    StringBuilder out = new StringBuilder();
    Reader inputStreamReader = null;
    try
    {
      File inputFile = new File(Utils.class.getResource(fileName).getFile());
      
      Logger.log("Utils.readFile -> inputFile " + inputFile);
      
      FileInputStream istream = new FileInputStream(inputFile);
      
      int bufferSize = 1024;
      char[] buffer = new char['Ѐ'];
      
      inputStreamReader = new InputStreamReader(istream, "UTF-8");
      for (;;)
      {
        int rsz = inputStreamReader.read(buffer, 0, buffer.length);
        if (rsz < 0)
          break;
        out.append(buffer, 0, rsz);
      }
      
      return out.toString();
    }
    catch (UnsupportedEncodingException e) {
      Logger.log("Utils.readFile -> UnsupportedEncodingException " + e.getMessage());
    } catch (IOException e) {
      Logger.log("Utils.readFile -> IOException " + e.getMessage());
    }
    finally {
      try {
        if (inputStreamReader != null) {
          inputStreamReader.close();
        }
      } catch (IOException e) {
        Logger.log("Utils.readFile -> IOException while closing inputStreamReader: " + e.getMessage());
      }
      inputStreamReader = null;
    }
    return null;
  }
  





  public static String readResourceFile(String fileName)
  {
    Reader inputStreamReader = null;
    try
    {
      Path path = Paths.get(Utils.class.getResource("/").toURI());
      Logger.log("Utils.readResourceFile -> path " + path);
      

      String resourceLoc = path + "/resources/" + fileName;
      
      InputStream inputStream = new FileInputStream(resourceLoc);
      
      Logger.log("Utils.readResourceFile -> inputStream " + inputStream);
      
      int bufferSize = 1024;
      char[] buffer = new char['Ѐ'];
      StringBuilder out = new StringBuilder();
      inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
      for (;;) {
        int rsz = inputStreamReader.read(buffer, 0, buffer.length);
        if (rsz < 0)
          break;
        out.append(buffer, 0, rsz);
      }
      return out.toString();
    }
    catch (URISyntaxException use) {
      Logger.log("Utils.readResourceFile -> URISyntaxException " + use.getMessage());
    } catch (UnsupportedEncodingException e) {
      Logger.log("Utils.readResourceFile -> UnsupportedEncodingException " + e.getMessage());
    } catch (IOException e) {
      Logger.log("Utils.readResourceFile-> IOException  " + e.getMessage());
    }
    finally {
      try {
        if (inputStreamReader != null) {
          inputStreamReader.close();
        }
      } catch (IOException e) {
        Logger.log("Utils.readResourceFile -> IOException while closing inputStreamReader: " + e.getMessage());
      }
      inputStreamReader = null;
    }
    return null;
  }
}
