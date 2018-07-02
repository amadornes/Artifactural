package com.amadornes.artifactural.api.artifact;

import java.util.function.Predicate;

public interface ArtifactIdentifier {

    static ArtifactIdentifier none() {
        return Internal.NO_IDENTIFIER;
    }

    String getGroup();

    String getName();

    String getVersion();

    String getClassifier();

    String getExtension();

    static Predicate<ArtifactIdentifier> groupMatches(String group) {
        return identifier -> identifier.getGroup().matches(group);
    }

    static Predicate<ArtifactIdentifier> nameMatches(String name) {
        return identifier -> identifier.getName().matches(name);
    }

    static Predicate<ArtifactIdentifier> versionMatches(String version) {
        return identifier -> identifier.getVersion().matches(version);
    }

    static Predicate<ArtifactIdentifier> classifierMatches(String classifier) {
        return identifier -> identifier.getClassifier().matches(classifier);
    }

    static Predicate<ArtifactIdentifier> extensionMatches(String extension) {
        return identifier -> identifier.getExtension().matches(extension);
    }

    static Predicate<ArtifactIdentifier> groupEquals(String group) {
        return identifier -> identifier.getGroup().equals(group);
    }

    static Predicate<ArtifactIdentifier> nameEquals(String name) {
        return identifier -> identifier.getName().equals(name);
    }

    static Predicate<ArtifactIdentifier> versionEquals(String version) {
        return identifier -> identifier.getVersion().equals(version);
    }

    static Predicate<ArtifactIdentifier> classifierEquals(String classifier) {
        return identifier -> identifier.getClassifier().equals(classifier);
    }

    static Predicate<ArtifactIdentifier> extensionEquals(String extension) {
        return identifier -> identifier.getExtension().equals(extension);
    }

}
