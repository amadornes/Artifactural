package com.amadornes.artifactural.base.transform;

import com.amadornes.artifactural.api.artifact.Artifact;
import com.amadornes.artifactural.api.artifact.ArtifactMetadata;
import com.amadornes.artifactural.api.cache.ArtifactCache;
import com.amadornes.artifactural.api.transform.ArtifactPipeline;
import com.amadornes.artifactural.api.transform.ArtifactTransformer;

import java.util.function.UnaryOperator;

public class SimpleArtifactPipeline implements ArtifactPipeline {

    public static ArtifactPipeline create() {
        return new SimpleArtifactPipeline();
    }

    private static final ArtifactTransformer IDENTITY = ArtifactTransformer.of(UnaryOperator.identity());

    private ArtifactTransformer transformer = IDENTITY;

    private SimpleArtifactPipeline() {
    }

    @Override
    public ArtifactPipeline apply(ArtifactTransformer transformer) {
        this.transformer = this.transformer.andThen(transformer);
        return this;
    }

    @Override
    public ArtifactPipeline cache(ArtifactCache cache, String specifier) {
        transformer = transformer.andThen(ArtifactTransformer.of(artifact -> cache.store(artifact, specifier)));
        return this;
    }

    @Override
    public Artifact transform(Artifact artifact) {
        return transformer.transform(artifact);
    }

    @Override
    public ArtifactMetadata withInfo(ArtifactMetadata metadata) {
        return transformer.withInfo(metadata);
    }

}
