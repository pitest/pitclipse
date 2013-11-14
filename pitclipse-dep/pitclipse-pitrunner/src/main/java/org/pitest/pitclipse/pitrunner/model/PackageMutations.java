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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((classMutations == null) ? 0 : classMutations.hashCode());
		result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
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
		PackageMutations other = (PackageMutations) obj;
		if (classMutations == null) {
			if (other.classMutations != null)
				return false;
		} else if (!classMutations.equals(other.classMutations))
			return false;
		if (packageName == null) {
			if (other.packageName != null)
				return false;
		} else if (!packageName.equals(other.packageName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PackageMutations [packageName=" + packageName + ", classMutations=" + classMutations + "]";
	}
}
