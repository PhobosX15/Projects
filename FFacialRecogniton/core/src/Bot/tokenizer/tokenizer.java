package Bot.tokenizer;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class tokenizer {
    public ArrayList<String> tokenize(String S)
    {
        ArrayList<String> tokens= new ArrayList<>();
        StringTokenizer st= new StringTokenizer(S,"");
        while (st.hasMoreTokens())
        {
            tokens.add(st.nextToken());
        }
        return tokens;
    }
    public ArrayList<String> keywords(ArrayList<String> S)
    {
        String[] search= new String[] { "Weather", "Schedule"};
        ArrayList<String> s = new ArrayList<>();
        ArrayList<String> query = new ArrayList<>();
        for(int i=0;i< search.length;i++)
        {
            for(int j=0;j<s.size();j++)
            {
                if(s.get(j).equalsIgnoreCase(search[i]))
                {
                    query.add(search[i]);
                }
            }

        }
        return(query);
    }


}
