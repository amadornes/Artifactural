package com.amadornes.artifactural.base.artifact;

import com.amadornes.artifactural.api.artifact.Artifact;
import com.amadornes.artifactural.api.artifact.ArtifactIdentifier;
import com.amadornes.artifactural.api.artifact.ArtifactMetadata;
import com.amadornes.artifactural.api.artifact.ArtifactType;
import com.amadornes.artifactural.api.artifact.MissingArtifactException;
import com.amadornes.artifactural.api.artifact.Streamable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class StreamableArtifact extends ArtifactBase {

    public static Artifact ofFile(ArtifactIdentifier identifier, ArtifactType type, File file) {
        return new StreamableFileArtifact(identifier, type, file);
    }

    public static Artifact ofURL(ArtifactIdentifier identifier, ArtifactType type, URL url) {
        return new StreamableArtifact(identifier, type, url::openStream);
    }

    public static Artifact ofBytes(ArtifactIdentifier identifier, ArtifactType type, byte[] bytes) {
        return new StreamableArtifact(identifier, type, () -> new ByteArrayInputStream(bytes));
    }

    public static Artifact ofStreamable(ArtifactIdentifier identifier, ArtifactType type, Streamable streamable) {
        return new StreamableArtifact(identifier, type, streamable);
    }

    private final Streamable streamable;

    private StreamableArtifact(ArtifactIdentifier identifier, ArtifactType type, Streamable streamable) {
        this(identifier, type, new SimpleArtifactMetadata(), streamable);
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

    private static class StreamableFileArtifact extends StreamableArtifact implements Artifact.Cached {

        private final File file;

        private StreamableFileArtifact(ArtifactIdentifier identifier, ArtifactType type, File file) {
            super(identifier, type, () -> new FileInputStream(file));
            this.file = file;
        }

        @Override
        public File asFile() throws MissingArtifactException {
            return file;
        }

        @Override
        public File getFileLocation() throws MissingArtifactException {
            return file;
        }

    }

}
