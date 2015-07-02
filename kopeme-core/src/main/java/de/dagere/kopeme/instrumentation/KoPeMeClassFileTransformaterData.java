package de.dagere.kopeme.instrumentation;

public class KoPeMeClassFileTransformaterData {
	static final String DEFAULT_ARG_SEPARATOR = ";;";
	
	private String instrumentableClass;
	private String instrumentableMethod;
	private String codeBefore;
	private String codeAfter;
	private int level;

	public KoPeMeClassFileTransformaterData(final String instrumentableClass,
			final String instrumentableMethod, final String codeBefore, final String codeAfter,
			final int level) {
		this.instrumentableClass = instrumentableClass;
		this.instrumentableMethod = instrumentableMethod;
		this.codeBefore = codeBefore;
		this.codeAfter = codeAfter;
		this.level = level;
	}

	public KoPeMeClassFileTransformaterData(String agentArgs) {
		this(agentArgs.split(DEFAULT_ARG_SEPARATOR));
	}
	
	private KoPeMeClassFileTransformaterData(String[] args) {
		this(args[0].trim(), args[1].trim(), args[2].trim(), args[3].trim(), Integer.parseInt(args[4].trim()));
	}

	public String getInstrumentableClass() {
		return instrumentableClass;
	}

	public String getInstrumentableMethod() {
		return instrumentableMethod;
	}

	public String getCodeBefore() {
		return codeBefore;
	}

	public String getCodeAfter() {
		return codeAfter;
	}

	public int getLevel() {
		return level;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((codeAfter == null) ? 0 : codeAfter.hashCode());
		result = prime * result
				+ ((codeBefore == null) ? 0 : codeBefore.hashCode());
		result = prime
				* result
				+ ((instrumentableClass == null) ? 0 : instrumentableClass
						.hashCode());
		result = prime
				* result
				+ ((instrumentableMethod == null) ? 0 : instrumentableMethod
						.hashCode());
		result = prime * result + level;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KoPeMeClassFileTransformaterData other = (KoPeMeClassFileTransformaterData) obj;
		if (codeAfter == null) {
			if (other.codeAfter != null)
				return false;
		} else if (!codeAfter.equals(other.codeAfter))
			return false;
		if (codeBefore == null) {
			if (other.codeBefore != null)
				return false;
		} else if (!codeBefore.equals(other.codeBefore))
			return false;
		if (instrumentableClass == null) {
			if (other.instrumentableClass != null)
				return false;
		} else if (!instrumentableClass.equals(other.instrumentableClass))
			return false;
		if (instrumentableMethod == null) {
			if (other.instrumentableMethod != null)
				return false;
		} else if (!instrumentableMethod.equals(other.instrumentableMethod))
			return false;
		if (level != other.level)
			return false;
		return true;
	}
	
	
}