package uk.ac.ebi.pride.utilities.trackhub.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.trackhub.TrackhubGenerator;
import uk.ac.ebi.pride.utilities.trackhub.TrackhubRegister;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This will create a test track hub, validate the parameters used, and then remove it after use.
 *
 * @author Tobias Ternent
 */
public class TrackhubTest {

    private static final Logger logger = LoggerFactory.getLogger(TrackhubTest.class);

    private TrackhubGenerator thg;
    private Path tempTrackHub;
    private TrackhubRegister thr;

    /**
     * Sets up a temporary track hub.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        tempTrackHub = Files.createTempDirectory("TempTrackhub");
        tempTrackHub.toFile().deleteOnExit();
        thg = new TrackhubGenerator("hubName", "trackName", "pride.eb.test@gmail.com", "speciesShort", "speciesSci",
                "assembly", TrackhubGenerator.TrackType.bigBed, "http://test.org", "centre", "hubShortLabel","hubLongLabel",
                "trackShortLabel", "trackLongLabel", "trackTaxaID", "trackTissue",
                "trackCellType", "trackDisese", "trackPubDate", "trackInstruments",
                "trackKeywords", "trackOtherOmics", "trackPubReference", tempTrackHub.toFile());
        thr = new TrackhubRegister();
        thr.setServer("https://www.trackhubregistry.org");
        thr.setUser("pride-test");
        thr.setPassword("pride-test");
    }

    /**
     * Deletes the temporary track hub that was created with setUp().
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        boolean deleted = tempTrackHub.toFile().delete(); //not needed when deleteOnExit specified
        logger.info("Manually deleted temp track hub: " + deleted);
    }

    /**
     * Test generating a new track hub.
     * @throws Exception
     */
    @Test
    public void testGenerate() throws Exception {
        thg.createTrackHub();
    }

    /**
     * Test login and logout.
     * @throws Exception
     */
    @Test
    public void testLoginLogout() throws Exception {
        thr.login();
        thr.logout();
    }

}
