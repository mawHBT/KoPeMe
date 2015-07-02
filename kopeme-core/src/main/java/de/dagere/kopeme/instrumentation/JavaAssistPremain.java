package de.dagere.kopeme.instrumentation;

import java.lang.instrument.Instrumentation;

import javassist.NotFoundException;

public class JavaAssistPremain {

	public static void premain(String agentArgs, Instrumentation inst) throws NotFoundException {
		System.out.println(agentArgs); // TODO print args
		System.out.println("Starting the javassist agent...");
		inst.addTransformer(new KoPeMeClassFileTransformater(new KoPeMeClassFileTransformaterData(agentArgs)));
	}
	
}
