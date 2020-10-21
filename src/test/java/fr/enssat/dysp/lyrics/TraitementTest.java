package fr.enssat.dysp.lyrics;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class TraitementTest {

    @BeforeClass
    public static void setUp() {
        Traitement.init();
    }

    @Test
    public void testTraiter_EvangelionSplitFullKanji() {

        String inputContent = extractFromResource("in/EvangelionOPFullKanji.sample");
        String expectedContent = extractFromResource("out/EvangelionOPFullKanji.split.sample");

        String actual = Traitement.traiter(inputContent);

        assertEquals(actual, expectedContent);

    }

    @Test
    public void testTraiter_EvangelionSplitFullRomaji() {

        String inputContent = extractFromResource("in/EvangelionOPFullRomaji.sample");
        String expectedContent = extractFromResource("out/EvangelionOPFullRomaji.split.sample");

        String actual = Traitement.traiter(inputContent);

        assertEquals(actual, expectedContent);
    }

    @Test
    public void testTraiter_WhenBlankInput_ReturnEmptyString() {
        assertEquals("", Traitement.traiter(null));
    }

    @Test
    public void testTraiter_EvangelionRomajiSplitOptimizeAndSplitThatShit() {

        String inputContent = extractFromResource("in/EvangelionOPFullRomaji.sample");

        String trait = Traitement.traiter(inputContent);

        String expectedContent = extractFromResource("out/EvangelionOPFullRomaji.split.optimize.sample");
        String expectedSplitThatShit = extractFromResource("out/EvangelionOPFullRomaji.split.optimize.chorus.sample");

        String actualOptimize = Traitement.optimize(trait);

        assertEquals(actualOptimize, expectedContent);

        String splitThatShitActual = Traitement.splitThatShit(actualOptimize);

        assertEquals(splitThatShitActual, expectedSplitThatShit);

    }

    private String extractFromResource(String directory) {
        ClassLoader classLoader = getClass().getClassLoader();
        String content = "";
        try (InputStream inputStream = classLoader.getResourceAsStream(directory)) {

            content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

}
