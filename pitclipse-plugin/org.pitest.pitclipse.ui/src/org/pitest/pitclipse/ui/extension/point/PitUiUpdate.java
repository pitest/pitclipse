package org.pitest.pitclipse.ui.extension.point;

public class PitUiUpdate {

	private final String html;

	private PitUiUpdate(String html) {
		this.html = html;
	}

	public String getHtml() {
		return html;
	}

	public static final class Builder {

		private String html;

		public Builder withHtml(String html) {
			this.html = html;
			return this;
		}

		public PitUiUpdate build() {
			return new PitUiUpdate(html);
		}

	}

}
