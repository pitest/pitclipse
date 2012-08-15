package org.pitest.pitclipse.ui.extension;

import java.net.URI;

import org.eclipse.swt.browser.Browser;

public class PitResultComponent {

	private final Browser browser;
	private final URI uri;

	public PitResultComponent(Browser browser, URI uri) {
		this.browser = browser;
		this.uri = uri;
	}

	public Browser getBrowser() {
		return browser;
	}

	public URI getUri() {
		return uri;
	}

}
