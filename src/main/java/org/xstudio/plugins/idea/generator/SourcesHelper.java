package org.xstudio.plugins.idea.generator;

import java.io.*;

/**
 * @author xiaobiao
 * @version 2019/3/21
 */
public class SourcesHelper {
    public static InputStream stringToStream(String s, String encoding) throws UnsupportedEncodingException {
        byte[] rawData = encoding != null ? s.getBytes(encoding) : s.getBytes();
        return new ByteArrayInputStream(rawData);
    }
}
