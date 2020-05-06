package de.dagere.kopeme.datastorage;

import java.io.IOException;
import java.util.Scanner;

public class EnvironmentUtil {
   public static String getCPU() {
      String result = "";
      if (!System.getProperty("os.name").startsWith("Windows")) {
         try {
            Process process = new ProcessBuilder("/bin/sh", "-c", "cat /proc/cpuinfo | grep \"model name\" | uniq").start();
            try (Scanner scanner = new Scanner(process.getInputStream())) {
               result = scanner.useDelimiter("\\A").next().replace("\t", " ").replace("  ", "");
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      } else {
         result = "";
      }
      return result;
   }
   
   public static String getMemory() {
      String result = "";
      if (!System.getProperty("os.name").startsWith("Windows")) {
         try {
            Process process = new ProcessBuilder("/bin/sh", "-c", "cat /proc/meminfo | grep \"MemTotal\"").start();
            try (Scanner scanner = new Scanner(process.getInputStream())) {
               result = scanner.useDelimiter("\\A").next().replace("\t", " ").replace("  ", "");
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      } else {
         result = "";
      }
      return result;
   }
}
