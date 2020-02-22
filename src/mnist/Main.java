package mnist;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import ann.Ann;
import ann.InputOutputSizeException;

public class Main {

    public static void main(String[] args) {
        try {
            new Main();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Draw drawer;
    Ann net;

    public Main() throws IOException 
    {
        // init the Neural network
        net = new Ann("src\\mnist\\annMnist2.txt");

        // Load the training dataset
        final Mnist train = new Mnist("src\\mnist\\data\\emnist-letters-train-images-idx3-ubyte.bin",
                "src\\mnist\\data\\emnist-letters-train-labels-idx1-ubyte.bin");
        
        // Lets train
        System.out.println("Lets start training.. (This is going to take a long time)");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("Training started at: " + LocalDateTime.now().format(formatter));
        // Number of times we run through the dataset.
        int trainingRuns = 10;

        double[] output = new double[27];// {0.,0.,0.,0.,0.,0.,0.,0.,0.,0.};
        try {
            for (int i = 0; i < trainingRuns; i++) {
                double sumError = 0;
                long startTime = System.currentTimeMillis();
                for (int img = 0; img < train.numberOfImages; img++) {
                    output[train.getImageLabel(img)] = 1.0;
                    net.trainInput(train.getImageData(img));
                    net.trainOutput(output);
                    output[train.getImageLabel(img)] = 0.0;
                    sumError += Ann.lastRunError;
                    if (img % (train.numberOfImages / 25) == 0)
                        System.out.printf("  Current run: %.1f %%\r",
                                (img / (float) train.numberOfImages) * 100);
                }
                float trainTime = (float) (System.currentTimeMillis() - startTime) / 1000.f;
                System.out.printf("Run:" + (i + 1) + " of " + trainingRuns
                                + ". Avg run error: %.4f %% Time: %.0f mins %2.0f secs\n",
                        (sumError / train.numberOfImages) * 100., trainTime / 60.f, trainTime % 60);
            }
        } catch (InputOutputSizeException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("------Training Done-----");
        System.out.println("Training ended at: "+LocalDateTime.now().format(formatter));
        System.out.println("Start testing");

        // Load the test dataset
        Mnist test = new Mnist("src\\mnist\\data\\emnist-letters-test-images-idx3-ubyte.bin","src\\mnist\\data\\emnist-letters-test-labels-idx1-ubyte.bin");
        //Mnist test = new Mnist("src\\mnist\\data\\t10k-images.idx3-ubyte.bin", "src\\mnist\\data\\t10k-labels.idx1-ubyte.bin");

        System.out.println("Lets start TESTING..");
        output = new double[27];//{0.,0.,0.,0.,0.,0.,0.,0.,0.,0.};
        try 
        {
            int correct = 0;    // number of correct guesses
            int wrong = 0;      // number of wrong guesses
            for (int img = 0; img<test.numberOfImages; img++)
            {
                output[test.getImageLabel(img)] = 1.0;
                double[] res = net.getAnnRes(test.getImageData(img));
                double max = 0;     // value of the highest output node (corresponds to the confidence of the guess)
                double maxi = 0;    // index of the most active output node (this is the actual guess)
                // find the output node with the highest activation
                for (int i=0;i<res.length;i++) 
                {
                    if(res[i]>max) 
                    {
                        max = res[i];
                        maxi = i;
                    }
                }
                // compare the network output to the correct answer
                if(maxi == test.getImageLabel(img)) correct++;
                else wrong++;
                output[test.getImageLabel(img)] = 0.0;
            }
            System.out.println("Number of correct: "+correct);
            System.out.println("Number of wrong:   "+wrong);
            System.out.printf("Error percent: %.4f %%\n", (100.-((float)correct/((double)correct+wrong))*100.));

        }
        catch(InputOutputSizeException e){e.printStackTrace();System.exit(1);}
        
        test = null;// use the GC to try to free some memory

        // open a drawing window to draw digits for the network to guess
        drawer = new Draw(this);
    
    }

    /**
     * Performs a guess of which digit is in the input image
     */
    public int guess(double[] input)
    {
        int maxi = -1;
        try
        {
            double[] res = net.getAnnRes(input);
            double max = 0;
            
            for (int i=0;i<res.length;i++) 
            {
                if(res[i]>max) 
                {
                    max = res[i];
                    maxi = i;
                }
            }
            System.out.printf("THE GUESS WAS: %d conf: %.2f\n", maxi, max);
            System.out.println(Ann.printDoubleArray(2,res));
        }
        catch(InputOutputSizeException e){e.printStackTrace();System.exit(1);}
        return maxi;
    }

}
