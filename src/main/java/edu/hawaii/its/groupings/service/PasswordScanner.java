package edu.hawaii.its.groupings.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import edu.hawaii.its.groupings.exceptions.PasswordFoundException;

@Service
public class PasswordScanner {

    private static final Log logger = LogFactory.getLog(PasswordScanner.class);
    private List<String> locations = new ArrayList<>(Arrays.asList("src/main/resources"));
    private final String pattern = "^.*password.*\\=(?!\\s*$).+";

    @PostConstruct
    public void init() throws PasswordFoundException {
        logger.info("init; starting...");
        try {
            checkForPasswords();
            logger.info("init; check for passwords finished.");
            logger.info("init; started.");
        } catch (PasswordFoundException pfe) {
            throw pfe;
        } catch (Exception e) {
            String message = "init; skipping over scanning "
                    + "since it failed unexpectedly. Error: " + e;
            logger.warn(message);
        }
    }

    private void checkForPasswords() throws PasswordFoundException {
        CheckForPattern checkForPattern = new CheckForPattern();

        String patternResult = "";
        for (String location : locations) {
            List<String> fileLocations = checkForPattern.fileLocations(".properties", location, pattern);
            if (fileLocations != null && !fileLocations.isEmpty()) {
                for (String list : fileLocations) {
                    patternResult += "\n" + list;
                }
            }
        }

        if (patternResult.length() > 0) {
            throw new PasswordFoundException(patternResult);
        }
    }

    public List<String> getLocations() {
        return Collections.unmodifiableList(locations);
    }

    public void setLocations(List<String> locations) {
        this.locations = locations != null ? locations : Collections.emptyList();
    }

    public void addLocation(String location) {
        if (location != null) {
            this.locations.add(location);
        }
    }
}
