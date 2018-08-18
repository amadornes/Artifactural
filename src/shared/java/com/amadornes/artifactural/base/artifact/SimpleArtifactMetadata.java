package com.amadornes.artifactural.base.artifact;

import com.amadornes.artifactural.api.artifact.ArtifactMetadata;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleArtifactMetadata implements ArtifactMetadata {

    private final List<Entry> entries = new LinkedList<>();
    private String hash = null;

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
        if (hash != null) return hash;
        try {
            String str = entries.stream().map(Entry::toString).collect(Collectors.joining("\n"));
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = digest.digest(str.getBytes());
            StringBuilder hashBuilder = new StringBuilder();
            for (byte b : hashBytes) {
                hashBuilder.append(String.format("%02x", b));
            }
            return hash = hashBuilder.toString();
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
