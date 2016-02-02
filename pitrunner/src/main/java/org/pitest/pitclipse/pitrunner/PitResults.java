package org.pitest.pitclipse.pitrunner;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import org.pitest.pitclipse.pitrunner.PitOptions.PitLaunchException;
import org.pitest.pitclipse.pitrunner.results.Mutations;
import org.pitest.pitclipse.pitrunner.results.ObjectFactory;
import org.pitest.pitclipse.pitrunner.results.summary.SummaryResult;
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;

public final class PitResults implements Serializable {
	private static final long serialVersionUID = 5457147591186148047L;

	private final File htmlResultFile;

	private final Mutations mutations;

	private final ImmutableList<String> projects;

	private final SummaryResult summaryResult;

	private PitResults(File htmlResultFile, File xmlResultFile, Mutations mutations, SummaryResult summaryResult,
			ImmutableList<String> projects) {
		this.htmlResultFile = htmlResultFile;
		this.mutations = mutations;
		this.summaryResult = summaryResult;
		this.projects = projects;
	};

	public File getHtmlResultFile() {
		return htmlResultFile;
	}

	public static final class Builder {

		private File htmlResultFile = null;
		private final File xmlResultFile = null;
		private ImmutableList<String> projects = ImmutableList.of();
		private Mutations mutations = new ObjectFactory().createMutations();
		private SummaryResult summaryResult = SummaryResult.EMPTY;

		private Builder() {
		}

		public PitResults build() {
			return new PitResults(htmlResultFile, xmlResultFile, mutations, summaryResult, projects);
		}

		public Builder withHtmlResults(File htmlResultFile) {
			checkFileExists(htmlResultFile);
			this.htmlResultFile = new File(htmlResultFile.getPath());
			return this;
		}

		private void checkFileExists(File file) {
			if (!file.exists()) {
				throw new PitLaunchException("File does not exist: " + file);
			}
		}

		public Builder withProjects(List<String> projects) {
			this.projects = ImmutableList.copyOf(projects);
			return this;
		}

		public Builder withMutations(Mutations mutations) {
			this.mutations = mutations;
			return this;
		}

		public Builder withSummaryResult(SummaryResult summaryResult) {
			this.summaryResult = summaryResult;
			return this;
		}
	}

	@Override
	public String toString() {
		return "PitResults [htmlResultFile=" + htmlResultFile + ", projects=" + projects + "]";
	}

	public Mutations getMutations() {
		return mutations;
	}

	public static Builder builder() {
		return new Builder();
	}

	public ImmutableList<String> getProjects() {
		return projects;
	}

}
