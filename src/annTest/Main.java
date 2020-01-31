package annTest;

import ann.Ann;
import ann.InputOutputSizeException;

/**
 * Creates an ANN based on a template and trains it to calculate Sin(x)
 */
public class Main
{
	public static void main(String[] args)
	{
		String path = "annBig.txt";

		Ann net = new Ann(path);

		try
		{
			System.out.println("Starting training...");
			double targetError = 0.0001;
			double input;
			for (int i = 0; i < 500000; i++)
			{
				input = Math.random()*Math.PI*2; 			// input = random values from [0-4PI]
				net.trainInput(input/(Math.PI*2)); 			// normalize input to [0-1]
				net.trainOutput((Math.sin(input)+1)/2.0); 	// expected result = sine function normalized to [0-1]
				
				//You can also use this format for training input:
				//net.trainAnnOnCase(new Double[]{1.0,1.0,0.0}, new Double[]{1.0,0.0});
				
				/*
				if(Ann.lastRunError*100 < targetError)
				{
					// Using this kind of break point is not always a good idea because 
					// the ann can randomly get low error on one specific input, but it may not have converged yet
					// A gliding average can be used to mitigate this somewhat.
					System.out.println("Error <"+targetError+"% after "+i+" runs.\n");
					break;
				}*/
			}

			//print result (plot with: http://www.alcula.com/calculators/statistics/scatter-plot/)
			for(double i=0;i<Math.PI*2;i+=0.1)
			{
				System.out.printf("%.2f,",i);
				net.runMinAnn(2, i/(Math.PI*2));
				System.out.println();
			}	
			
			System.out.println("Last run error: "+Ann.lastRunError*100+"%");
		}
		catch (InputOutputSizeException e)
		{
			e.printStackTrace();
		}
	}
}
