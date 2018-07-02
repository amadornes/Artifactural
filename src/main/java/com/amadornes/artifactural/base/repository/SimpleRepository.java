package com.amadornes.artifactural.base.repository;

import com.amadornes.artifactural.api.artifact.Artifact;
import com.amadornes.artifactural.api.artifact.ArtifactIdentifier;
import com.amadornes.artifactural.api.repository.ArtifactProvider;
import com.amadornes.artifactural.api.repository.Repository;

public class SimpleRepository implements Repository {

    public static Repository of(ArtifactProvider<ArtifactIdentifier> provider) {
        return new SimpleRepository(provider);
    }

    private final ArtifactProvider<ArtifactIdentifier> provider;

    private SimpleRepository(ArtifactProvider<ArtifactIdentifier> provider) {
        this.provider = provider;
    }

    @Override
    public Artifact getArtifact(ArtifactIdentifier identifier) {
        return provider.getArtifact(identifier);
    }

}
