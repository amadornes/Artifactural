package com.amadornes.artifactural.api.artifact;

public interface ArtifactMetadata {

    static ArtifactMetadata empty() {
        return (ArtifactMetadata) null;
    }

    ArtifactMetadata with(String key, String value);

    String getHash();

}
