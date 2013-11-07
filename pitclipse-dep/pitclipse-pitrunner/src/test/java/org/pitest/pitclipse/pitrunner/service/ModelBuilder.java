package org.pitest.pitclipse.pitrunner.service;

import org.pitest.pitclipse.pitrunner.PitResults;
import org.pitest.pitclipse.pitrunner.model.MutationsModel;

public interface ModelBuilder {
	MutationsModel buildFrom(PitResults results);
}
