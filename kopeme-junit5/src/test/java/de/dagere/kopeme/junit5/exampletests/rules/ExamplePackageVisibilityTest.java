package de.dagere.kopeme.junit5.exampletests.rules;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit.rule.annotations.BeforeWithMeasurement;
import de.dagere.kopeme.junit5.rule.KoPeMeExtension;

/**
 * An example test für testing whether the KoPeMe-TestRule works correct
 * 
 * @author reichelt
 *
 */
@ExtendWith(KoPeMeExtension.class)
public class ExamplePackageVisibilityTest {

   int i = 10;

   @BeforeWithMeasurement
   void setUp() {
      System.out.println("Executing Setup");
      i = 0;
   }

   @Test
   @PerformanceTest(warmup = 3, iterations = 3, repetitions = 1, timeout = 5000000, dataCollectors = "ONLYTIME", executeBeforeClassInMeasurement = true)
   public void testNormal() throws InterruptedException {
      if (i != 0) {
         throw new RuntimeException("i needs to be 0");
      } else {
         Thread.sleep(100);
      }
   }
}
