package de.dagere.kopeme.datastorage;

import java.io.File;
import java.io.IOException;

import de.dagere.kopeme.datastorage.xml.XMLConversionLoader;
import de.dagere.kopeme.junit.rule.annotations.KoPeMeConstants;
import de.dagere.kopeme.kopemedata.DatacollectorResult;
import de.dagere.kopeme.kopemedata.Kopemedata;

public class JSONDataLoader implements DataLoader {

   private final File file;

   public JSONDataLoader(File file) {
      this.file = file;
   }

   public Kopemedata getFullData() {
      return loadData(file);
   }

   public DatacollectorResult getData(String collectorName) {
      Kopemedata data = getFullData();
      DatacollectorResult result = JSONDataStorer.findCollector(collectorName, data.getMethods().get(0));
      return result;
   }

   public static Kopemedata loadData(File file, int warmup) {
      throw new RuntimeException("Not implemented yet");
   }
   
   public static Kopemedata loadWarmedupData(File file) {
      throw new RuntimeException("Not implemented yet");
   }
   
   public static Kopemedata loadData(File file2) {
      try {
         if (file2.getName().endsWith(".json")) {
            if (file2.exists()) {
               return KoPeMeConstants.OBJECTMAPPER.readValue(file2, Kopemedata.class);
            } else {
               return new Kopemedata("");
            }
         } else if (file2.getName().endsWith(".xml")) {
            Kopemedata kopemedata = XMLConversionLoader.loadData(file2);
            String pureFileName = file2.getName().substring(0, file2.getName().length()- ".xml".length());
            File jsonFile = new File(file2.getParentFile(), pureFileName + ".json");
            JSONDataStorer.storeData(jsonFile, kopemedata);
            file2.delete();
            return kopemedata;
         } else {
            return null;
         }
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

}
