package com.amadornes.artifactural.api.artifact;

public interface ArtifactMetadata {

    ArtifactMetadata with(String key, String value);

    String getHash();

}
