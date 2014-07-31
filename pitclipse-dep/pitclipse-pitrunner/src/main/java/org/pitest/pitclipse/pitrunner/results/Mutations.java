package org.pitest.pitclipse.pitrunner.results;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Mutations implements Serializable {

	private final static long serialVersionUID = 1L;
	protected List<Mutations.Mutation> mutation;

	public List<Mutations.Mutation> getMutation() {
		if (mutation == null) {
			mutation = new ArrayList<Mutations.Mutation>();
		}
		return this.mutation;
	}

	public static class Mutation implements Serializable {

		private final static long serialVersionUID = 1L;
		protected String sourceFile;
		protected String mutatedClass;
		protected String mutatedMethod;
		protected BigInteger lineNumber;
		protected String mutator;
		protected BigInteger index;
		protected String killingTest;
		protected String description;
		protected Boolean detected;
		protected DetectionStatus status;

		public String getSourceFile() {
			return sourceFile;
		}

		public void setSourceFile(String value) {
			this.sourceFile = value;
		}

		public String getMutatedClass() {
			return mutatedClass;
		}

		public void setMutatedClass(String value) {
			this.mutatedClass = value;
		}

		public String getMutatedMethod() {
			return mutatedMethod;
		}

		public void setMutatedMethod(String value) {
			this.mutatedMethod = value;
		}

		public BigInteger getLineNumber() {
			return lineNumber;
		}

		public void setLineNumber(BigInteger value) {
			this.lineNumber = value;
		}

		public String getMutator() {
			return mutator;
		}

		public void setMutator(String value) {
			this.mutator = value;
		}

		public BigInteger getIndex() {
			return index;
		}

		public void setIndex(BigInteger value) {
			this.index = value;
		}

		public String getKillingTest() {
			return killingTest;
		}

		public void setKillingTest(String value) {
			this.killingTest = value;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String value) {
			this.description = value;
		}

		public Boolean isDetected() {
			return detected;
		}

		public void setDetected(Boolean value) {
			this.detected = value;
		}

		public DetectionStatus getStatus() {
			return status;
		}

		public void setStatus(DetectionStatus value) {
			this.status = value;
		}
	}
}
