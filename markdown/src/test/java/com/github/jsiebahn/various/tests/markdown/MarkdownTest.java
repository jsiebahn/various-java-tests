package com.github.jsiebahn.various.tests.markdown;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * $Id$
 *
 * @author  jsiebahn
 * @since 25.09.14 07:09
 */
public class MarkdownTest {

    @Test
    public void testCreateBlockHtml() {

        PegDownProcessor processor = new PegDownProcessor(Extensions.ALL + Extensions.SUPPRESS_ALL_HTML);

        testAll(processor,
                "HelloWorld",
                "UnorderedList",
                "OneParagraph",
                "TwoParagraphs",
                "InlineBold",
                "Ellipses",
                "SimpleLink",
                "DefinitionList",
                "Quotes"
        );

    }

    private void testAll(PegDownProcessor processor, String... filenames) {
        for (String testFile : filenames) {
            assertEquals("Parsing failed for '" + testFile + "'",
                    getHtml(testFile), processor.markdownToHtml(getMd(testFile)));
        }
    }

    private String getMd(String filenameWithoutExtension) {
        return readFile(filenameWithoutExtension + ".md");
    }

    private String getHtml(String filenameWithoutExtension) {
        return readFile(filenameWithoutExtension + ".html");
    }

    private String readFile(String resourceName) {
        try {
            return IOUtils.toString(getClass().getResourceAsStream(resourceName));
        } catch (IOException e) {
            fail("Could not read '" + resourceName + "': " + e.getMessage());
        }
        return "";
    }
}
