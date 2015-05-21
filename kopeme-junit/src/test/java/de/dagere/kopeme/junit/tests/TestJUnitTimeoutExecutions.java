package de.dagere.kopeme.junit.tests;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import de.dagere.kopeme.junit.exampletests.runner.ExampleJUnitTests;
import de.dagere.kopeme.junit.exampletests.runner.ExampleMethodTimeoutTest;
import de.dagere.kopeme.junit.exampletests.runner.classtimeout.ExampleClassTimeoutTest;
import de.dagere.kopeme.junit.exampletests.runner.classtimeout.MultipleCallClassTimeout;
import de.dagere.kopeme.junit.exampletests.runner.classtimeout.NoTimeoutOutput;

/**
 * Beginn einer Implementierung einer Klasse, die prüft, ob Tests entsprechende Ergebnisse liefern
 * 
 * @author reichelt
 * 
 */
public class TestJUnitTimeoutExecutions {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	public static Logger log = LogManager.getLogger(TestJUnitTimeoutExecutions.class);

	@Test(timeout = 600)
	public void testClassTimeout() {
		JUnitCore jc = new JUnitCore();
		jc.run(ExampleClassTimeoutTest.class);
	}

	@Test(timeout = 1600)
	public void testMethodTimeout() {
		JUnitCore jc = new JUnitCore();
		jc.run(ExampleMethodTimeoutTest.class);
	}

	@Test
	public void testNoTimeout() {
		JUnitCore jc = new JUnitCore();
		Result r = jc.run(NoTimeoutOutput.class);
		Assert.assertEquals(2, r.getRunCount());
		Assert.assertEquals(0, r.getFailureCount());
	}

	@Test
	public void testMultipleClassTimeout() throws InterruptedException {
		JUnitCore jc = new JUnitCore();
		Result r = jc.run(MultipleCallClassTimeout.class);
		assertFailureDocumentation(r);
	}

	private void assertFailureDocumentation(Result r) {
		List<Failure> failures = r.getFailures();
		assertEquals(5, r.getFailureCount());
		int countTimeoutException = 0, countInterruptedException = 0;
		for (Failure f : failures) {
			Class<? extends Throwable> execeptionType = f.getException().getClass();
			if (execeptionType.isAssignableFrom(TimeoutException.class)) {
				// this exception should occur four times:
				// for each test one time (we have three in the example) + one for the whole class
				countTimeoutException++;
				assertEquals("Test timed out because of class timeout", f.getMessage());
			} else if (execeptionType.isAssignableFrom(InterruptedException.class)) { // this should be there one time because we interrupt the current test
				countInterruptedException++;
			}
		}
		assertEquals(4, countTimeoutException);
		assertEquals(1, countInterruptedException);
	}

	public void testNormalJUnitExecution() {
		JUnitCore jc = new JUnitCore();
		jc.run(ExampleJUnitTests.class);
	}
}
