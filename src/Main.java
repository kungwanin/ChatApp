/*
    Main Class
 */


/**
 *
 * @author Neeraj
 */
public class Main 
{
    public static void main(String [] args)
    {
        MainFrame mainFrame = new MainFrame(); 
        Thread thread  = new Thread(mainFrame); 
        thread.start();
    }
}
