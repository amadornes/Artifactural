package com.amadornes.artifactural.api.transform;

import com.amadornes.artifactural.api.artifact.Artifact;
import com.amadornes.artifactural.api.artifact.ArtifactMetadata;

import java.util.Set;
import java.util.function.UnaryOperator;

public interface ArtifactTransformer {

    static ArtifactTransformer of(UnaryOperator<Artifact> operator) {
        return new ArtifactTransformer() {
            @Override
            public Artifact transform(Artifact artifact) {
                return operator.apply(artifact);
            }

            @Override
            public ArtifactMetadata withInfo(ArtifactMetadata metadata) {
                return metadata;
            }
        };
    }

    static ArtifactTransformer exclude(Set<String> filters) {
        return (ArtifactTransformer) new Object();
    }

    default boolean appliesTo(Artifact artifact) {
        return true;
    }

    Artifact transform(Artifact artifact);

    ArtifactMetadata withInfo(ArtifactMetadata metadata);

    default ArtifactTransformer andThen(ArtifactTransformer other) {
        ArtifactTransformer current = this;
        return new ArtifactTransformer() {

            @Override
            public Artifact transform(Artifact artifact) {
                return other.transform(current.transform(artifact));
            }

            @Override
            public ArtifactMetadata withInfo(ArtifactMetadata metadata) {
                return other.withInfo(current.withInfo(metadata));
            }

        };
    }

}
