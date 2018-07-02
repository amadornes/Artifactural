package com.amadornes.artifactural.api.cache;

import com.amadornes.artifactural.api.artifact.Artifact;

public interface ArtifactCache {

    Artifact store(Artifact artifact, String specifier);

}
