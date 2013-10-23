package org.pitest.pitclipse.pitrunner;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.pitest.pitclipse.pitrunner.PitOptions.PitLaunchException;
import org.pitest.pitclipse.pitrunner.results.Mutations;
import org.pitest.pitclipse.pitrunner.results.ObjectFactory;

import com.google.common.collect.ImmutableList;

@Immutable
public final class PitResults implements Serializable {
	private static final long serialVersionUID = 5457147591186148047L;

	private static final JAXBContext MUTATIONS_CONTEXT = theJaxbContext();

	private final File htmlResultFile;
	private final File xmlResultFile;

	private final Mutations mutations;

	private final ImmutableList<String> projects;

	private PitResults(File htmlResultFile, File xmlResultFile, Mutations mutations, ImmutableList<String> projects) {
		this.htmlResultFile = htmlResultFile;
		this.xmlResultFile = xmlResultFile;
		this.mutations = mutations;
		this.projects = projects;
	};

	private static JAXBContext theJaxbContext() {
		try {
			return JAXBContext.newInstance(Mutations.class);
		} catch (JAXBException e) {
			throw new RuntimeException("Unable to create a JAXB context", e);
		}
	}

	public File getHtmlResultFile() {
		return htmlResultFile;
	}

	public File getXmlResultFile() {
		return xmlResultFile;
	}

	public static final class PitResultsBuilder {
		private File htmlResultFile = null;
		private File xmlResultFile = null;
		private ImmutableList<String> projects = ImmutableList.of();

		private PitResultsBuilder() {
		}

		public PitResults build() {
			validateResultsFile();
			Mutations mutations = null;
			try {
				Unmarshaller unmarshaller = MUTATIONS_CONTEXT.createUnmarshaller();
				mutations = (Mutations) unmarshaller.unmarshal(xmlResultFile);
			} catch (JAXBException e) {
				mutations = new ObjectFactory().createMutations();
			}
			return new PitResults(htmlResultFile, xmlResultFile, mutations, projects);
		}

		public PitResultsBuilder withHtmlResults(File htmlResultFile) {
			this.htmlResultFile = new File(htmlResultFile.getPath());
			return this;
		}

		public PitResultsBuilder withXmlResults(File xmlResultFile) {
			this.xmlResultFile = new File(xmlResultFile.getPath());
			return this;
		}

		private void validateResultsFile() {
			if (null == htmlResultFile) {
				throw new PitLaunchException("PIT HTML Result File not set");
			}
			if (null == xmlResultFile) {
				throw new PitLaunchException("PIT XML Result File not set");
			}
			if (!htmlResultFile.exists()) {
				throw new PitLaunchException("File does not exist: " + htmlResultFile);
			}
			if (!xmlResultFile.exists()) {
				throw new PitLaunchException("File does not exist: " + xmlResultFile);
			}
		}

		public PitResultsBuilder withProjects(List<String> projects) {
			this.projects = ImmutableList.copyOf(projects);
			return this;
		}
	}

	@Override
	public String toString() {
		return "PitResults [htmlResultFile=" + htmlResultFile + ", xmlResultFile=" + xmlResultFile + ", projects="
				+ projects + "]";
	}

	public Mutations getMutations() {
		return mutations;
	}

	public static PitResultsBuilder builder() {
		return new PitResultsBuilder();
	}

	public ImmutableList<String> getProjects() {
		return projects;
	}

}
