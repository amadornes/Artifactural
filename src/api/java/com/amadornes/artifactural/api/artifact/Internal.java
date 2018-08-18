package com.amadornes.artifactural.api.artifact;

import com.amadornes.artifactural.api.cache.ArtifactCache;
import com.amadornes.artifactural.api.transform.ArtifactTransformer;

import java.io.File;
import java.io.InputStream;

final class Internal {

    static final ArtifactIdentifier NO_IDENTIFIER = new ArtifactIdentifier() {

        @Override
        public String getGroup() {
            return "missing";
        }

        @Override
        public String getName() {
            return "missing";
        }

        @Override
        public String getVersion() {
            return "0.0.0";
        }

        @Override
        public String getClassifier() {
            return "";
        }

        @Override
        public String getExtension() {
            return "missing";
        }

    };

    static final Artifact NO_ARTIFACT = new Artifact.Cached() {

        @Override
        public ArtifactIdentifier getIdentifier() {
            return ArtifactIdentifier.none();
        }

        @Override
        public ArtifactMetadata getMetadata() {
            return new ArtifactMetadata() {
                @Override
                public ArtifactMetadata with(String key, String value) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public String getHash() {
                    return "ERROR";
                }
            };
        }

        @Override
        public ArtifactType getType() {
            return ArtifactType.OTHER;
        }

        @Override
        public Artifact withMetadata(ArtifactMetadata metadata) {
            return this;
        }

        @Override
        public Artifact apply(ArtifactTransformer transformer) {
            return this;
        }

        @Override
        public Artifact.Cached cache(ArtifactCache cache, String specifier) {
            return this;
        }

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public InputStream openStream() throws MissingArtifactException {
            throw new MissingArtifactException(getIdentifier());
        }

        @Override
        public File asFile() throws MissingArtifactException {
            throw new MissingArtifactException(getIdentifier());
        }

        @Override
        public File getFileLocation() throws MissingArtifactException {
            throw new MissingArtifactException(getIdentifier());
        }

    };

}
