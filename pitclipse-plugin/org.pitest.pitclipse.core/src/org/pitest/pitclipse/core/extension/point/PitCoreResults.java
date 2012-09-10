package org.pitest.pitclipse.core.extension.point;

import java.net.URI;

public class PitCoreResults {
	private final URI uri;

	public PitCoreResults(URI uri) {
		this.uri = uri;
	}

	public URI getUri() {
		return uri;
	}

}
