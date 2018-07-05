package com.github.jsiebahn.various.tests.markdown;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
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
    public void testFindContentAfterMetadata() {

        PegDownProcessor processor = new PegDownProcessor(Extensions.ALL + Extensions.SUPPRESS_ALL_HTML);
        String actual = processor.markdownToHtml(
                "*[layout]: post\n"
                + "*[published-on]: 1 January 2000\n"
                + "*[title]: Blogging Like a Boss\n"
                + "\n"
                + "Content goes here.");
        assertEquals("<p>Content goes here.</p>", actual);
    }

    @Test
    @Ignore("Yaml metadata not working with pegdown.")
    public void testFindContentAfterYamlMetadata() {

        PegDownProcessor processor = new PegDownProcessor(Extensions.ALL + Extensions.SUPPRESS_ALL_HTML);
        String actual = processor.markdownToHtml("---\n"
                + "layout: post\n"
                + "published-on: 1 January 2000\n"
                + "title: Blogging Like a Boss\n"
                + "---\n"
                + "\n"
                + "Content goes here.");
        assertEquals("<p>Content goes here.</p>", actual);
    }

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
                    normalize(getHtml(testFile)), normalize(processor.markdownToHtml(getMd(testFile))));
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

    private String normalize(String s) {
        return s.replaceAll("(\r\n|\r|\n)", "\n");
    }
}
