package com.amadornes.artifactural.base.transform;

import com.amadornes.artifactural.api.artifact.Artifact;
import com.amadornes.artifactural.api.artifact.ArtifactMetadata;
import com.amadornes.artifactural.api.artifact.ArtifactType;
import com.amadornes.artifactural.api.transform.ArtifactTransformer;
import com.amadornes.artifactural.base.artifact.StreamableArtifact;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ExclusiveTransformer implements ArtifactTransformer {

    public static ExclusiveTransformer of(boolean whitelist, String... filters) {
        return new ExclusiveTransformer(whitelist, filters);
    }

    private final Set<Pattern> filters = new HashSet<>();
    private final boolean whitelist;

    private ExclusiveTransformer(boolean whitelist, String... filters) {
        this.whitelist = whitelist;
        for (String s : filters) {
            String regex = s
                    .replaceAll("(?:(^|[^\\w\\*])\\*\\*([^\\w\\*]|$))", "$1.*$2") // ** matches anything
                    .replaceAll("(?:(^|[^\\w\\*])\\*([^\\w\\*]|$))", "$1[^\\/]*$2"); // * matches anything but /
            this.filters.add(Pattern.compile(regex));
        }
    }

    @Override
    public Artifact transform(Artifact artifact) {
        if (!artifact.isPresent()) return Artifact.none();

        if (artifact.getType() == ArtifactType.BINARY || artifact.getType() == ArtifactType.SOURCE) {
            return exclude(artifact);
        } else {
            return Artifact.none();
        }
    }

    @Override
    public ArtifactMetadata withInfo(ArtifactMetadata metadata) {
        return metadata.with("EXCLUDE", filters.stream().map(Pattern::pattern).collect(Collectors.joining(";")));
    }

    private Artifact exclude(Artifact artifact) {
        return StreamableArtifact.ofStreamable(artifact.getIdentifier(), artifact.getType(),
                () -> new ZipInputStream(artifact.openStream()) {
                    @Override
                    public ZipEntry getNextEntry() throws IOException {
                        ZipEntry next;
                        while ((next = super.getNextEntry()) != null) {
                            if (isAllowed(next.getName())) {
                                return next;
                            }
                        }
                        return null;
                    }
                });
    }

    private boolean isAllowed(String name) {
        if (whitelist) {
            return filters.stream().anyMatch(p -> p.asPredicate().test(name));
        } else {
            return filters.stream().noneMatch(p -> p.asPredicate().test(name));
        }
    }

}
