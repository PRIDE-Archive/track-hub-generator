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
     * @throws IOException if there are problems with writing the track file
     */
    private void writeTrack(File trackDbTxt, boolean append) throws IOException {
        Writer fileWriter = new FileWriter(trackDbTxt, append);
        fileWriter.write("track " + trackName + "\n");
        fileWriter.write("bigDataUrl " + bigDataURL + "\n");
        fileWriter.write("shortLabel " + trackShortLabel + "\n");
        fileWriter.write("longLabel " + trackLongLabel + "\n");
        StringBuilder metadata = new StringBuilder("metadata ");
        metadata.append("track_added_date=\"").append(new SimpleDateFormat("EEE MMM YY HH:MM:ss YYYY z").format(new Date())).append("\" ");
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
        // TODO metadata not used at all because it's not used/indexed by the registry. Remove?
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

  /**
   * Gets trackInstruments.
   *
   * @return Value of trackInstruments.
   */
  public String getTrackInstruments() {
    return trackInstruments;
  }

  /**
   * Sets new trackInstruments.
   *
   * @param trackInstruments New value of trackInstruments.
   */
  public void setTrackInstruments(String trackInstruments) {
    this.trackInstruments = trackInstruments;
  }

  /**
   * Sets new trackCellType.
   *
   * @param trackCellType New value of trackCellType.
   */
  public void setTrackCellType(String trackCellType) {
    this.trackCellType = trackCellType;
  }

  /**
   * Sets new hubName.
   *
   * @param hubName New value of hubName.
   */
  public void setHubName(String hubName) {
    this.hubName = hubName;
  }

  /**
   * Sets new bigDataURL.
   *
   * @param bigDataURL New value of bigDataURL.
   */
  public void setBigDataURL(String bigDataURL) {
    this.bigDataURL = bigDataURL;
  }

  /**
   * Gets trackLongLabel.
   *
   * @return Value of trackLongLabel.
   */
  public String getTrackLongLabel() {
    return trackLongLabel;
  }

  /**
   * Sets new centre.
   *
   * @param centre New value of centre.
   */
  public void setCentre(String centre) {
    this.centre = centre;
  }

  /**
   * Gets trackhub.
   *
   * @return Value of trackhub.
   */
  public File getTrackhub() {
    return trackhub;
  }

  /**
   * Sets new speciesShort.
   *
   * @param speciesShort New value of speciesShort.
   */
  public void setSpeciesShort(String speciesShort) {
    this.speciesShort = speciesShort;
  }

  /**
   * Sets new fileType.
   *
   * @param fileType New value of fileType.
   */
  public void setFileType(String fileType) {
    this.fileType = fileType;
  }

  /**
   * Sets new trackhub.
   *
   * @param trackhub New value of trackhub.
   */
  public void setTrackhub(File trackhub) {
    this.trackhub = trackhub;
  }

  /**
   * Sets new hubShortLabel.
   *
   * @param hubShortLabel New value of hubShortLabel.
   */
  public void setHubShortLabel(String hubShortLabel) {
    this.hubShortLabel = hubShortLabel;
  }

  /**
   * Gets speciesShort.
   *
   * @return Value of speciesShort.
   */
  public String getSpeciesShort() {
    return speciesShort;
  }

  /**
   * Sets new trackType.
   *
   * @param trackType New value of trackType.
   */
  public void setTrackType(TrackType trackType) {
    this.trackType = trackType;
  }

  /**
   * Gets trackShortLabel.
   *
   * @return Value of trackShortLabel.
   */
  public String getTrackShortLabel() {
    return trackShortLabel;
  }

  /**
   * Gets fileType.
   *
   * @return Value of fileType.
   */
  public String getFileType() {
    return fileType;
  }

  /**
   * Gets assembly.
   *
   * @return Value of assembly.
   */
  public String getAssembly() {
    return assembly;
  }

  /**
   * Sets new speciesSci.
   *
   * @param speciesSci New value of speciesSci.
   */
  public void setSpeciesSci(String speciesSci) {
    this.speciesSci = speciesSci;
  }

  /**
   * Gets trackPubDate.
   *
   * @return Value of trackPubDate.
   */
  public String getTrackPubDate() {
    return trackPubDate;
  }

  /**
   * Gets trackDisese.
   *
   * @return Value of trackDisese.
   */
  public String getTrackDisese() {
    return trackDisese;
  }

  /**
   * Sets new trackTaxaID.
   *
   * @param trackTaxaID New value of trackTaxaID.
   */
  public void setTrackTaxaID(String trackTaxaID) {
    this.trackTaxaID = trackTaxaID;
  }

  /**
   * Gets centre.
   *
   * @return Value of centre.
   */
  public String getCentre() {
    return centre;
  }

  /**
   * Sets new emailAddress.
   *
   * @param emailAddress New value of emailAddress.
   */
  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  /**
   * Sets new trackPubReference.
   *
   * @param trackPubReference New value of trackPubReference.
   */
  public void setTrackPubReference(String trackPubReference) {
    this.trackPubReference = trackPubReference;
  }

  /**
   * Gets trackName.
   *
   * @return Value of trackName.
   */
  public String getTrackName() {
    return trackName;
  }

  /**
   * Gets trackOtherOmics.
   *
   * @return Value of trackOtherOmics.
   */
  public String getTrackOtherOmics() {
    return trackOtherOmics;
  }

  /**
   * Gets hubName.
   *
   * @return Value of hubName.
   */
  public String getHubName() {
    return hubName;
  }

  /**
   * Sets new trackKeywords.
   *
   * @param trackKeywords New value of trackKeywords.
   */
  public void setTrackKeywords(String trackKeywords) {
    this.trackKeywords = trackKeywords;
  }

  /**
   * Sets new trackPubDate.
   *
   * @param trackPubDate New value of trackPubDate.
   */
  public void setTrackPubDate(String trackPubDate) {
    this.trackPubDate = trackPubDate;
  }

  /**
   * Gets trackTissue.
   *
   * @return Value of trackTissue.
   */
  public String getTrackTissue() {
    return trackTissue;
  }

  /**
   * Gets speciesSci.
   *
   * @return Value of speciesSci.
   */
  public String getSpeciesSci() {
    return speciesSci;
  }

  /**
   * Sets new trackDisese.
   *
   * @param trackDisese New value of trackDisese.
   */
  public void setTrackDisese(String trackDisese) {
    this.trackDisese = trackDisese;
  }

  /**
   * Sets new trackOtherOmics.
   *
   * @param trackOtherOmics New value of trackOtherOmics.
   */
  public void setTrackOtherOmics(String trackOtherOmics) {
    this.trackOtherOmics = trackOtherOmics;
  }

  /**
   * Sets new hubLongLabel.
   *
   * @param hubLongLabel New value of hubLongLabel.
   */
  public void setHubLongLabel(String hubLongLabel) {
    this.hubLongLabel = hubLongLabel;
  }

  /**
   * Gets trackKeywords.
   *
   * @return Value of trackKeywords.
   */
  public String getTrackKeywords() {
    return trackKeywords;
  }

  /**
   * Sets new trackTissue.
   *
   * @param trackTissue New value of trackTissue.
   */
  public void setTrackTissue(String trackTissue) {
    this.trackTissue = trackTissue;
  }

  /**
   * Gets hubShortLabel.
   *
   * @return Value of hubShortLabel.
   */
  public String getHubShortLabel() {
    return hubShortLabel;
  }

  /**
   * Gets trackPubReference.
   *
   * @return Value of trackPubReference.
   */
  public String getTrackPubReference() {
    return trackPubReference;
  }

  /**
   * Sets new trackShortLabel.
   *
   * @param trackShortLabel New value of trackShortLabel.
   */
  public void setTrackShortLabel(String trackShortLabel) {
    this.trackShortLabel = trackShortLabel;
  }

  /**
   * Sets new trackLongLabel.
   *
   * @param trackLongLabel New value of trackLongLabel.
   */
  public void setTrackLongLabel(String trackLongLabel) {
    this.trackLongLabel = trackLongLabel;
  }

  /**
   * Gets emailAddress.
   *
   * @return Value of emailAddress.
   */
  public String getEmailAddress() {
    return emailAddress;
  }

  /**
   * Gets trackCellType.
   *
   * @return Value of trackCellType.
   */
  public String getTrackCellType() {
    return trackCellType;
  }

  /**
   * Gets trackTaxaID.
   *
   * @return Value of trackTaxaID.
   */
  public String getTrackTaxaID() {
    return trackTaxaID;
  }

  /**
   * Gets trackType.
   *
   * @return Value of trackType.
   */
  public TrackType getTrackType() {
    return trackType;
  }

  /**
   * Gets hubLongLabel.
   *
   * @return Value of hubLongLabel.
   */
  public String getHubLongLabel() {
    return hubLongLabel;
  }

  /**
   * Sets new trackName.
   *
   * @param trackName New value of trackName.
   */
  public void setTrackName(String trackName) {
    this.trackName = trackName;
  }

  /**
   * Gets bigDataURL.
   *
   * @return Value of bigDataURL.
   */
  public String getBigDataURL() {
    return bigDataURL;
  }

  /**
   * Sets new assembly.
   *
   * @param assembly New value of assembly.
   */
  public void setAssembly(String assembly) {
    this.assembly = assembly;
  }
}
