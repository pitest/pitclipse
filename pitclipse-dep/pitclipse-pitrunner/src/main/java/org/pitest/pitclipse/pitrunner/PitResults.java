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
import org.pitest.pitclipse.reloc.guava.collect.ImmutableList;

@Immutable
public final class PitResults implements Serializable {
	private static final long serialVersionUID = 5457147591186148047L;

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

	public File getHtmlResultFile() {
		return htmlResultFile;
	}

	public File getXmlResultFile() {
		return xmlResultFile;
	}

	public static final class Builder {

		private static final JAXBContext MUTATIONS_CONTEXT = theJaxbContext();

		private static JAXBContext theJaxbContext() {
			try {
				return JAXBContext.newInstance(Mutations.class);
			} catch (JAXBException e) {
				throw new RuntimeException("Unable to create a JAXB context", e);
			}
		}

		private File htmlResultFile = null;
		private File xmlResultFile = null;
		private ImmutableList<String> projects = ImmutableList.of();
		private Mutations mutations = new ObjectFactory().createMutations();

		private Builder() {
		}

		public PitResults build() {
			return new PitResults(htmlResultFile, xmlResultFile, mutations, projects);
		}

		public Builder withHtmlResults(File htmlResultFile) {
			checkFileExists(htmlResultFile);
			this.htmlResultFile = new File(htmlResultFile.getPath());
			return this;
		}

		public Builder withXmlResults(File xmlResultFile) {
			checkFileExists(xmlResultFile);
			this.xmlResultFile = new File(xmlResultFile.getPath());
			try {
				Unmarshaller unmarshaller = MUTATIONS_CONTEXT.createUnmarshaller();
				mutations = (Mutations) unmarshaller.unmarshal(xmlResultFile);
			} catch (JAXBException e) {
				mutations = new ObjectFactory().createMutations();
			}
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
	}

	@Override
	public String toString() {
		return "PitResults [htmlResultFile=" + htmlResultFile + ", xmlResultFile=" + xmlResultFile + ", projects="
				+ projects + "]";
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
