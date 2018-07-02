package com.amadornes.artifactural.gradle;

import com.amadornes.artifactural.api.artifact.Artifact;
import com.amadornes.artifactural.api.artifact.ArtifactIdentifier;
import com.amadornes.artifactural.api.artifact.ArtifactType;
import com.amadornes.artifactural.base.artifact.StreamableArtifact;

import java.io.File;
import java.util.Set;

public class GradleArtifact {

    public static Artifact maven(DependencyResolver resolver, ArtifactIdentifier identifier, ArtifactType type) {
        Set<File> files = resolver.resolveDependency(
                identifier.getGroup()
                        + ":" + identifier.getName()
                        + ":" + identifier.getVersion()
                        + (identifier.getClassifier().isEmpty() ? "" : ":" + identifier.getClassifier())
                        + (identifier.getExtension().isEmpty() ? "" : "@" + identifier.getExtension()),
                false
        );
        if (files.isEmpty()) return Artifact.none();
        return StreamableArtifact.ofJar(identifier, type, files.iterator().next());
    }

}
