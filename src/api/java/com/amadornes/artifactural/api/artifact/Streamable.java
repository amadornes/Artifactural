package com.amadornes.artifactural.api.artifact;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface Streamable {

    InputStream openStream() throws IOException;

}
