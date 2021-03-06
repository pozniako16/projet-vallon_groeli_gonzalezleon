package ch.heigvd.VallonGroeliGonzalezLeon.statique.command.api;/*
 * @File MdAPITest.java
 * @Authors : David González León
 * @Date 19 mars 2021
 */

import ch.heigvd.VallonGroeliGonzalezLeon.statique.util.Util;
import org.apache.commons.io.FileUtils;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class MdAPITest {

   private final File currentDirectory = new File(new File(".").getCanonicalPath() + "/Workspace");
   private final File buildDirectory = new File(currentDirectory.getPath() + "/build");

   MdAPITest() throws IOException {}


   @BeforeEach
   void setUp() {
      currentDirectory.mkdir();
      buildDirectory.mkdir();
   }

   @AfterEach
   void tearDown() throws IOException {
      FileUtils.deleteDirectory(currentDirectory);
   }

   @Test
   void testInitMdFileCreatesAFile() throws IOException {
      File file = new File(currentDirectory.getPath() + "/test.md");
      assertFalse(file.exists());
      MdAPI.initMdIndexFile(file);
      assertTrue(file.exists());
   }

   @Test
   void testInitMdFileThrowsExceptionCorrectly() throws IOException {
      File file = new File(currentDirectory.getPath() + "/test.md");
      Util.writeFile("Test", new FileWriter(file));
      assertThrows(IllegalArgumentException.class, () -> {MdAPI.initMdIndexFile(file);});
   }


   @Test
   void testAnalyseFile() throws IOException {
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      LocalDateTime now = LocalDateTime.now();
      String fileContent =
              "titre:Mon premier article\n" + "auteur:John Smith\n" + "date:" + dtf.format(now) + "\n" + "---\n" +
              "# Mon premier article\n" + "## Mon sous-titre\n" + "Le contenu de mon article.\n";
      String content = "# Mon premier article\n" + "## Mon sous-titre\n" + "Le contenu de mon article.\n";
      String author = "John Smith";
      String date = dtf.format(now);
      String titre = "Mon premier article";
      File mdFile = new File(currentDirectory.getPath()+"/mdFile.md");
      Util.writeFile(fileContent,
                     new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mdFile), StandardCharsets.UTF_8)));
      assertTrue(mdFile.exists());
      MdAPI.MdContent mdContent = MdAPI.analyseFile(mdFile);
      assertEquals(author,mdContent.getAuthor());
      Parser parser = Parser.builder().build();
      Node document = parser.parse(content);
      HtmlRenderer renderer = HtmlRenderer.builder().build();
      assertEquals(renderer.render(document),mdContent.getContent());
      assertEquals(date,mdContent.getDate());
      assertEquals(titre,mdContent.getPageTitle());
   }
}