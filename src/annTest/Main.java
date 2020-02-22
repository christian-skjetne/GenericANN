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
			long startTime = System.currentTimeMillis();
			double input;
			int runs = 10000000;
			for (int i = 0; i < runs; i++)
			{
				input = Math.random()*Math.PI*2; 			// input = random values from [0-4PI]
				net.trainInput(input/(Math.PI*2)); 			// normalize input to [0-1]
				net.trainOutput((Math.sin(input)+1)/2.0); 	// expected result = sine function normalized to [0-1]
				
				if(i % (runs/25) == 0)
					System.out.printf("   %.1f %%\r",(i/(float)runs)*100);
			}
			float trainTime = (float)(System.currentTimeMillis() - startTime)/1000.f;
			System.out.printf("Training done! Time elapsed: %.2f secs\n",trainTime);
			System.out.printf("Last run error: %.4f %%\n",Ann.lastRunError*100);
			

			//print result (plot with: http://www.alcula.com/calculators/statistics/scatter-plot/)
			for(double i=0;i<Math.PI*2;i+=0.1)
			{
				System.out.printf("\n%.2f,",i);
				net.runMinAnn(2, i/(Math.PI*2));
			}	
			
			
		}
		catch (InputOutputSizeException e)
		{
			e.printStackTrace();
		}

	}
}
