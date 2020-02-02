package mnist;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

class Mnist 
{

    int[] labels;
    double[][] data; //[imageIndex][dataIndex]
    int cols = 0;
    int rows = 0;
    int numberOfImages = 0;
    int dataSize = 0;

    public Mnist(String dataFilePath, String labelFilePath) throws IOException
    {
        DataInputStream dataStrm = new DataInputStream(new BufferedInputStream(new FileInputStream(dataFilePath)));
        int dataMagicNumber = dataStrm.readInt();
        numberOfImages = dataStrm.readInt();
        rows = dataStrm.readInt();
        cols = dataStrm.readInt();

        DataInputStream labelStrm = new DataInputStream(new BufferedInputStream(new FileInputStream(labelFilePath)));
        int labelMagicNumber = labelStrm.readInt();
        numberOfImages = labelStrm.readInt();

        System.out.println("number of images: "+numberOfImages);

        dataSize = cols*rows;
        labels  = new int[numberOfImages]; //labelSize and dataSize have to be the same
        data    = new double[numberOfImages][cols*rows];
        for (int i = 0; i < numberOfImages; i++) 
        {
            labels[i]   = labelStrm.readUnsignedByte();
            // Data
            for (int j = 0; j < rows*cols; j++) 
            {
                data[i][j] = dataStrm.readUnsignedByte()/255.;
            }
        }
        dataStrm.close();
        labelStrm.close();
    }

    public int getImageLabel(int imageIndex)
    {
        return labels[imageIndex];
    }

    public double[] getImageData(int imageIndex)
    {
        return data[imageIndex];
    }

    public void printImage(int imageIndex) {
        System.out.println("label: " + getImageLabel(imageIndex));
        for (int i = 0; i < dataSize; i++) 
        {
            if(getImageData(imageIndex)[i] > .5) 
                System.out.print("#.");
            else 
                System.out.print(" .");
            if(i % cols == 0 && i != 0) System.out.println(); //new row = new line
        }
        System.out.println();
    }

    public static void main(String[] args) 
    {
        try 
        {
            Mnist r = new Mnist("src\\mnist\\data\\t10k-images.idx3-ubyte", "src\\mnist\\data\\t10k-labels.idx1-ubyte");
            System.out.println("now: "+r.numberOfImages);
            r.printImage(r.numberOfImages-1);
            r.printImage(0);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            System.exit(1);
        }
    }
}