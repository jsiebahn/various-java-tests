package com.github.jsiebahn.various.tests.markdown;

import com.vladsch.flexmark.ext.abbreviation.AbbreviationExtension;
import com.vladsch.flexmark.ext.definition.DefinitionExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.typographic.TypographicExtension;
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profiles.pegdown.Extensions;
import com.vladsch.flexmark.profiles.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.options.MutableDataSet;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * $Id$
 *
 * @author  jsiebahn
 * @since 25.09.14 07:09
 */
@RunWith(Parameterized.class)
public class MarkdownTest {

    private Parser parser;
    private HtmlRenderer htmlRenderer;

    private String testFilename;

    @Before
    public void setUp() {
        DataHolder OPTIONS = PegdownOptionsAdapter.flexmarkOptions(
                Extensions.ALL
        );
        MutableDataSet options = new MutableDataSet();
        // uncomment to set optional extensions
        options.set(Parser.EXTENSIONS, Arrays.asList(
                TablesExtension.create(),
                StrikethroughExtension.create(),
                DefinitionExtension.create(),
                TypographicExtension.create(),
                YamlFrontMatterExtension.create(),
                AbbreviationExtension.create()
                )
        );
        options.set(HtmlRenderer.GENERATE_HEADER_ID, true);
        options.set(HtmlRenderer.HEADER_ID_GENERATOR_RESOLVE_DUPES, true);
        options.set(HtmlRenderer.INDENT_SIZE, 2);

        parser = Parser.builder(options).build();
        htmlRenderer = HtmlRenderer.builder(options).build();
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[] {"HelloWorld"},
                new Object[] {"UnorderedList"},
                new Object[] {"OneParagraph"},
                new Object[] {"TwoParagraphs"},
                new Object[] {"InlineBold"},
                new Object[] {"Ellipses"},
                new Object[] {"SimpleLink"},
                new Object[] {"DefinitionList"},
                new Object[] {"Quotes"},
                new Object[] {"MetadataAbbreviations"},
                // new Object[] {"MetadataPandoc"}, Seems to be not supported
                // new Object[] {"MetadataMultimarkdown"}, // there seems to be a general profile
                new Object[] {"MetadataYaml"}
                );
    }

    public MarkdownTest(String testFilename) {
        this.testFilename = testFilename;
    }

    @Test
    public void test() {
        assertThat(normalize(htmlRenderer.render(parser.parse(getMd(testFilename)))))
                .isEqualTo(normalize(getHtml(testFilename)));
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
        return s.replaceAll("(\r\n|\r|\n)", "\n").trim();
    }
}
