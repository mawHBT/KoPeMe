package de.dagere.kopeme;

import static de.dagere.kopeme.PerformanceTestUtils.saveData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.annotations.Assertion;
import de.dagere.kopeme.annotations.MaximalRelativeStandardDeviation;
import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.datacollection.TestResult;
import de.dagere.kopeme.datastorage.SaveableTestData;

/**
 * Represents an execution of all runs of one test.
 * 
 * @author dagere
 * 
 */
public class PerformanceTestRunner {

	private static Logger log = LogManager.getLogger(PerformanceTestRunner.class);

	protected final Class klasse;
	protected final Object instanz;
	protected final Method method;
	protected int executionTimes, warmupExecutions, minEarlyStopExecutions, timeout;
	protected Map<String, Double> maximalRelativeStandardDeviation;
	protected Map<String, Long> assertationvalues;
	protected String filename;

	/**
	 * Initializes the PerformanceTestRunner.
	 * 
	 * @param klasse
	 * @param instance
	 * @param method
	 */
	public PerformanceTestRunner(Class klasse, Object instance, Method method) {
		this.klasse = klasse;
		this.instanz = instance;
		this.method = method;

		PerformanceTest annotation = method.getAnnotation(PerformanceTest.class);

		if (annotation != null) {
			executionTimes = annotation.executionTimes();
			warmupExecutions = annotation.warmupExecutions();
			minEarlyStopExecutions = annotation.minEarlyStopExecutions();
			timeout = annotation.timeout();
			maximalRelativeStandardDeviation = new HashMap<>();

			for (MaximalRelativeStandardDeviation maxDev : annotation.deviations()) {
				maximalRelativeStandardDeviation.put(maxDev.collectorname(), maxDev.maxvalue());
			}

			assertationvalues = new HashMap<>();
			for (Assertion a : annotation.assertions()) {
				assertationvalues.put(a.collectorname(), a.maxvalue());
			}
		}

		filename = klasse.getName();
		log.info("Executing Performancetest: " + filename);
	}

	public void evaluate() throws Throwable {
		final Thread mainThread = new Thread(new Runnable() {
			@Override
			public void run() {
				TestResult tr = new TestResult(method.getName(), warmupExecutions);
				try {
					if (method.getParameterTypes().length == 1) {
						tr = executeComplexTest(tr);

					} else {
						tr = executeSimpleTest(tr);
					}
					if (!assertationvalues.isEmpty()) {
						tr.checkValues(assertationvalues);
					}
				} catch (IllegalAccessException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		TimeBoundedExecution tbe = new TimeBoundedExecution(mainThread, timeout);
		tbe.execute();

		log.trace("Test {} beendet", filename);
	}

	private TestResult executeComplexTest(TestResult tr) throws IllegalAccessException, InvocationTargetException {
		Object[] params = { tr };
		runWarmup(params);
		try {
			if (!PerformanceTestUtils.checkCollectorValidity(tr, assertationvalues, maximalRelativeStandardDeviation)) {
				log.warn("Not all Collectors are valid!");
			}
			tr = new TestResult(method.getName(), executionTimes);
			params[0] = tr;
			PerformanceKoPeMeStatement pts = new PerformanceKoPeMeStatement(method, instanz, false, params, tr);
			runMainExecution(pts, tr, params);
		} catch (Throwable t) {
			tr.finalizeCollection();
			saveData(SaveableTestData.createErrorTestData(method.getName(), filename, tr, true));
			throw t;
		}
		saveData(SaveableTestData.createFineTestData(method.getName(), filename, tr, true));
		tr.checkValues();
		return tr;
	}

	private TestResult executeSimpleTest(TestResult tr) throws IllegalAccessException, InvocationTargetException {

		Object[] params = {};
		runWarmup(params);
		tr = new TestResult(method.getName(), executionTimes);

		if (!PerformanceTestUtils.checkCollectorValidity(tr, assertationvalues, maximalRelativeStandardDeviation)) {
			log.warn("Not all Collectors are valid!");
		}
		long start = System.currentTimeMillis();
		try {
			PerformanceKoPeMeStatement pts = new PerformanceKoPeMeStatement(method, instanz, true, params, tr);
			runMainExecution(pts, tr, params);
		} catch (Throwable t) {
			tr.finalizeCollection();
			saveData(SaveableTestData.createErrorTestData(method.getName(), filename, tr, true));
			throw t;
		}
		log.trace("Zeit: " + (System.currentTimeMillis() - start));
		tr.finalizeCollection();
		saveData(SaveableTestData.createFineTestData(method.getName(), filename, tr, true));
		tr.checkValues();

		return tr;
	}

	private void runWarmup(Object[] params) throws IllegalAccessException, InvocationTargetException {
		String methodString = method.getClass().getName() + "." + method.getName();
		for (int i = 1; i <= warmupExecutions; i++) {
			log.info("--- Starting warmup execution " + methodString + " - " + i + "/" + warmupExecutions + " ---");
			method.invoke(instanz, params);
			log.info("--- Stopping warmup execution " + i + "/" + warmupExecutions + " ---");
		}
	}

	private void runMainExecution(PerformanceKoPeMeStatement pts, TestResult tr, Object[] params) throws IllegalAccessException, InvocationTargetException {
		String methodString = method.getClass().getName() + "." + method.getName();
		int executions;
		for (executions = 1; executions <= executionTimes; executions++) {
			log.debug("--- Starting execution " + methodString + " " + executions + "/" + executionTimes + " ---");
			pts.evaluate();
			log.debug("--- Stopping execution " + executions + "/" + executionTimes + " ---");
			for (Map.Entry<String, Double> entry : maximalRelativeStandardDeviation.entrySet()) {
				log.debug("Entry: {} Aim: {} Value: {}", entry.getKey(), entry.getValue(), tr.getRelativeStandardDeviation(entry.getKey()));
			}
			if (executions >= minEarlyStopExecutions && !maximalRelativeStandardDeviation.isEmpty()
					&& tr.isRelativeStandardDeviationBelow(maximalRelativeStandardDeviation)) {
				break;
			}
		}
		log.debug("Executions: " + executions);
		tr.setRealExecutions(executions);
	}
}
