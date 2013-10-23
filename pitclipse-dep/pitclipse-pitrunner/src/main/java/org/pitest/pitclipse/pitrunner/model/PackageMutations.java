package org.pitest.pitclipse.pitrunner.model;

import com.google.common.collect.ImmutableList;

public class PackageMutations implements Visitable {

	private final String packageName;
	private final ImmutableList<ClassMutations> classMutations;

	private PackageMutations(String packageName, ImmutableList<ClassMutations> mutations) {
		this.packageName = packageName;
		this.classMutations = mutations;
	}

	public String getPackageName() {
		return packageName;
	}

	public ImmutableList<ClassMutations> getClassMutations() {
		return classMutations;
	}

	@Override
	public <T> T accept(MutationsModelVisitor<T> visitor) {
		return visitor.visitPackage(this);
	}

	public static class Builder {
		private String packageName;
		private ImmutableList<ClassMutations> mutations;

		private Builder() {
		}

		public Builder withPackageName(String packageName) {
			this.packageName = packageName;
			return this;
		}

		public Builder withClassMutations(Iterable<ClassMutations> mutations) {
			this.mutations = ImmutableList.copyOf(mutations);
			return this;
		}

		public PackageMutations build() {
			return new PackageMutations(packageName, mutations);
		};
	}

	public static Builder builder() {
		return new Builder();
	}
}
