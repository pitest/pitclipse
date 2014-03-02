package org.pitest.pitclipse.pitrunner.model;

import static org.pitest.pitclipse.reloc.guava.collect.ImmutableList.copyOf;

import java.util.List;

import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;

public class ClassMutations implements Visitable, Countable {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((mutations == null) ? 0 : mutations.hashCode());
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
		ClassMutations other = (ClassMutations) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (mutations == null) {
			if (other.mutations != null)
				return false;
		} else if (!mutations.equals(other.mutations))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ClassMutations [className=" + className + ", mutations=" + mutations + "]";
	}

	@Override
	public long count() {
		return mutations.size();
	}

}
