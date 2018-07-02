package com.amadornes.artifactural.base.artifact;

import com.amadornes.artifactural.api.artifact.ArtifactMetadata;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class SimpleArtifactMetadata implements ArtifactMetadata {

    private final LinkedList<Entry> entries = new LinkedList<>();

    public SimpleArtifactMetadata() {
    }

    private SimpleArtifactMetadata(SimpleArtifactMetadata parent, Entry entry) {
        this.entries.addAll(parent.entries);
        this.entries.add(entry);
    }

    @Override
    public ArtifactMetadata with(String key, String value) {
        return new SimpleArtifactMetadata(this, new Entry(key, value));
    }

    @Override
    public String getHash() {
        try {
            String str = entries.stream().map(Entry::toString).collect(Collectors.joining(";;"));
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(str.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static class Entry {

        private final String key, value;

        private Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return '[' + key + ',' + value + ']';
        }

    }

}
