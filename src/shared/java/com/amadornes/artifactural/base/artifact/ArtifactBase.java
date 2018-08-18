package com.amadornes.artifactural.base.artifact;

import com.amadornes.artifactural.api.artifact.Artifact;
import com.amadornes.artifactural.api.artifact.ArtifactMetadata;
import com.amadornes.artifactural.api.artifact.ArtifactType;
import com.amadornes.artifactural.api.cache.ArtifactCache;
import com.amadornes.artifactural.api.artifact.ArtifactIdentifier;
import com.amadornes.artifactural.api.transform.ArtifactTransformer;

public abstract class ArtifactBase implements Artifact {

    private final ArtifactIdentifier identifier;
    private final ArtifactType type;
    private final ArtifactMetadata metadata;

    public ArtifactBase(ArtifactIdentifier identifier, ArtifactType type, ArtifactMetadata metadata) {
        this.identifier = identifier;
        this.type = type;
        this.metadata = metadata;
    }

    @Override
    public ArtifactIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public ArtifactType getType() {
        return type;
    }

    @Override
    public ArtifactMetadata getMetadata() {
        return metadata;
    }

    @Override
    public Artifact apply(ArtifactTransformer transformer) {
        if (!transformer.appliesTo(this)) return this;
        return transformer.transform(this);
    }

    @Override
    public Artifact.Cached cache(ArtifactCache cache, String specifier) {
        return cache.store(this, specifier);
    }

}
