package uk.ac.ebi.pride.utilities.trackhub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *  This will either create a new track hub, or update an existing track with a new track.
 * If track hub and track already exists, the old track will be removed, and the new track added.
 *
 * @author Tobias Ternent
 */
public class TrackhubGenerator {
    public static final Logger logger = LoggerFactory.getLogger(TrackhubGenerator.class);

    private final String HUB_TXT = "hub.txt";
    private final String GENOMES_TXT = "genomes.txt";
    private final String TRACKDB_TXT = "trackDb.txt";


    private String hubName;
    private String trackName;
    private String emailAddress;
    private String speciesShort;
    private String speciesSci;
    private String assembly;
    private String fileType;
    private String bigDataURL;
    private String centre;
    private String hubShortLabel;
    private String hubLongLabel;
    private String trackShortLabel;
    private String trackLongLabel;
    private String trackTaxaID;
    private String trackTissue;
    private String trackCellType;
    private String trackDisese;
    private String trackPubDate;
    private String trackInstruments;
    private String trackKeywords;
    private String trackOtherOmics;
    private String trackPubReference;
    private File trackhub;
    private TrackType trackType;

    /**
     * Enum for allowable track hub types.
     */
    public enum TrackType {
        bigBed, bigGenePred, bigChain, bigPsl, bigMaf, bigWig, BAM, CRAM, HAL , VCF
    }

    /**
     * Default constructor for the TrackhubGenerator class. Parameters will need to be set separately.
     */
    public TrackhubGenerator() {
    }

    /**
     * Constructor for the TrackhubGenerator class. Parameters are all included here.
     *
     *     @param hubName The name for the hub.
     *     @param trackName The name for the track.
     *     @param emailAddress The contact email address.
     *     @param speciesShort The short name for the species,
     *     @param speciesSci The full scientific name for the species.
     *     @param assembly The assembly version.
     *     @param trackType The file type of the track.
     *     @param bigDataURL The URL that points to the track data file. This may be relative or absolute.
     *     @param centre The centre affiliation for the data.
     *     @param hubShortLabel The short name for the track hub.
     *     @param hubLongLabel The long name for the track hub.
     *     @param trackShortLabel The short name for the track.
     *     @param trackLongLabel The long name for the track.
     *     @param trackTaxaID The taxonomy ID for the species.
     *     @param trackTissue The tissue for the track.
     *     @param trackCellType The cell type for the track.
     *     @param trackDisese The disease for the track.
     *     @param trackPubDate The publication date for the track.
     *     @param trackInstruments The instrument date for the track.
     *     @param trackKeywords The keywords date for the track.
     *     @param trackOtherOmics The other -omics accessions date for the track.
     *     @param trackPubReference The publication references date for the track.
     *     @param trackhub The target directory for the track hub.
     *
     */
    public TrackhubGenerator(String hubName, String trackName, String emailAddress, String speciesShort, String speciesSci,
                             String assembly, TrackType trackType, String bigDataURL, String centre, String hubShortLabel, String hubLongLabel,
                             String trackShortLabel, String trackLongLabel, String trackTaxaID, String trackTissue,
                             String trackCellType, String trackDisese, String trackPubDate, String trackInstruments,
                             String trackKeywords, String trackOtherOmics, String trackPubReference, File trackhub) {
        logger.debug("TrackhubGenerator params: " + hubName + ", " + trackName + ", " + emailAddress + ", " + speciesShort + ", " + speciesSci + ", " +
                assembly + ", " + trackType + ", " + bigDataURL + ", " + centre + ", " + hubShortLabel + ", " + hubShortLabel + ", " +
                trackShortLabel + ", " +  trackLongLabel + ", " + trackTaxaID + ", " + trackTissue + ", " +
                trackCellType + ", " +  trackDisese + ", " +  trackPubDate + ", " + trackInstruments + ", " +
                trackKeywords + ", " + trackOtherOmics + ", " + trackPubReference + ", " + trackhub.getPath());
        this.hubName = hubName;
        this.trackName = trackName;
        this.emailAddress = emailAddress;
        this.speciesShort = speciesShort;
        this.speciesSci = speciesSci;
        this.assembly = assembly;
        this.trackType = trackType;
        this.bigDataURL = bigDataURL;
        this.centre = centre;
        this.hubShortLabel = hubShortLabel;
        this.hubLongLabel = hubLongLabel;
        this.trackShortLabel = trackShortLabel;
        this.trackLongLabel = trackLongLabel;
        this.trackhub = trackhub;
        this.trackTaxaID = trackTaxaID;
        this.trackTissue = trackTissue;
        this.trackCellType = trackCellType;
        this.trackDisese = trackDisese;
        this.trackPubDate = trackPubDate;
        this.trackInstruments = trackInstruments;
        this.trackKeywords = trackKeywords;
        this.trackOtherOmics = trackOtherOmics;
        this.trackPubReference = trackPubReference;
    }

    /**
     * This method first validates the parameters, and then creates/updates the track hub.
     */
    public void createTrackHub() {
        try {
            if (validateParams()) {
                start();
            }
        } catch (IOException ioe) {
            logger.error("Error while creating/updating track hub." , ioe);
        }
    }

    /**
     * This method starts ti create/updatesthe track hub.
     */
    private void start() throws IOException{
        File hubTxt = new File(trackhub.getPath() + File.separator + speciesSci + File.separator + HUB_TXT);
        hubTxt.getParentFile().mkdirs();
        if (!hubTxt.exists()) {
            Writer fileWriter = new FileWriter(hubTxt);
            fileWriter.write("hub " + hubName + "\n");
            fileWriter.write("shortLabel " + hubShortLabel + "\n");
            fileWriter.write("longLabel " + hubLongLabel + "\n");
            fileWriter.write("genomesFile " + GENOMES_TXT + "\n");
            fileWriter.write("email " + emailAddress + "\n");
            fileWriter.close();
            logger.info("Created hub.txt file");
        } //else hub.txt file exists, don't need to update it

        File genomesTxt = new File(trackhub.getPath() + File.separator + speciesSci + File.separator + GENOMES_TXT);
        if (!genomesTxt.exists()) {
            Writer fileWriter = new FileWriter(genomesTxt);
            fileWriter.write("genome " + assembly + "\n");
            fileWriter.write("trackDb " + assembly + "/" + TRACKDB_TXT + "\n");
            fileWriter.close();
            logger.info("Created genomes.txt file");
        } //else genomes.txt file exists, don't need to update it

        File assemblyDir = new File(trackhub.getPath() + File.separator + speciesSci + File.separator + assembly);
        if (!assemblyDir.exists()) {
            assemblyDir.mkdirs();
        }

        File trackDbTxt = new File(trackhub.getPath() + File.separator + speciesSci + File.separator + assembly
                + File.separator + TRACKDB_TXT);
        trackDbTxt.getParentFile().mkdirs();
        if (!trackDbTxt.exists()) {
            writeTrack(trackDbTxt, false);
        } else { // file exists, remove track entry if present, then append to end of file
            BufferedReader reader = Files.newBufferedReader(trackDbTxt.toPath(), Charset.defaultCharset());
            File tempOutput = new File(trackDbTxt.getPath() + ".tmp");
            Writer fileWriter = new FileWriter(tempOutput);
            String line;
            boolean foundExisting = false;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("track " + trackName)){
                    fileWriter.write(line + "\n");
                } else {
                    foundExisting = true;
                    logger.info("Found existing track in trackDB.txt: " + "track " + trackName);
                    while ((line = reader.readLine()) != null) {
                        if (line.isEmpty()) {
                            break;
                        }
                    }
                }
            }
            reader.close();
            fileWriter.close();
            if (foundExisting) {
                Files.move(tempOutput.toPath(), trackDbTxt.toPath(), StandardCopyOption.REPLACE_EXISTING);
                logger.info("Removed track entry in existing hub.");
            } else {
                tempOutput.delete();
            }
            writeTrack(trackDbTxt, true);
        }
    }

    /**
     * This method writes out the track information.
     * @param trackDbTxt The trackDb.txt file.
     * @param append Output will be appended to the end of the trackDb.txt file, or otherwise.
     * @throws IOException
     */
    private void writeTrack(File trackDbTxt, boolean append) throws IOException {
        Writer fileWriter = new FileWriter(trackDbTxt, append);
        fileWriter.write("track " + trackName + "\n");
        fileWriter.write("bigDataUrl " + bigDataURL + "\n");
        fileWriter.write("shortLabel " + trackShortLabel + "\n");
        fileWriter.write("longLabel " + trackLongLabel + "\n");
        StringBuilder metadata = new StringBuilder("metadata ");
        metadata.append("track_added_date=\"" + new SimpleDateFormat("EEE MMM YY HH:MM:ss YYYY z").format(new Date()) + "\" ");
        if (!StringUtils.isBlank(trackTissue)) {
            metadata.append("tissue_type=\"").append(trackTissue).append("\" ");
        }
        if (!StringUtils.isBlank(trackCellType)) {
            metadata.append("cell_type=\"").append(trackCellType).append("\" ");
        }
        if (!StringUtils.isBlank(trackDisese)) {
            metadata.append("disease=\"").append(trackDisese).append("\" ");
        }
        metadata.append("description=\"").append(trackShortLabel).append("\" ");
        metadata.append("scientific_name=\"").append(speciesSci).append("\" ");
        if (!StringUtils.isBlank(trackTaxaID)) {
            metadata.append("tax_id=").append(trackTaxaID).append(" ");
        }
        if (!StringUtils.isBlank(centre)) {
            metadata.append("center_name=\"").append(centre).append("\" ");
        }
        if (!StringUtils.isBlank(trackInstruments)) {
            metadata.append("instruments=\"").append(trackInstruments).append("\" ");
        }
        if (!StringUtils.isBlank(trackKeywords)) {
            metadata.append("keywords=\"").append(trackKeywords).append("\" ");
        }
        if (!StringUtils.isBlank(trackOtherOmics)) {
            metadata.append("other_omics=\"").append(trackOtherOmics).append("\" ");
        }
        if (!StringUtils.isBlank(trackPubReference)) {
            metadata.append("references=\"").append(trackPubReference).append("\" ");
        }
        if (!StringUtils.isBlank(trackPubDate)) {
            metadata.append("first_public=\"").append(trackPubDate).append("\"");
        }
        metadata.append('\n');
        fileWriter.write("type " + trackType + "\n");
        fileWriter.write("\n");
        fileWriter.close();
        logger.info(String.format("Finished writing track: track %s_%s in : %s", trackName, hubName, trackDbTxt.getPath()));
    }

    /**
     * This method validates the supplied track hub parameters.
     * @return Valid parameters, true. Otherwise, false.
     */
    private boolean validateParams() {
        boolean result = true;
        String badParam = "";
        if (StringUtils.isBlank(hubName)) {
            result = false;
            badParam = "hubName";
        } else if (StringUtils.isBlank(emailAddress) || !TrackhubGenerator.isValidEmailAddress(emailAddress)) {
            result = false;
            badParam = "emailAddress";
        }
        else if (StringUtils.isBlank(speciesShort)) {
            result = false;
            badParam = "speciesShort";
        } else if (StringUtils.isBlank(speciesSci)) {
            result = false;
            badParam = "speciesSci";
        } else if (StringUtils.isBlank(assembly)) {
            result = false;
            badParam = "assembly";
        } else if (trackType==null) {
            result = false;
            badParam = "trackType";
        } else if (StringUtils.isBlank(bigDataURL) || !(bigDataURL.startsWith("http://") || bigDataURL.startsWith("https://") || bigDataURL.startsWith("ftp://"))) {
            result = false;
            badParam = "bigDataURL";
        } else if (StringUtils.isBlank(centre)) {
            result = false;
            badParam = "centre";
        } else if (StringUtils.isBlank(hubShortLabel)) {
            result = false;
            badParam = "shortLabel";
        } else if (StringUtils.isBlank(hubLongLabel)) {
            result = false;
            badParam = "longLabel";
        } else if (trackhub==null) {
            result = false;
            badParam = "trackhub";
        }
        if (!result) {
            logger.error("Failed parameter violation. Check supplied parameter: " + badParam);
        }
        return result;
    }

    /**
     * Checks if the supplied email address is valid or not.
     * @param email The email addreess to test.
     * @return Valid email address, true. Otherwise, false.
     */
    private static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    public String getHubName() {
        return hubName;
    }

    public void setHubName(String hubName) {
        this.hubName = hubName;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getSpeciesShort() {
        return speciesShort;
    }

    public void setSpeciesShort(String speciesShort) {
        this.speciesShort = speciesShort;
    }

    public String getSpeciesSci() {
        return speciesSci;
    }

    public void setSpeciesSci(String speciesSci) {
        this.speciesSci = speciesSci;
    }

    public String getAssembly() {
        return assembly;
    }

    public void setAssembly(String assembly) {
        this.assembly = assembly;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getBigDataURL() {
        return bigDataURL;
    }

    public void setBigDataURL(String bigDataURL) {
        this.bigDataURL = bigDataURL;
    }

    public String getCentre() {
        return centre;
    }

    public void setCentre(String centre) {
        this.centre = centre;
    }

    public String getHubShortLabel() {
        return hubShortLabel;
    }

    public void setHubShortLabel(String hubShortLabel) {
        this.hubShortLabel = hubShortLabel;
    }

    public String getHubLongLabel() {
        return hubLongLabel;
    }

    public void setHubLongLabel(String hubLongLabel) {
        this.hubLongLabel = hubLongLabel;
    }

    public String getTrackShortLabel() {
        return trackShortLabel;
    }

    public void setTrackShortLabel(String trackShortLabel) {
        this.trackShortLabel = trackShortLabel;
    }

    public String getTrackLongLabel() {
        return trackLongLabel;
    }

    public void setTrackLongLabel(String trackLongLabel) {
        this.trackLongLabel = trackLongLabel;
    }

    public String getTrackTaxaID() {
        return trackTaxaID;
    }

    public void setTrackTaxaID(String trackTaxaID) {
        this.trackTaxaID = trackTaxaID;
    }

    public String getTrackTissue() {
        return trackTissue;
    }

    public void setTrackTissue(String trackTissue) {
        this.trackTissue = trackTissue;
    }

    public String getTrackCellType() {
        return trackCellType;
    }

    public void setTrackCellType(String trackCellType) {
        this.trackCellType = trackCellType;
    }

    public String getTrackDisese() {
        return trackDisese;
    }

    public void setTrackDisese(String trackDisese) {
        this.trackDisese = trackDisese;
    }

    public String getTrackPubDate() {
        return trackPubDate;
    }

    public void setTrackPubDate(String trackPubDate) {
        this.trackPubDate = trackPubDate;
    }

    public String getTrackInstruments() {
        return trackInstruments;
    }

    public void setTrackInstruments(String trackInstruments) {
        this.trackInstruments = trackInstruments;
    }

    public String getTrackKeywords() {
        return trackKeywords;
    }

    public void setTrackKeywords(String trackKeywords) {
        this.trackKeywords = trackKeywords;
    }

    public String getTrackOtherOmics() {
        return trackOtherOmics;
    }

    public void setTrackOtherOmics(String trackOtherOmics) {
        this.trackOtherOmics = trackOtherOmics;
    }

    public String getTrackPubReference() {
        return trackPubReference;
    }

    public void setTrackPubReference(String trackPubReference) {
        this.trackPubReference = trackPubReference;
    }

    public File getTrackhub() {
        return trackhub;
    }

    public void setTrackhub(File trackhub) {
        this.trackhub = trackhub;
    }

    public TrackType getTrackType() {
        return trackType;
    }

    public void setTrackType(TrackType trackType) {
        this.trackType = trackType;
    }

}
