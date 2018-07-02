package com.amadornes.artifactural.api.artifact;

public class MissingArtifactException extends RuntimeException {

    public MissingArtifactException(ArtifactIdentifier identifier) {
        super("Could not find artifact: " + identifier);
    }

}
