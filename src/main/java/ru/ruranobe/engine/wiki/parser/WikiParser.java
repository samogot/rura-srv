package ru.ruranobe.engine.wiki.parser;

import java.util.*;

public class WikiParser
{

    public static String parseWikiText(String s)
    {
        for (int i = 0; i < s.length(); )
        {
            int symbolsTillEnd = s.length() - i;
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
                    states7.add(new Replacement(i, 15, 7));
                    i+=15;
                    continue;
                }
            }
            if (symbolsTillEnd > 3)
            {
                if (s.substring(i, i+4).equals("===="))
                {
                    states4.add(new Replacement(i, 4, 4));
                    i+=4;
                    continue;
                }
            }
            if (symbolsTillEnd > 2)
            {
                if (s.substring(i, i+3).equals("\'\'\'"))
                {
                    states6.add(new Replacement(i, 3, 6));
                    i+=3;
                    continue;
                }
                if (s.substring(i, i+3).equals("==="))
                {
                    states3.add(new Replacement(i, 3, 3));
                    i+=3;
                    continue;
                }
            }
            if (symbolsTillEnd > 1)
            {
                if (s.substring(i, i+2).equals("\'\'"))
                {
                    states5.add(new Replacement(i, 2, 5));
                    i+=2;
                    continue;
                }
                if (s.substring(i, i+2).equals("=="))
                {
                    states2.add(new Replacement(i, 2, 2));
                    i+=2;
                    continue;
                }
                if (s.substring(i, i+2).equals("}}"))
                {
                    states8.add(new Replacement(i, 2, 8));
                    i+=2;
                    continue;
                }
            }
            if (symbolsTillEnd > 0)
            {
                if (s.codePointAt(i) == '\n')
                {
                    states9.add(new Replacement(i, 1, 9));
                    states9.add(new Replacement(i, 1, 9));
                    i++;
                    continue;
                }
            }
            i++;
        }

        int j = 0;
        for (Replacement state1 : states1)
        {
            int startPoint = state1.startPoint;
            for (;j < states8.size(); ++j)
            {
                if (states8.get(j).startPoint > startPoint)
                {
                    state1.setEndPoint(states8.get(j).startPoint + 2);
                    state1.setEndSequenceLength(2);
                    states8.remove(j);
                    j--;
                }
            }
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

        StringBuilder parsedWikiText = new StringBuilder("<p>");
        for (int i = 0; i < s.length(); )
        {
            Replacement r = startPointToReplacement.get(i);
            Replacement r2 = endPointToReplacement.get(i);
            boolean updated = false;
            if (r != null)
            {
                parsedWikiText.append(r.getHtmlStartSequence());
                i+=r.startSequenceLength;
                updated = true;
            }
            if (r2 != null)
            {
                parsedWikiText.append(r2.getHtmlEndSequence());
                i+=!updated ? r2.endSequenceLength : 0;
                updated = true;
            }
            if (!updated)
            {

                parsedWikiText.appendCodePoint(s.codePointAt(i));
                i++;
            }
        }
        parsedWikiText.append("</p>");
        return parsedWikiText.toString();
    }


    private static void addReplacementPoints(Map<Integer, Replacement> startPointToReplacement, Map<Integer, Replacement> endPointToReplacement, List<Replacement> states)
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

    private static void resolveObviousTags(List<Replacement> tags)
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

            stateIdToHtmlStartSequence.put(9, "</p>");
            stateIdToHtmlEndSequence.put(9,"<p>");
        }
    }

    private static final List<Replacement> states1 = new ArrayList<Replacement>();
    private static final List<Replacement> states2 = new ArrayList<Replacement>();
    private static final List<Replacement> states3 = new ArrayList<Replacement>();
    private static final List<Replacement> states4 = new ArrayList<Replacement>();
    private static final List<Replacement> states5 = new ArrayList<Replacement>();
    private static final List<Replacement> states6 = new ArrayList<Replacement>();
    private static final List<Replacement> states7 = new ArrayList<Replacement>();
    private static final List<Replacement> states8 = new ArrayList<Replacement>();
    private static final List<Replacement> states9 = new ArrayList<Replacement>();
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
     */
}
