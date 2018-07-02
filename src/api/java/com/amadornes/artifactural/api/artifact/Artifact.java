package com.amadornes.artifactural.api.artifact;

import com.amadornes.artifactural.api.cache.ArtifactCache;
import com.amadornes.artifactural.api.transform.ArtifactTransformer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.function.UnaryOperator;

public interface Artifact {

    static Artifact none() {
        return Internal.NO_ARTIFACT;
    }

    ArtifactIdentifier getIdentifier();

    ArtifactMetadata getMetadata();

    ArtifactType getType();

    Artifact withMetadata(ArtifactMetadata metadata);

    Artifact apply(ArtifactTransformer transformer);

    Artifact cache(ArtifactCache cache, String specifier);

    boolean isPresent();

    InputStream openStream() throws IOException, MissingArtifactException;

}
