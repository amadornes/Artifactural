package com.amadornes.artifactural.api.repository;

import com.amadornes.artifactural.api.artifact.Artifact;

import java.util.function.Function;
import java.util.function.Predicate;

public interface ArtifactProvider<I> {

    Artifact getArtifact(I info);

    interface Builder<S, I> {

        Builder<S, I> filter(Predicate<I> filter);

        <D> Builder<S, D> mapInfo(Function<I, D> mapper);

        Complete<S, I> provide(ArtifactProvider<I> provider);

        interface Complete<S, I> extends ArtifactProvider<S> {

            Complete<S, I> provide(ArtifactProvider<I> provider);

        }

    }

}
