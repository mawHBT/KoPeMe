package de.dagere.kopeme.junit5.exampletests.rules;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit5.rule.KoPeMeExtension;

/**
 * An example test für testing whether the KoPeMe-TestRule works correct
 * 
 * @author reichelt
 *
 */
@ExtendWith(KoPeMeExtension.class)
public class ExampleExtensionTestThrowing {

	@Test
	@PerformanceTest(warmup = 3, iterations = 3, repetitions = 1, timeout = 5000000, dataCollectors = "ONLYTIME")
	public void testNormal() {
	   System.out.println("Normal Execution");
	   throw new RuntimeException("Stupid Exception");
	}
}
