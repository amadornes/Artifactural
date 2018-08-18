package com.amadornes.artifactural.base.artifact;

import com.amadornes.artifactural.api.artifact.ArtifactIdentifier;

public class SimpleArtifactIdentifier implements ArtifactIdentifier {

    private final String group, name, version, classifier, extension;

    public SimpleArtifactIdentifier(String group, String name, String version, String classifier, String extension) {
        this.group = group;
        this.name = name;
        this.version = version;
        this.classifier = classifier;
        this.extension = extension;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getClassifier() {
        return classifier;
    }

    @Override
    public String getExtension() {
        return extension;
    }

}
