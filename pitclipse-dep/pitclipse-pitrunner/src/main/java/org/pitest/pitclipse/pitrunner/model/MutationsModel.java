package org.pitest.pitclipse.pitrunner.model;

import static org.pitest.pitclipse.reloc.guava.collect.ImmutableList.copyOf;

import java.util.List;

import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;

public class MutationsModel implements Visitable, Countable {

	public static final MutationsModel EMPTY_MODEL = make(ImmutableList.<Status> of());

	private final ImmutableList<Status> statuses;

	private MutationsModel(ImmutableList<Status> statuses) {
		this.statuses = statuses;
	}

	public static MutationsModel make(List<Status> statuses) {
		return new MutationsModel(copyOf(statuses));
	}

	public List<Status> getStatuses() {
		return statuses;
	}

	@Override
	public <T> T accept(MutationsModelVisitor<T> visitor) {
		return visitor.visitModel(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((statuses == null) ? 0 : statuses.hashCode());
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
		MutationsModel other = (MutationsModel) obj;
		if (statuses == null) {
			if (other.statuses != null)
				return false;
		} else if (!statuses.equals(other.statuses))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MutationsModel [statuses=" + statuses + "]";
	}

	@Override
	public long count() {
		long sum = 0L;
		for (Status status : statuses) {
			sum += status.count();
		}
		return sum;
	}
}
