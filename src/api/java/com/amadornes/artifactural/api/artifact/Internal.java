package com.amadornes.artifactural.api.artifact;

import com.amadornes.artifactural.api.cache.ArtifactCache;
import com.amadornes.artifactural.api.transform.ArtifactTransformer;

import java.io.IOException;
import java.io.InputStream;

class Internal {

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

    static final Artifact NO_ARTIFACT = new Artifact() {

        @Override
        public ArtifactIdentifier getIdentifier() {
            return ArtifactIdentifier.none();
        }

        @Override
        public ArtifactMetadata getMetadata() {
            return ArtifactMetadata.empty();
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
        public Artifact cache(ArtifactCache cache, String specifier) {
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

    };

}
