package ch.cyberduck.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @version $Id:$
 */
public class PathRelativizerTest extends AbstractTestCase {

    @Test
    public void testRelativize() throws Exception {
        assertEquals("/b/path", PathRelativizer.relativize("/a", "/b/path"));
        assertEquals("/path", PathRelativizer.relativize("/a", "/a/path"));
    }
}
