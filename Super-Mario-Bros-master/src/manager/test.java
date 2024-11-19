package manager;

import view.ImageLoader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class test {
    public static void main(String[] args) throws Exception,IllegalArgumentException, FileNotFoundException {
        File f=new File("high_score.txt");
        Scanner in=new Scanner(f);
        while(in.hasNextLine()){
            String xau=in.nextLine();
            System.out.println(xau);
        }
    }

}
