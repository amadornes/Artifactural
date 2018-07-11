package com.amadornes.artifactural.base.repository;

import com.amadornes.artifactural.api.artifact.Artifact;
import com.amadornes.artifactural.api.repository.ArtifactProvider;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class ArtifactProviderBuilder<S, I> implements ArtifactProvider.Builder<S, I> {

    public static <I> ArtifactProviderBuilder<I, I> begin(Class<I> type) {
        return new ArtifactProviderBuilder<>(Function.identity());
    }

    private final Function<S, I> mapper;
    private final Set<Predicate<I>> filters = new HashSet<>();

    private ArtifactProviderBuilder(Function<S, I> mapper) {
        this.mapper = mapper;
    }

    @Override
    public ArtifactProvider.Builder<S, I> filter(Predicate<I> filter) {
        filters.add(filter);
        return this;
    }

    @Override
    public <D> ArtifactProvider.Builder<S, D> mapInfo(Function<I, D> mapper) {
        if (filters.isEmpty()) {
            return new ArtifactProviderBuilder<>(this.mapper.andThen(mapper));
        }
        return new ArtifactProviderBuilder<>((S info) -> {
            I localInfo = this.mapper.apply(info);
            if (localInfo == null) return null;
            for (Predicate<I> filter : filters) {
                if (!filter.test(localInfo)) {
                    return null;
                }
            }
            return mapper.apply(localInfo);
        });
    }

    @Override
    public ArtifactProvider.Builder.Complete<S, I> provide(ArtifactProvider<I> provider) {
        return new Complete<>(mapper).provide(provider);
    }

    private static class Complete<S, I> implements ArtifactProvider.Builder.Complete<S, I> {

        private final Set<ArtifactProvider<I>> providers = new HashSet<>();
        private final Function<S, I> mapper;

        private Complete(Function<S, I> mapper) {
            this.mapper = mapper;
        }

        @Override
        public Builder.Complete<S, I> provide(ArtifactProvider<I> provider) {
            providers.add(provider);
            return this;
        }

        @Override
        public Artifact getArtifact(S info) {
            I localInfo = mapper.apply(info);
            if (localInfo == null) return Artifact.none();

            for (ArtifactProvider<I> provider : providers) {
                Artifact artifact = provider.getArtifact(localInfo);
                if (artifact.isPresent()) return artifact;
            }
            return Artifact.none();
        }

    }

}
