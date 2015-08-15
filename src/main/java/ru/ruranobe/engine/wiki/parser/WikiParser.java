package ru.ruranobe.engine.wiki.parser;

import java.util.*;

/**
 * .substring(int beginIndex, int endIndex) complexity in Java 7 (or higher) is O(n)
 * .substring(int beginIndex, int endIndex) complexity in pre-Java 7 or is O(1)
 */
public class WikiParser
{

    public String parseWikiText(String s, List<String> imageUrls, boolean appendExtraImagesAtTheEnd)
    {
        // Detect special tags in text and remember them
        for (int i = 0; i < s.length(); )
        {
            int symbolsTillEnd = s.length() - i;
            if (s.codePointAt(i) == '{')
            {
                if (symbolsTillEnd > 14)
                {
                    if (s.substring(i, i+15).equals("{{Подзаголовок|"))
                    {
                        states1.add(new Replacement(i, 15, 1));
                        i+=15;
                        continue;
                    }

                    if (s.substring(i, i+15).equals("{{Иллюстрация}}"))
                    {
                        states7.add(new Replacement(i, 14, 7));
                        i+=15;
                        continue;
                    }
                }
                if (symbolsTillEnd > 5)
                {
                    if (s.substring(i, i+6).equals("{{ref|"))
                    {
                        states10.add(new Replacement(i, 6, 10));
                        i+=6;
                        continue;
                    }
                }
            }
            if (s.codePointAt(i)=='=')
            {
                if (symbolsTillEnd > 3 && s.substring(i, i+4).equals("===="))
                {
                    states4.add(new Replacement(i, 4, 4));
                    i+=4;
                    continue;
                }
                if (symbolsTillEnd > 2 && s.substring(i, i+3).equals("==="))
                {
                    states3.add(new Replacement(i, 3, 3));
                    i+=3;
                    continue;
                }
                if (symbolsTillEnd > 1 && s.substring(i, i+2).equals("=="))
                {
                    states2.add(new Replacement(i, 2, 2));
                    i+=2;
                    continue;
                }
            }
            if (s.codePointAt(i)=='\'')
            {
                if (symbolsTillEnd > 2 && s.substring(i, i+3).equals("\'\'\'"))
                {
                    states6.add(new Replacement(i, 3, 6));
                    i+=3;
                    continue;
                }
                if (symbolsTillEnd > 1 && s.substring(i, i+2).equals("\'\'"))
                {
                    states5.add(new Replacement(i, 2, 5));
                    i+=2;
                    continue;
                }
            }
            if (s.codePointAt(i)=='}')
            {
                if (symbolsTillEnd > 1 && s.substring(i, i+2).equals("}}"))
                {
                    states8.add(new Replacement(i, 2, 8));
                    i+=2;
                    continue;
                }
            }
            if (s.codePointAt(i) == '\n' && symbolsTillEnd > 0)
            {
                states9.add(new Replacement(i, 1, 9));
                states9.add(new Replacement(i, 1, 9));
                i++;
                continue;
            }
            i++;
        }

        // Connect related special tags together. Determine tag's end position in text.
        int j = 0;
        int k = 0;
        for (int i = 0; i < states8.size(); ++i)
        {
            Replacement state8 = states8.get(i);
            Replacement state1 = null;
            Replacement state10 = null;

            int startPoint = Integer.MAX_VALUE;
            if (j < states1.size())
            {
                state1 = states1.get(j);
                startPoint = Math.min(startPoint, state1.startPoint);
            }
            if (k < states10.size())
            {
                state10 = states10.get(k);
                startPoint = Math.min(startPoint, state10.startPoint);
            }

            if (state8.startPoint > startPoint)
            {
                if (state1 != null && state1.startPoint == startPoint)
                {
                    state1.setEndPoint(state8.startPoint + 2);
                    state1.setEndSequenceLength(2);
                    states8.remove(i);
                    j++;
                    i--;
                }
                else if (state10 != null && state10.startPoint == startPoint)
                {
                    state10.setEndPoint(state8.startPoint + 2);
                    state10.setEndSequenceLength(2);
                    states8.remove(i);
                    k++;
                    i--;
                }
            }
        }

        for (Replacement state7 : states7)
        {
            state7.setEndPoint(state7.startPoint + state7.startSequenceLength+1);
            state7.setEndSequenceLength(1);
        }

        resolveObviousTags(states4);
        resolveObviousTags(states6);
        resolveObviousTags(states3);
        resolveObviousTags(states2);
        resolveObviousTags(states5);
        resolveObviousTags(states9);

        Map<Integer, Replacement> startPointToReplacement = new HashMap<Integer, Replacement> ();
        Map<Integer, Replacement> endPointToReplacement = new HashMap<Integer, Replacement> ();
        addReplacementPoints(startPointToReplacement, endPointToReplacement, states1);
        addReplacementPoints(startPointToReplacement, endPointToReplacement, states2);
        addReplacementPoints(startPointToReplacement, endPointToReplacement, states3);
        addReplacementPoints(startPointToReplacement, endPointToReplacement, states4);
        addReplacementPoints(startPointToReplacement, endPointToReplacement, states5);
        addReplacementPoints(startPointToReplacement, endPointToReplacement, states6);
        addReplacementPoints(startPointToReplacement, endPointToReplacement, states7);
        addReplacementPoints(startPointToReplacement, endPointToReplacement, states9);
        addReplacementPoints(startPointToReplacement, endPointToReplacement, states10);

        // Form output html text
        int paragraphId = 1;
        int referenceId = 0;
        StringBuilder references = new StringBuilder("<h2><span>Примечания</span></h2><ol class=\"references\">");
        StringBuilder parsedWikiText = new StringBuilder("<p id=\'p_id-1\'>");
        Iterator<String> imageUrlsIterator = imageUrls.iterator();
        boolean insideReference = false;
        for (int i = 0; i < s.length(); )
        {
            Replacement r = startPointToReplacement.get(i);
            Replacement r2 = endPointToReplacement.get(i);
            boolean updated = false;
            if (r != null)
            {
                String htmlStartSequence = r.getHtmlStartSequence();
                if (r.stateId == 10)
                {
                    // reference
                    htmlStartSequence = String.format(htmlStartSequence, "cite_ref-" + referenceId, "cite_note-" + referenceId, referenceId);
                    references.append("<li id=\"cite_note-%d\"><a href=\"#cite_ref-%d\">↑</a></span>", referenceId, referenceId);
                    referenceId++;
                }
                else if (r.stateId == 7)
                {
                    // image
                    if (imageUrlsIterator.hasNext())
                    {
                        htmlStartSequence = String.format(htmlStartSequence, imageUrlsIterator.next());
                    }
                    else
                    {
                        htmlStartSequence = String.format(htmlStartSequence, "unknownSource");
                    }
                }

                if (insideReference)
                {
                    references.append(htmlStartSequence);
                }
                else
                {
                    parsedWikiText.append(htmlStartSequence);
                }
                i+=r.startSequenceLength;
                updated = true;
            }
            if (r2 != null)
            {
                if (r2.stateId == 10)
                {
                    references.append("</li>");
                    insideReference = false;
                }
                else
                {
                    String htmlEndSequence = r2.getHtmlEndSequence();
                    if (r2.stateId == 9)
                    {
                        // <p>
                        paragraphId++;
                        htmlEndSequence = String.format(htmlEndSequence, "p_id-"+paragraphId);
                    }

                    if (insideReference)
                    {
                        references.append(htmlEndSequence);
                    }
                    else
                    {
                        parsedWikiText.append(htmlEndSequence);
                    }
                }
                i+=!updated ? r2.endSequenceLength : 0;
                updated = true;
            }
            if (!updated)
            {
                if (insideReference)
                {
                    references.appendCodePoint(s.codePointAt(i));
                }
                else
                {
                    parsedWikiText.appendCodePoint(s.codePointAt(i));
                }
                i++;
            }

            if (r != null && r.stateId == 10)
            {
                insideReference = true;
            }
        }
        if (appendExtraImagesAtTheEnd)
        {
            while(imageUrlsIterator.hasNext())
            {
                parsedWikiText.append(String.format("<img src=\'%s\'>", imageUrlsIterator.next()));
            }
        }
        parsedWikiText.append("</p>");
        references.append("</ol>");
        if (!states10.isEmpty())
        {
            parsedWikiText.append(references.toString());
        }
        return parsedWikiText.toString();
    }


    private void addReplacementPoints(Map<Integer, Replacement> startPointToReplacement, Map<Integer, Replacement> endPointToReplacement, List<Replacement> states)
    {
        for (Replacement r : states)
        {
            if (r.endMatchingSequenceFound)
            {
                startPointToReplacement.put(r.startPoint, r);
                endPointToReplacement.put(r.endPoint-r.endSequenceLength, r);
            }
        }
    }

    private void resolveObviousTags(List<Replacement> tags)
    {
        for (int i = 0; i < tags.size(); i++)
        {
            if (i+1 >= tags.size())
            {
                break;
            }
            Replacement r = tags.remove(i+1);
            tags.get(i).setEndPoint(r.startPoint+r.startSequenceLength);
            tags.get(i).setEndSequenceLength(r.startSequenceLength);
        }
    }

    private static class Replacement
    {
        private final int startPoint;
        private final int startSequenceLength;
        private final int stateId;
        private int endPoint;
        private int endSequenceLength;
        private boolean endMatchingSequenceFound;

        public Replacement(int startPoint, int countOfSymbols, int stateId)
        {
            this.startPoint = startPoint;
            this.startSequenceLength = countOfSymbols;
            this.stateId = stateId;
        }

        public void setEndPoint(int endPoint)
        {
            this.endPoint = endPoint;
            endMatchingSequenceFound = true;
        }

        public void setEndSequenceLength(int endSequenceLength)
        {
            this.endSequenceLength = endSequenceLength;
        }

        public String getHtmlStartSequence()
        {
            return stateIdToHtmlStartSequence.get(stateId) == null ? "" : stateIdToHtmlStartSequence.get(stateId);
        }

        public String getHtmlEndSequence()
        {
            return stateIdToHtmlEndSequence.get(stateId) == null ? "" : stateIdToHtmlEndSequence.get(stateId);
        }

        private static final Map<Integer, String> stateIdToHtmlStartSequence = new HashMap<Integer, String>();
        private static final Map<Integer, String> stateIdToHtmlEndSequence = new HashMap<Integer, String>();
        static
        {
            stateIdToHtmlStartSequence.put(1, "<div class=\"subtitle\">");
            stateIdToHtmlEndSequence.put(1,"</div>");

            stateIdToHtmlStartSequence.put(2, "<h2>");
            stateIdToHtmlEndSequence.put(2,"</h2>");

            stateIdToHtmlStartSequence.put(3, "<h3>");
            stateIdToHtmlEndSequence.put(3,"</h3>");

            stateIdToHtmlStartSequence.put(4, "<h4>");
            stateIdToHtmlEndSequence.put(4,"</h4>");

            stateIdToHtmlStartSequence.put(5, "<i>");
            stateIdToHtmlEndSequence.put(5,"</i>");

            stateIdToHtmlStartSequence.put(6, "<b>");
            stateIdToHtmlEndSequence.put(6,"</b>");

            stateIdToHtmlStartSequence.put(7, "<img src=\'%s\'>");
            stateIdToHtmlEndSequence.put(7,"</img>");

            stateIdToHtmlStartSequence.put(9, "</p>");
            stateIdToHtmlEndSequence.put(9,"<p id=\'%s\'>");

            stateIdToHtmlStartSequence.put(10, "<sup id=\"%s\" class=\"reference\"><a href=\"#%s\">[%d]</a></sup>");
        }
    }

    private final List<Replacement> states1 = new ArrayList<Replacement>();
    private final List<Replacement> states2 = new ArrayList<Replacement>();
    private final List<Replacement> states3 = new ArrayList<Replacement>();
    private final List<Replacement> states4 = new ArrayList<Replacement>();
    private final List<Replacement> states5 = new ArrayList<Replacement>();
    private final List<Replacement> states6 = new ArrayList<Replacement>();
    private final List<Replacement> states7 = new ArrayList<Replacement>();
    private final List<Replacement> states8 = new ArrayList<Replacement>();
    private final List<Replacement> states9 = new ArrayList<Replacement>();
    private final List<Replacement> states10 = new ArrayList<Replacement>();
    /*
     * 1 -> {{Подзаголовок|
     * 2 -> ==
     * 3 -> ===
     * 4 -> ====
     * 5 -> ''
     * 6 -> '''
     * 7 -> {{Иллюстрация}}
     * 8 -> }}
     * 9 -> \n (start and end of paragraph aka <p></p>)
     * 10 -> {{ref|
     */
}
