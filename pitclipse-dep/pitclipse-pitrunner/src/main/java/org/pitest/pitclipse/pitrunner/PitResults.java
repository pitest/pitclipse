package org.pitest.pitclipse.pitrunner;

import java.io.File;
import java.io.Serializable;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.pitest.pitclipse.pitrunner.PitOptions.PitLaunchException;
import org.pitest.pitclipse.pitrunner.results.Mutations;
import org.pitest.pitclipse.pitrunner.results.ObjectFactory;

@Immutable
public final class PitResults implements Serializable {

	private static final JAXBContext MUTATIONS_CONTEXT = theJaxbContext();
	
	private static final long serialVersionUID = 5271802933404287709L;
	private final File htmlResultFile;
	private final File xmlResultFile;

	private final PitOptions options;

	private final Mutations mutations;

	private PitResults(PitOptions options, File htmlResultFile, File xmlResultFile, Mutations mutations) {
		this.options = options;
		this.htmlResultFile = htmlResultFile;
		this.xmlResultFile = xmlResultFile;
		this.mutations = mutations;
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
		private PitOptions options = null;

		public PitResults build() {
			validateResultsFile();
			Mutations mutations = null;
			try {
				Unmarshaller unmarshaller = MUTATIONS_CONTEXT.createUnmarshaller();
				mutations = (Mutations) unmarshaller.unmarshal(xmlResultFile);
			} catch (JAXBException e) {
				mutations = new ObjectFactory().createMutations();
			}
			return new PitResults(options, htmlResultFile, xmlResultFile, mutations);
		}

		public PitResultsBuilder withHtmlResults(File htmlResultFile) {
			this.htmlResultFile = new File(htmlResultFile.getPath());
			return this;
		}

		public PitResultsBuilder withXmlResults(File xmlResultFile) {
			this.xmlResultFile = new File(xmlResultFile.getPath());
			return this;
		}
		
		public PitResultsBuilder withPitOptions(PitOptions options) {
			this.options = options;
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
				throw new PitLaunchException("File does not exist: "
						+ htmlResultFile);
			}
			if (!xmlResultFile.exists()) {
				throw new PitLaunchException("File does not exist: "
						+ xmlResultFile);
			}
		}
	}

	@Override
	public String toString() {
		return "PitResults [htmlResultFile=" + htmlResultFile
				+ ", xmlResultFile=" + xmlResultFile + "]";
	}

	public Mutations getMutations() {
		return mutations;
	}

	public PitOptions getPitOptions() {
		return options;
	}

}
