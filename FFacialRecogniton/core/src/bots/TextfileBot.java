package bots;

import bots.Bot;
import java.util.List;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.io.*;
import bots.skill.text.textskill;

public class TextfileBot{

    private String function_name;
    Dictionary<String, Integer> dict_key= new Hashtable<>();
    List<List<String>> lines= new ArrayList<>();
    //String[] search= new String[] { "Weather","Monday","Thursday","Climate", "Schedule"};
    List<textskill> skills= new ArrayList<>();
    ArrayList<String> searchlist= new ArrayList<>();
    public TextfileBot()
    {
        //read_from_file("D:\\DKE-(19-22)\\Year-2\\Project 2-2\\Project-2.2-Group10\\core\\src\\bots\\resources\\weather.csv");
    }
    public static void main(String args[]) throws IOException {
        System.out.println("Welcome! My name is Billy.");
        while(true) {
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter your question");
            String s = sc.nextLine();
            TextfileBot bot = new TextfileBot();
            String answer;
            answer = bot.ask(s);
            System.out.println(answer);
        }
    }

    public String write_to_file(String file, String[][] skill) throws IOException
    {
        File obj = new File(file);

        if (obj.exists())
        {
            obj.delete();
        }
        else
        {
            try
            {

                FileWriter writer = new FileWriter(file + ".csv");
                for (int i = 0; i < skill.length; i++)
                {
                    for (int j = 0; j < skill[i].length; j++)
                    {
                        writer.append(skill[i][j]);
                        writer.append(",");
                    }
                    writer.append("/n");
                }
                writer.close();
            }
            catch (Exception e)
            {
                e.getStackTrace();
            }
        }

        return null;
    }

    //@Override
    public List<List<String>> read_from_file(String file) {
        lines.clear();

        try{
            BufferedReader br=new BufferedReader(new FileReader(file));

            String row;



                while ((row=br.readLine())!= null)
                {
                    String values[]= row.split(",");
                    lines.add(Arrays.asList(values));
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }

            for(int i=1;i<lines.get(0).size();i++)
            {
                dict_key.put(lines.get(0).get(i), i);
                searchlist.add(lines.get(0).get(i));

            }
            return lines;
        }


    public ArrayList<String> tokenize(String S)
    {
        ArrayList<String> tokens= new ArrayList<>();
        StringTokenizer st= new StringTokenizer(S," ");
        while (st.hasMoreTokens())
        {
            tokens.add(st.nextToken());
        }
       return tokens;
    }
    public ArrayList<String> keywords(ArrayList<String> S, ArrayList<String> search)
    {

        //ArrayList<String> s = new ArrayList<>();
        ArrayList<String> query = new ArrayList<>();
        for(int i=0;i< search.size();i++)
        {
            for(int j=0;j<S.size();j++)
            {
                if(S.get(j).equalsIgnoreCase(search.get(i)))
                {
                    query.add(search.get(i));
                }
            }

        }
        return query;
    }

    public void load_file(ArrayList<String> tokens  ,  String question) throws IOException
    {

        BufferedReader br= new BufferedReader(new FileReader("core\\src\\bots\\resources\\Filename.txt"));
        String st;
        ArrayList<String> files= new ArrayList<>();
        while((st=br.readLine())!=null)
        {
            files.add(st);
        }
        ArrayList<String> query = keywords(tokens,files);
        read_from_file("core\\src\\bots\\resources"+"\\"+query.get(0)+".csv");
    }

    public String ask(String question) throws IOException{
        ArrayList<String> tokens = tokenize(question);
        /*load_file(tokens,question);
        // file has been loaded


        String answer;
        ArrayList<String> query = keywords(tokens,searchlist);
        answer= query.toString();
        if ((query.size() < 2 || query.size() > 2))
            answer = "I am unable to process your request. Try again.";
        else {
            ArrayList<Integer> keyword_index = new ArrayList<>();
            for (int i = 0; i < query.size(); i++) {
                keyword_index.add(dict_key.get(query.get(i)));
            }
            answer = (lines.get(keyword_index.get(0)).get(keyword_index.get(1))).toString();

        }*/
        String answer;
        answer= tokens.toString();
        return answer;
    }

    /**
     *Creates new skill as the .csv file and stores the keywords and corresponding answers
     * @param file file name
     * @param keywords keywords in the file
     * @param answers desired answers
     * @throws IOException if file not created
     */
    public void createSkill(String file,List<String> keywords, List<List<String>> answers) throws IOException {
        textskill skill = new textskill(file,keywords,answers);
        skills.add(skill);
        File myObj = new File(file+".csv");
        boolean flag= myObj.createNewFile();
        String newskill[][]= new String[keywords.size()+1][keywords.size()+1];

        for(int i=0;i<keywords.size();i++)
        {
            for(int j=0; j<keywords.size();j++)
            {
                if((i==0) && (j==0))
                {
                    newskill[0][0]= " ";
                }
                else if(i==0)
                {
                    newskill[i][j]= keywords.get(j-1);
                }
                else if(j==0)
                {
                    newskill[i][j]= keywords.get(i-1);
                }
                else
                {
                    newskill[i][j]= answers.get(i-1).get(j-1);
                }
            }
        }
        write_to_file(file, newskill);




    }

    public void editskill(String file, String keyword1, String keyword2, String answer) throws IOException
    {
        read_from_file(file);
        lines.get(dict_key.get(keyword1)).set(dict_key.get(keyword2),answer);
        List<List<String>> temp = lines;
        write_to_file(file, (String[][]) temp.toArray());
    }

}
