package fr.enssat.dysp.lyrics;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TraitementTest {

    @Before
    public void setUp() {
        Traitement.init();
    }

    @Test
    public void testInit() {
        Assert.assertEquals("SAMPLE", Traitement.optimize("SAMPLE"));
    }

    @Test
    public void testTraiter() {
        String lyrics = "hello, again its you and me! 123 go! abc@abc.com:)♡っョ わたしはにほんへいきます ロシア 语大字典";
        String actual = Traitement.traiter(lyrics);
        Assert.assertTrue(actual.contains("/"));

    }

    @Test
    public void testSplitThatShit() {
        String text = "split that shit";
        String actual = Traitement.splitThatShit(text);

        Assert.assertEquals(text, actual);
    }

}
