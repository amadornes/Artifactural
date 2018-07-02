package com.amadornes.artifactural.gradle;

import com.amadornes.artifactural.api.artifact.Artifact;
import com.amadornes.artifactural.api.artifact.ArtifactIdentifier;
import com.amadornes.artifactural.api.repository.Repository;
import com.amadornes.artifactural.base.artifact.ArtifactIdentifierImpl;
import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectCollection;
import org.gradle.api.Transformer;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.ConfiguredModuleComponentRepository;
import org.gradle.api.internal.artifacts.repositories.AbstractArtifactRepository;
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository;
import org.gradle.api.internal.artifacts.repositories.ResolutionAwareRepository;
import org.gradle.api.internal.artifacts.repositories.resolver.ExternalResourceArtifactResolver;
import org.gradle.api.internal.artifacts.repositories.resolver.ExternalResourceResolver;
import org.gradle.api.internal.artifacts.repositories.resolver.MavenResolver;
import org.gradle.api.resources.ResourceException;
import org.gradle.internal.impldep.com.google.common.io.CountingInputStream;
import org.gradle.internal.impldep.org.apache.commons.io.IOUtils;
import org.gradle.internal.resource.*;
import org.gradle.internal.resource.metadata.DefaultExternalResourceMetaData;
import org.gradle.internal.resource.metadata.ExternalResourceMetaData;

import javax.annotation.Nullable;
import java.io.*;
import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GradleRepositoryAdapter extends AbstractArtifactRepository implements ResolutionAwareRepository {

    private static final Pattern URL_PATTERN = Pattern.compile(
            "^/(?<group>\\S+(?:/\\S+)*)/(?<name>\\S+)/(?<version>\\S+)/" +
                    "\\2-\\3(?:-(?<classifier>[^.\\s]+))?\\.(?<extension>\\S+)$");

    public static GradleRepositoryAdapter add(RepositoryHandler handler, String name, Object url, Repository repository) {
        // Create the real maven test we'll be using and remove it
        MavenArtifactRepository maven = handler.maven($ -> {
            $.setName(name);
            $.setUrl(url);
        });
        handler.remove(maven);

        // Add our own custom test instead, using the real one in the background
        GradleRepositoryAdapter repo = new GradleRepositoryAdapter((DefaultMavenArtifactRepository) maven, repository);
        handler.add(repo);
        return repo;
    }

    private final DefaultMavenArtifactRepository maven;
    private final Repository repository;

    private GradleRepositoryAdapter(DefaultMavenArtifactRepository maven, Repository repository) {
        this.maven = maven;
        this.repository = repository;
    }

    @Override
    public String getName() {
        return maven.getName(); // Proxy to the real repo
    }

    @Override
    public void setName(String name) {
        maven.setName(name); // Proxy to the real repo
    }

    @Override
    public String getDisplayName() {
        return maven.getDisplayName(); // Proxy to the real repo
    }

    @Override
    public void onAddToContainer(NamedDomainObjectCollection<ArtifactRepository> container) {
        // No-op. The real repo will get this already
    }

    @Override
    public ConfiguredModuleComponentRepository createResolver() {
        MavenResolver resolver = (MavenResolver) maven.createResolver();
        ExternalResourceRepository repo = new StreamingRepo();

        ExternalResourceArtifactResolver artifactResolver = ReflectionUtils.invoke(resolver, ExternalResourceResolver.class, "createArtifactResolver");
        ReflectionUtils.alter(resolver, "repository", prev -> repo);
        ReflectionUtils.alter(resolver, "mavenMetaDataLoader.cacheAwareExternalResourceAccessor.delegate", prev -> repo);
        ReflectionUtils.alter(artifactResolver, "repository", prev -> repo);

        return resolver;
    }

    private class StreamingRepo implements ExternalResourceRepository {

        @Override
        public ExternalResourceRepository withProgressLogging() {
            return this;
        }

        @Override
        public ExternalResource resource(ExternalResourceName name, boolean revalidate) {
            URI uri = name.getUri();
            Matcher matcher = URL_PATTERN.matcher(uri.getPath());
            if (!matcher.matches()) return new NullExternalResource(uri);
            ArtifactIdentifier identifier = new ArtifactIdentifierImpl(
                    matcher.group("group").replace('/', '.'),
                    matcher.group("name"),
                    matcher.group("version"),
                    matcher.group("classifier"),
                    matcher.group("extension"));
            Artifact artifact = repository.getArtifact(identifier);
            if (!artifact.isPresent()) return new NullExternalResource(uri);
            return new CustomArtifactExternalResource(uri, artifact);
        }

        @Override
        public ExternalResource resource(ExternalResourceName name) {
            return resource(name, false);
        }

    }

    private class CustomArtifactExternalResource extends AbstractExternalResource {

        private final URI uri;
        private final Artifact artifact;

        private CustomArtifactExternalResource(URI uri, Artifact artifact) {
            this.uri = uri;
            this.artifact = artifact;
        }

        @Override
        public String getDisplayName() {
            return uri.toString();
        }

        @Override
        public URI getURI() {
            return uri;
        }

        @Nullable
        @Override
        public ExternalResourceReadResult<Void> writeToIfPresent(File file) {
            try {
                if (!artifact.isPresent()) return null;
                FileOutputStream out = new FileOutputStream(file);
                ExternalResourceReadResult<Void> result = writeTo(out);
                out.close();
                return result;
            } catch (IOException ex) {
                return null;
            }
        }

        @Override
        public ExternalResourceReadResult<Void> writeTo(OutputStream out) throws ResourceException {
            return withContent(in -> {
                try {
                    IOUtils.copy(in, out);
                } catch (IOException ex) {
                    throw ResourceExceptions.failure(uri, "Failed to write resource!", ex);
                }
            });
        }

        @Override
        public ExternalResourceReadResult<Void> withContent(Action<? super InputStream> action) throws ResourceException {
            try {
                if (!artifact.isPresent()) throw ResourceExceptions.getMissing(uri);
                CountingInputStream in = new CountingInputStream(artifact.openStream());
                action.execute(in);
                in.close();
                return ExternalResourceReadResult.of(in.getCount());
            } catch (IOException ex) {
                throw ResourceExceptions.failure(uri, "Failed to write resource!", ex);
            }
        }

        @Nullable
        @Override
        public <T> ExternalResourceReadResult<T> withContentIfPresent(Transformer<? extends T, ? super InputStream> transformer) {
            try {
                if (!artifact.isPresent()) return null;
                CountingInputStream in = new CountingInputStream(artifact.openStream());
                T result = transformer.transform(in);
                in.close();
                return ExternalResourceReadResult.of(in.getCount(), result);
            } catch (IOException ex) {
                return null;
            }
        }

        @Nullable
        @Override
        public <T> ExternalResourceReadResult<T> withContentIfPresent(ContentAction<? extends T> contentAction) {
            try {
                if (!artifact.isPresent()) return null;
                CountingInputStream in = new CountingInputStream(artifact.openStream());
                T result = contentAction.execute(in, getMetaData());
                in.close();
                return ExternalResourceReadResult.of(in.getCount(), result);
            } catch (IOException ex) {
                return null;
            }
        }

        @Override
        public ExternalResourceWriteResult put(ReadableContent readableContent) throws ResourceException {
            throw ResourceExceptions.putFailed(uri, null);
        }

        @Nullable
        @Override
        public List<String> list() throws ResourceException {
            return null;
        }

        @Nullable
        @Override
        public ExternalResourceMetaData getMetaData() {
            try {
                if (!artifact.isPresent()) return null;
                InputStream stream = artifact.openStream();
                int length = stream.available();
                stream.close();
                return new DefaultExternalResourceMetaData(uri, 0, length);
            } catch (IOException ex) {
                return null;
            }
        }

    }

    private class NullExternalResource extends AbstractExternalResource {

        private final URI uri;

        private NullExternalResource(URI uri) {
            this.uri = uri;
        }

        @Override
        public String getDisplayName() {
            return uri.toString();
        }

        @Override
        public URI getURI() {
            return uri;
        }

        @Nullable
        @Override
        public ExternalResourceReadResult<Void> writeToIfPresent(File destination) throws ResourceException {
            return null;
        }

        @Override
        public ExternalResourceReadResult<Void> writeTo(OutputStream destination) throws ResourceException {
            throw ResourceExceptions.getMissing(uri);
        }

        @Override
        public ExternalResourceReadResult<Void> withContent(Action<? super InputStream> readAction) throws ResourceException {
            throw ResourceExceptions.getMissing(uri);
        }

        @Nullable
        @Override
        public <T> ExternalResourceReadResult<T> withContentIfPresent(Transformer<? extends T, ? super InputStream> readAction) {
            return null;
        }

        @Nullable
        @Override
        public <T> ExternalResourceReadResult<T> withContentIfPresent(ContentAction<? extends T> readAction) {
            return null;
        }

        @Override
        public ExternalResourceWriteResult put(ReadableContent source) throws ResourceException {
            throw ResourceExceptions.getMissing(uri);
        }

        @Nullable
        @Override
        public List<String> list() {
            return null;
        }

        @Nullable
        @Override
        public ExternalResourceMetaData getMetaData() {
            return null;
        }

    }

}
