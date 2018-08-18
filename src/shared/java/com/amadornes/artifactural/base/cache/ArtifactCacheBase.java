package com.amadornes.artifactural.base.cache;

import com.amadornes.artifactural.api.artifact.Artifact;
import com.amadornes.artifactural.api.artifact.ArtifactIdentifier;
import com.amadornes.artifactural.api.artifact.ArtifactMetadata;
import com.amadornes.artifactural.api.artifact.ArtifactType;
import com.amadornes.artifactural.api.artifact.MissingArtifactException;
import com.amadornes.artifactural.api.cache.ArtifactCache;
import com.amadornes.artifactural.api.transform.ArtifactTransformer;
import com.amadornes.artifactural.base.artifact.StreamableArtifact;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class ArtifactCacheBase implements ArtifactCache {

    Artifact.Cached doStore(File path, Artifact artifact) {
        return wrap(
                StreamableArtifact.ofStreamable(
                        artifact.getIdentifier(),
                        artifact.getType(),
                        () -> stream(path, artifact)
                ).withMetadata(artifact.getMetadata()),
                path
        );
    }

    private InputStream stream(File path, Artifact artifact) throws IOException {
        if (!path.exists()) {
            path.getParentFile().mkdirs();
            path.createNewFile();
            FileOutputStream fos = new FileOutputStream(path);
            InputStream is = artifact.openStream();
            int read;
            byte[] bytes = new byte[256];
            while ((read = is.read(bytes)) > 0) {
                fos.write(bytes, 0, read);
            }
            fos.close();
            is.close();
        }
        return new FileInputStream(path);
    }

    public static Artifact.Cached wrap(Artifact artifact, File file) {
        return new Artifact.Cached() {

            @Override
            public ArtifactIdentifier getIdentifier() {
                return artifact.getIdentifier();
            }

            @Override
            public ArtifactMetadata getMetadata() {
                return artifact.getMetadata();
            }

            @Override
            public ArtifactType getType() {
                return artifact.getType();
            }

            @Override
            public Artifact withMetadata(ArtifactMetadata metadata) {
                return artifact.withMetadata(metadata);
            }

            @Override
            public Artifact apply(ArtifactTransformer transformer) {
                return artifact.apply(transformer);
            }

            @Override
            public Artifact.Cached cache(ArtifactCache cache, String specifier) {
                return artifact.cache(cache, specifier);
            }

            @Override
            public boolean isPresent() {
                return artifact.isPresent();
            }

            @Override
            public InputStream openStream() throws IOException, MissingArtifactException {
                return artifact.openStream();
            }

            @Override
            public File asFile() throws IOException, MissingArtifactException {
                if(!file.exists()) {
                    artifact.openStream().close();
                }
                return file;
            }

            @Override
            public File getFileLocation() throws MissingArtifactException {
                return file;
            }

        };
    }

}
