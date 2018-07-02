package com.amadornes.artifactural.base.artifact;

import com.amadornes.artifactural.api.artifact.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class StreamableArtifact extends ArtifactBase {

    public static Artifact ofJar(ArtifactIdentifier identifier, ArtifactType type, File file) {
        return ofStreamable(identifier, type, () -> new FileInputStream(file));
    }

    public static Artifact ofURL(ArtifactIdentifier identifier, ArtifactType type, URL url) {
        return ofStreamable(identifier, type, url::openStream);
    }

    public static Artifact ofStreamable(ArtifactIdentifier identifier, ArtifactType type, Streamable streamable) {
        return new StreamableArtifact(identifier, type, streamable);
    }

    private final Streamable streamable;

    private StreamableArtifact(ArtifactIdentifier identifier, ArtifactType type, Streamable streamable) {
        this(identifier, type, ArtifactMetadata.empty(), streamable);
    }

    private StreamableArtifact(ArtifactIdentifier identifier, ArtifactType type, ArtifactMetadata metadata, Streamable streamable) {
        super(identifier, type, metadata);
        this.streamable = streamable;
    }

    @Override
    public Artifact withMetadata(ArtifactMetadata metadata) {
        return new StreamableArtifact(getIdentifier(), getType(), metadata, streamable);
    }

    @Override
    public boolean isPresent() {
        try (InputStream is = openStream()) {
            is.close();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public InputStream openStream() throws IOException {
        return streamable.openStream();
    }

}
