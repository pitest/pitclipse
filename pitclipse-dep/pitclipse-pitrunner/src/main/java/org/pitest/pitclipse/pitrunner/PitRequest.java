package org.pitest.pitclipse.pitrunner;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;

public class PitRequest implements Serializable {
	private static final long serialVersionUID = 2058881520214195050L;
	private final PitOptions options;
	private final ImmutableList<String> projects;

	private PitRequest(PitOptions options, ImmutableList<String> projects) {
		this.options = options;
		this.projects = projects;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {

		private PitOptions options;
		private ImmutableList<String> projects = ImmutableList.of();

		private Builder() {
		}

		public PitRequest build() {
			return new PitRequest(options, projects);
		}

		public Builder withPitOptions(PitOptions options) {
			this.options = options;
			return this;
		}

		public Builder withProjects(List<String> projects) {
			this.projects = ImmutableList.copyOf(projects);
			return this;
		}
	}

	public File getReportDirectory() {
		return options.getReportDirectory();
	}

	public PitOptions getOptions() {
		return options;
	}

	public List<String> getProjects() {
		return projects;
	}

	@Override
	public String toString() {
		return "PitRequest [options=" + options + ", projects=" + projects + "]";
	}
}
