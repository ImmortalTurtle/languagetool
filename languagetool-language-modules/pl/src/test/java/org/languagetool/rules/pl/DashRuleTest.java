/* LanguageTool, a natural language style checker
 * Copyright (C) 2005 Daniel Naber (http://www.danielnaber.de)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.rules.pl;

import org.junit.Before;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.language.Polish;
import org.languagetool.rules.AbstractCompoundRule;
import org.languagetool.rules.AbstractCompoundRuleTest;
import org.languagetool.rules.Rule;
import org.languagetool.rules.pl.DashRule;
import org.languagetool.rules.RuleMatch;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DashRuleTest {

  JLanguageTool langTool;
  Rule rule;

  @Before
  public void setUp() throws Exception {
    langTool = new JLanguageTool(new Polish());
    rule = new DashRule();
  }

  @Test
  public void testRule() throws IOException {
    // correct sentences:
    check(0, "Nie róbmy nic na łapu-capu.");
    check(0, "Jedzmy kogel-mogel.");
    // incorrect sentences:
    check(1, "bim – bom", new String[]{"bim-bom"});
    check(1, "Papua–Nowa Gwinea", new String[]{"Papua-Nowa"});
    check(1, "Papua — Nowa Gwinea", new String[]{"Papua-Nowa"});
    check(1, "Aix — en — Provence", new String[]{"Aix-en-Provence"});
  }

  public void check(int expectedErrors, String text) throws IOException {
    check(expectedErrors, text, null);
  }

  /**
   * Check the text against the compound rule.
   * @param expectedErrors the number of expected errors
   * @param text the text to check
   * @param expSuggestions the expected suggestions
   */
  public void check(int expectedErrors, String text, String[] expSuggestions) throws IOException {
    assertNotNull("Please initialize langTool!", langTool);
    assertNotNull("Please initialize 'rule'!", rule);
    RuleMatch[] ruleMatches = rule.match(langTool.getAnalyzedSentence(text));
    assertEquals("Expected " + expectedErrors + "errors, but got: " + Arrays.toString(ruleMatches),
        expectedErrors, ruleMatches.length);
    if (expSuggestions != null && expectedErrors != 1) {
      throw new RuntimeException("Sorry, test case can only check suggestion if there's one rule match");
    }
    if (expSuggestions != null) {
      RuleMatch ruleMatch = ruleMatches[0];
      String errorMessage =
          String.format("Got these suggestions: %s, expected %s ", ruleMatch.getSuggestedReplacements(),
              Arrays.toString(expSuggestions));
      assertEquals(errorMessage, expSuggestions.length, ruleMatch.getSuggestedReplacements().size());
      int i = 0;
      for (Object element : ruleMatch.getSuggestedReplacements()) {
        String suggestion = (String) element;
        assertEquals(expSuggestions[i], suggestion);
        i++;
      }
    }
  }

}