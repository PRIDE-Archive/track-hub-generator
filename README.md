[![Build Status](https://travis-ci.org/PRIDE-Utilities/track-hub-generator.svg?branch=master)](https://travis-ci.org/PRIDE-Utilities/track-hub-generator)
# track-hub-generator
Generates and updates track hubs for use with the Ensembl Track Hub Registry.

## Getting track-hub-generator

Maven Dependency
The track-hub-generator library can be used in Maven projects, you can include the following snippets in your Maven pom file.
 
 ```maven
 <dependency>
   <groupId>uk.ac.ebi.pride.utilities</groupId>
   <artifactId>track-hub-generator</artifactId>
   <version>x.x.x</version>
 </dependency> 
 ```
 
  ```maven
 <!-- EBI release repo -->
  <repository>
    <id>nexus-ebi-release-repo</id>
    <url>http://www.ebi.ac.uk/Tools/maven/repos/content/groups/ebi-repo/</url>
  </repository>
 </snapshotRepository>
  ```
  
## Using track-hub-generator

### Reading a mzIdentML file:

This example shows how to read an mzIdentML file and retrieve the information from them:

```java
//Create a new track hub
TrackhubGenerator thg = new TrackhubGenerator(hubName, trackName, emailAddress, speciesShort, speciesSci, assembly, TrackType.bigBed, bigDataURL, centre, hubShortLabel, hubLongLabel, trackShortLabel, trackLongLabel, trackTaxaID, trackTissue, trackCellType, trackDisese, trackPubDate, trackInstruments, trackKeywords, trackOtherOmics, trackPubReference, trackhub;
thg.createTrackHub();

//Post the track hub to the registry
TrackhubRegister thr = thr = new TrackhubRegister(server, user, password, url, PostType.PROTEOMICS, SearchType.PUBLIC, assemblies) {
thr.login();
thr.postTrackhub()
thr.logout();
```

## Getting Help

If you have questions or need additional help, please contact the PRIDE Helpdesk at the EBI: pride-support@ebi.ac.uk.
