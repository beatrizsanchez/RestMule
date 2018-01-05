package org.epsilonlabs.rescli.test.github;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.epsilonlabs.rescli.test.util.MDE;
import org.junit.Test;

public class MDETest {

	private static final Logger LOG = LogManager.getLogger(GitHubTest.class);

	@Test
	public void mde(){
		for (MDE m : MDE.values()){
			LOG.info(m.query());
		}
	}
}
