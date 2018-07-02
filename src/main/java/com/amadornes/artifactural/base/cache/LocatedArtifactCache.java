package com.amadornes.artifactural.base.cache;

import com.amadornes.artifactural.api.artifact.Artifact;
import com.amadornes.artifactural.api.artifact.ArtifactIdentifier;

import java.io.File;

public class LocatedArtifactCache extends ArtifactCacheBase {

    private final File path;

    public LocatedArtifactCache(File path) {
        this.path = path;
    }

    @Override
    public Artifact store(Artifact artifact, String specifier) {
        ArtifactIdentifier identifier = artifact.getIdentifier();
        File cachePath = new File(path.getAbsolutePath()
                .replace("${GROUP}", identifier.getGroup())
                .replace("${NAME}", identifier.getName())
                .replace("${VERSION}", identifier.getVersion())
                .replace("${CLASSIFIER}", identifier.getClassifier())
                .replace("${EXTENSION}", identifier.getExtension())
                .replace("${SPECIFIER}", specifier)
                .replace("${META_HASH}", artifact.getMetadata().getHash())
        );
        return doStore(cachePath, artifact);
    }

}
