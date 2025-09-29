import java.time.YearMonth;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args) 
    {
        Field playField = new Field((byte)10, (byte)10, (byte)20);
        Scanner scaner = new Scanner(System.in);
        String input;

        while (!playField.BOOM)
        {
            System.out.flush();
            String s = playField.ReadOut();
            System.out.println(s + "\n\n");
            input = "";
            
            while (input == "")
            {
                System.out.print("Pick to uncover/tag [x,y](+F): ");
                input = scaner.nextLine();
                input = input.trim();
                input = playField.tryUncover(input);
            }

        }
        scaner.close();
        System.out.println(playField.ReadOut());
        System.out.println("GAME OVER");
    }
}

public class Field
{
    public boolean BOOM;
    public byte flagCouter;
    Land[][] field; 
    Byte[] size;
    Byte mineCount;
    Random random; 

    public Field(Byte x, Byte y, Byte mines)
    {
        random = new Random();
        size = new Byte[2];
        size[0] = x;
        size[1] = y;
        mineCount = mines;
        flagCouter = 0;

        field = new Land[size[0]][size[1]];
        BOOM = false;
        MineField();
    }
    public String ReadOut()
    {
        String out = "";
        for (int y = 0; y < size[1]; y++)
        {
            out += size[1]-y-1 + "\t";
            for (int x = 0; x < size[0]; x++)
            {
                if (!BOOM)
                    out += field[x][y].getValue() + " ";
                else
                    out += field[x][y].getRealValue() + " ";
            }
            out += "\n";
        }

        out += "\n⚑: " + flagCouter + "\t";
        char c = 'A';
        for (int i = 0; i < size[0]; c++, i++)
        {
            out += c + " ";
        }

        return out;
    }
    public String tryUncover(String input)
    {
        // A1 or B2F
        input = input.toUpperCase();
        
        try 
        {
            char c = input.charAt(0);
            int  y = Integer.parseInt(Character.toString(input.charAt(1)));
            y = 9 - y;
            char flag = ' ';

            if (2 < input.length())
                flag = input.charAt(2);
            int x = c - 'A';

            //System.out.println("[" + cValue + "," + n + "]");

            if (!field[x][y].isClicked())
            {                
                if (flag == 'F')
                    flagCouter += field[x][y].flag();

                // on click
                else if (field[x][y].click())
                {
                    if (field[x][y] instanceof Mine m)
                    {
                        m.Boom();
                        BOOOOOM();
                    }
                    else 
                        ClickAround(x,y);
                }
                
                return " ";
            }   
        } 
        catch (Exception e) 
        {

        }

        return "";
    }
    // Detonates all Mines if is BOOOOOM
    private void BOOOOOM()
    {
        BOOM = true;
        for (int y = 0; y < size[1]; y++)
            for (int x = 0; x < size[0]; x++)
                if (field[x][y] instanceof Mine)
                   field[x][y].click(); 
    }
    private void MineField()
    {
        int LandCount = size[0] * size[1];
        List<Integer> list = new ArrayList<>();
        int LandMines = 0; 
        
        // Generate mines
        while (LandMines < mineCount)
        {
            int rand = random.nextInt(LandCount);
            if (!list.contains(rand))
            {
                list.add(rand);
                LandMines++;
            }
        }

        // Create land on Field
        for (int y = 0; y < size[1]; y++)
            for (int x = 0; x < size[0]; x++)
                field[x][y] = new Land();

        // Land mines
        for (int y = 0; y < size[1]; y++)
            for (int x = 0; x < size[0]; x++)
                if (list.contains(x + (y * size[0])))
                {
                    field[x][y] = new Mine();
                    AddToHood(x,y);
                }
    }
    private void AddToHood(int x, int y)
    {
        // Get all surroundings

        for (int i = -1; i < 2; i+=1)
        {
            for (int ii = -1; ii < 2; ii += 1)
            {
                int tx = x + i, ty = y + ii;
                if (i == 0 && 0 == ii)
                    continue;

                // check if in bounds
                else if (0 <= tx && tx < size[0] && 0 <= ty && ty < size[1])
                    field[tx][ty].addNeighbour();
            }
        }
    }
    private void ClickAround(int x, int y)
    {
        // Get all surroundings

        for (int i = -1; i < 2; i+=1)
        {
            for (int ii = -1; ii < 2; ii += 1)
            {
                int tx = x + i, ty = y + ii;
                if (i == 0 && 0 == ii)
                    continue;

                // check if in bounds
                else if (0 <= tx && tx < size[0] && 0 <= ty && ty < size[1])
                    if (field[tx][ty].click())
                        ClickAround(tx,ty);
            }
        }
    }
    private class Land
    {
        protected boolean clicked = false;
        protected boolean flagged = false;
        private int neighbours = 0;

        public boolean click()
        {
            boolean c = clicked;
            this.clicked = true;
            return neighbours == 0 && !c;
        }
        public byte flag()
        {
            flagged = !flagged;
            byte b = 1;
            if (!flagged)
                b = -1;
            return b;
        }
        public void addNeighbour()
        {
            neighbours++;
        }
        public char getRealValue()
        {
            char c;
            
            if (flagged)
                c = '✚';
            else 
                c = getValue();

            return c;
        }
        public char getValue()
        {
            char c = ' ';
            if (clicked)
            {
                if (neighbours != 0)
                    c = (char)(neighbours + '0');
            }
            else if (flagged)
                c = '⚑';
            else
                c = '□';

            return c;
        }
        public boolean isClicked()
        {
            return clicked;
        }
    }
    private class Mine extends Land
    {
        private boolean boom = false;

        public boolean click()
        {
            this.clicked = true;
            // BOOOM !!
            return true;
        }
        public void Boom()
        {
            boom = true;
        }
        public char getValue()
        {
            if (this.clicked)
                return '✷';
            else
                return super.getValue();
        }
        public char getRealValue()
        {
            char c;
            if (flagged)
                c = '✽';
            else if (boom)
                c = '☼';
            else
                c = getValue();

            return c;
        }
    }
}