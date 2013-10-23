package org.pitest.pitclipse.pitrunner.model;

import static com.google.common.collect.ImmutableList.copyOf;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class MutationsModel implements Visitable {

	public static final MutationsModel EMPTY_MODEL = make(ImmutableList.<ProjectMutations> of());

	private final List<ProjectMutations> projectMutations;

	private MutationsModel(List<ProjectMutations> projectMutations) {
		this.projectMutations = copyOf(projectMutations);
	}

	public static MutationsModel make(List<ProjectMutations> projectMutations) {
		return new MutationsModel(projectMutations);
	}

	public List<ProjectMutations> getProjectMutations() {
		return copyOf(projectMutations);
	}

	@Override
	public <T> T accept(MutationsModelVisitor<T> visitor) {
		return visitor.visitModel(this);
	}

}
