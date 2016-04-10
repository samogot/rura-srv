package ru.ruranobe.engine.wiki.parser;

import java.util.ArrayList;

public class QuoteParser {

    ArrayList<Integer> counts = new ArrayList<>(); // Count of Quote
    ArrayList<Integer> positions = new ArrayList<>(); // Position of Quote in source text
    ArrayList<StringBuilder> replacements = new ArrayList<>(); // <b> <i> </b> </i> and extra ' symbols
    StringBuilder lastTags = new StringBuilder(); // can contain </b> </i> - closing opened and not closed <b>, <i>

    protected void getQuotes(String text)
    {
        for (int i = 0; i < text.length(); )
        {
            if (text.codePointAt(i) == '\'')
            {
                int j = i;
                while (i < text.length() && text.codePointAt(i) == '\'')
                {
                    i++;
                }
                counts.add(i-j);
                positions.add(j);
            }
            i++;
        }
    }

    protected void parse()
    {
        ArrayList<String> answer = new ArrayList<>();
        ArrayList<Integer> poss = new ArrayList<>();
        int pos = 0;
        int i = -1;
        int j = -1;
        while (true)
        {
            boolean threeQOpened = i>-1; // three quotes was opened
            boolean twoQOpened = j>-1;   // two quotes was opened

            if (pos > counts.size()-1)
            {
                break;
            }

            if (counts.get(pos)-3 > -1)
            {
                if (threeQOpened)
                {
                    if (j > i && poss.get(j).equals(poss.get(i)))
                    {
                        // swap answer.get(j) and answer.get(i)
                        String temp = answer.get(j);
                        answer.set(j, answer.get(i));
                        answer.set(i, temp);

                        // swap i and j
                        int temp2 = j;
                        j = i;
                        i = temp2;
                    }

                    if ("<b>".equals(answer.get(Math.max(i,j))))
                    {
                        i = -1;
                        answer.add("</b>");
                        poss.add(pos);
                    }
                }
                else
                {
                    i = answer.size();
                    answer.add("<b>");
                    poss.add(pos);
                }
                counts.set(pos, counts.get(pos)-3);
            }
            else if (counts.get(pos)-2 > -1)
            {
                if (twoQOpened)
                {
                    if (i > j && poss.get(j).equals(poss.get(i)))
                    {
                        // swap answer.get(j) and answer.get(i)
                        String temp = answer.get(j);
                        answer.set(j, answer.get(i));
                        answer.set(i, temp);

                        // swap i and j
                        int temp2 = j;
                        j = i;
                        i = temp2;
                    }

                    if ("<i>".equals(answer.get(Math.max(i,j))))
                    {
                        j = -1;
                        answer.add("</i>");
                        poss.add(pos);
                    }
                }
                else
                {
                    j = answer.size();
                    answer.add("<i>");
                    poss.add(pos);
                }
                counts.set(pos, counts.get(pos)-2);
            }
            else
            {
                pos++;
            }
        }

        for (int k = 0; k < counts.size(); ++k)
        {
            replacements.add(new StringBuilder());
        }

        for (int k = 0; k < answer.size(); ++k)
        {
            int position = poss.get(k);
            replacements.set(position, replacements.get(position).append(answer.get(k)));
        }

        for (int k = 0; k < counts.size(); ++k)
        {
            StringBuilder temp = new StringBuilder();
            for (int y = 0; y < counts.get(k); y++)
            {
                temp.append('\'');
            }
            replacements.set(k, replacements.get(k).append(temp));
        }

        if ((i > j && i > -1)
                || (j < 0 && i > -1))
        {
            i = -1;
            lastTags.append("</b>");
        }
        if ((j > i && j > -1)
                || (i < 0 && j > -1))
        {
            j = -1;
            lastTags.append("</i>");
        }
        if ((i > j && i > -1)
                || (j < 0 && i > -1))
        {
            i = -1;
            lastTags.append("</b>");
        }
        if ((j > i && j > -1)
                || (i < 0 && j > -1))
        {
            lastTags.append("</i>");
        }
    }

    public String applyTo(String text)
    {
        getQuotes(text);
        parse();

        StringBuilder result = new StringBuilder();
        int j = 0;
        for (int i = 0; i < text.length(); )
        {
            if (j < positions.size() && positions.get(j) == i)
            {
                result.append(replacements.get(j));
                while (i < text.length() && text.codePointAt(i) == '\'')
                {
                    i++;
                }
                i--;
            }
            else
            {
                result.appendCodePoint(text.codePointAt(i));
            }

            if (j < positions.size() && i > positions.get(j))
            {
                j++;
            }
            i++;
        }
        result.append(lastTags);
        return result.toString();
    }
}
