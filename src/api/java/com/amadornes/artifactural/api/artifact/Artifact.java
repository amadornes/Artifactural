package com.amadornes.artifactural.api.artifact;

import com.amadornes.artifactural.api.cache.ArtifactCache;
import com.amadornes.artifactural.api.transform.ArtifactTransformer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface Artifact {

    static Artifact none() {
        return Internal.NO_ARTIFACT;
    }

    ArtifactIdentifier getIdentifier();

    ArtifactMetadata getMetadata();

    ArtifactType getType();

    Artifact withMetadata(ArtifactMetadata metadata);

    Artifact apply(ArtifactTransformer transformer);

    Artifact.Cached cache(ArtifactCache cache, String specifier);

    default Artifact.Cached optionallyCache(ArtifactCache cache, String specifier) {
        return this instanceof Artifact.Cached ? (Artifact.Cached) this : cache(cache, specifier);
    }

    boolean isPresent();

    InputStream openStream() throws IOException, MissingArtifactException;

    interface Cached extends Artifact {

        File asFile() throws IOException, MissingArtifactException;

        File getFileLocation() throws IOException, MissingArtifactException;

    }

}
