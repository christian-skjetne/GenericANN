package mnist;

import java.io.IOException;

import ann.Ann;
import ann.InputOutputSizeException;

public class Main {

    public static void main(String[] args){
        try {
            Main m = new Main();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Draw drawer;
    Ann net;

    public Main() throws IOException 
    {
        net = new Ann("src\\mnist\\annMnist.txt");
        
        Mnist train = new Mnist("src\\mnist\\data\\train-images.idx3-ubyte", "src\\mnist\\data\\train-labels.idx1-ubyte");

        // Lets train
        System.out.println("Lets start training..");
        double[] output = new double[]{0.,0.,0.,0.,0.,0.,0.,0.,0.,0.};
        try 
        {
            for (int i = 0; i < 4; i++) 
            {
                double sumError = 0;
                for (int img = 0; img<train.numberOfImages; img++)
                {
                    output[train.getImageLabel(img)] = 1.0;
                    net.trainInput(train.getImageData(img));
                    net.trainOutput(output);
                    output[train.getImageLabel(img)] = 0.0;
                    sumError += Ann.lastRunError;
                    //if(img > 200) break;
                }
                System.out.printf("Run:"+(i+1)+" Avg run error: %.4f %%\n",(sumError/train.numberOfImages)*100.);   
            }
        }
        catch(InputOutputSizeException e){e.printStackTrace();System.exit(1);}

        train = null;
        System.out.println("------Training Done-----");
        System.out.println("Start testing");

        Mnist test = new Mnist("src\\mnist\\data\\t10k-images.idx3-ubyte", "src\\mnist\\data\\t10k-labels.idx1-ubyte");

        System.out.println("Lets start TESTING..");
        output = new double[]{0.,0.,0.,0.,0.,0.,0.,0.,0.,0.};
        try 
        {
            int correct = 0;
            int wrong = 0;
            for (int img = 0; img<test.numberOfImages; img++)
            {
                output[test.getImageLabel(img)] = 1.0;
                double[] res = net.getAnnRes(test.getImageData(img));
                double max = 0;
                double maxi = 0;
                for (int i=0;i<res.length;i++) 
                {
                    if(res[i]>max) 
                    {
                        max = res[i];
                        maxi = i;
                    }
                }
                if(maxi == test.getImageLabel(img)) correct++;
                else wrong++;
                output[test.getImageLabel(img)] = 0.0;
            }
            System.out.println("Number of correct: "+correct);
            System.out.println("Number of wrong:   "+wrong);
            System.out.printf("Error percent: %.4f %%\n", (100.-((float)correct/((double)correct+wrong))*100.));

        }
        catch(InputOutputSizeException e){e.printStackTrace();System.exit(1);}
        
        drawer = new Draw(this);
    
    }

    public void guess(double[] input)
    {
        try
        {
            double[] res = net.getAnnRes(input);
            double max = 0;
            double maxi = 0;
            for (int i=0;i<res.length;i++) 
            {
                if(res[i]>max) 
                {
                    max = res[i];
                    maxi = i;
                }
            }
            System.out.println("THE GUESS WAS: "+maxi+" conf: "+max);
            System.out.println(Ann.printDoubleArray(2,res));
        }
        catch(InputOutputSizeException e){e.printStackTrace();System.exit(1);}
    }

}
