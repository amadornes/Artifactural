package com.amadornes.artifactural.api.transform;

import com.amadornes.artifactural.api.cache.ArtifactCache;

public interface ArtifactPipeline extends ArtifactTransformer {

    ArtifactPipeline apply(ArtifactTransformer transformer);

    ArtifactPipeline cache(ArtifactCache cache, String specifier);

}
