package org.pitest.pitclipse.pitrunner.model;

import static com.google.common.collect.ImmutableList.copyOf;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class ClassMutations implements Visitable {
	private final String className;
	private final ImmutableList<Mutation> mutations;

	private ClassMutations(String className, ImmutableList<Mutation> mutations) {
		this.className = className;
		this.mutations = mutations;
	}

	@Override
	public <T> T accept(MutationsModelVisitor<T> visitor) {
		return visitor.visitClass(this);
	}

	public String getClassName() {
		return className;
	}

	public List<Mutation> getMutations() {
		return mutations;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String className;
		private ImmutableList<Mutation> mutations = ImmutableList.of();

		private Builder() {
		}

		public Builder withClassName(String className) {
			this.className = className;
			return this;
		}

		public Builder withMutations(Iterable<Mutation> mutations) {
			this.mutations = copyOf(mutations);
			return this;
		}

		public ClassMutations build() {
			return new ClassMutations(className, mutations);
		};
	}

}
