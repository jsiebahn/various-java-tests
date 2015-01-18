package com.github.jsiebahn.various.tests.lesscss;

import org.junit.Test;
import org.lesscss.LessCompiler;
import org.lesscss.LessException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * $Id$
 *
 * @author jsiebahn
 * @since 06.12.14 09:49
 */
public class LessCompileTest {

    @Test
    public void shouldCompileLess() throws LessException, URISyntaxException, IOException {

        LessCompiler compiler = new LessCompiler();

        compiler.setCompress(true);

        // compiler.setOptions(Arrays.asList(""));

        assertEquals(".c{color:#fff}", compiler.compile("@col: #fff; .c{color: @col;}").trim());

        URL classpathFile = LessCompileTest.class.getResource("/test.less");
        File file = new File(classpathFile.toURI());

        assertEquals(".foo{color:#000;background:url('Orange.gif')}", compiler.compile(file).trim());
    }

}
