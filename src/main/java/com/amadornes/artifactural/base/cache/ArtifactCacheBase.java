package com.amadornes.artifactural.base.cache;

import com.amadornes.artifactural.api.artifact.Artifact;
import com.amadornes.artifactural.api.cache.ArtifactCache;
import com.amadornes.artifactural.base.artifact.StreamableArtifact;

import java.io.*;

abstract class ArtifactCacheBase implements ArtifactCache {

    Artifact doStore(File path, Artifact artifact) {
        return StreamableArtifact.ofStreamable(artifact.getIdentifier(), artifact.getType(), () -> stream(path, artifact))
                .withMetadata(artifact.getMetadata());
    }

    private InputStream stream(File path, Artifact artifact) throws IOException {
        if (!path.exists()) {
            path.getParentFile().mkdirs();
            path.createNewFile();
            FileOutputStream fos = new FileOutputStream(path);
            InputStream is = artifact.openStream();
            int read;
            byte[] bytes = new byte[256];
            while ((read = is.read(bytes)) > 0) {
                fos.write(bytes, 0, read);
            }
            fos.close();
            is.close();
        }
        return new FileInputStream(path);
    }

}
