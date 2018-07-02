package com.amadornes.artifactural.api.repository;

import com.amadornes.artifactural.api.artifact.Artifact;
import com.amadornes.artifactural.api.artifact.ArtifactIdentifier;

public interface Repository {

    Artifact getArtifact(ArtifactIdentifier identifier);

}
