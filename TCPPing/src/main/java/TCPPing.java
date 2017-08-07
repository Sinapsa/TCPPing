import java.util.List;

import org.apache.commons.cli.*;

public class TCPPing {

	public static void main(String[] args) throws Exception {

		int port = 0;
		String hostname = "0.0.0.0";
		String bind = "0.0.0.0";
		int mps = 1;
		int size = 300; 

		// Reading command line parameters
		CLParser commandLineParser = new CLParser(args);
		CommandLine parsedResults = commandLineParser.parse();
		try {
			port = Integer.parseInt(parsedResults.getOptionValue("port"));
		} catch (NumberFormatException e) {
			System.err.println("Parsing failed. Reason: invalid port number");
			System.exit(1);
		}

		if (parsedResults.hasOption("c")) {
			if (parsedResults.hasOption("bind"))
				bind = parsedResults.getOptionValue("bind");
			else {
				System.err.println("Parsing failed.  Reason: bind address missing");
				System.exit(1);
			}

			// Catcher catcher = new Catcher(port, bind, calculateNTPOffset());
			// Since, for the test purposes, both Pitcher and Catcher will run on the same
			// computer, there is no need for time synchronization
			Catcher catcher = new Catcher(port, bind, 0);
			catcher.run();
		}
		
		if (parsedResults.hasOption("p")) {
			List<String> standAloneParameters = parsedResults.getArgList();
			if (standAloneParameters.size() == 0) {
				System.err.println("Parsing failed.  Reason: hostname missing");
				System.exit(1);
			}

			hostname = standAloneParameters.get(0);
			try {
				if (parsedResults.hasOption("mps")) {
					mps = Integer.parseInt(parsedResults.getOptionValue("mps"));
					if(mps < 1) {
						System.err.println("Parsing failed. Reason: invalid mps. mps > 1");
						System.exit(1);
					}
				}
				if (parsedResults.hasOption("size")) {
					size = Integer.parseInt(parsedResults.getOptionValue("size"));
					if(size < 50 || size > 3000) {
						System.err.println("Parsing failed. Reason: invalid size. size = [50,3000]");
						System.exit(1);
					}
				}
			} catch (NumberFormatException e) {
				System.err.println("Parsing failed. Reason: invalid size or mps");
				System.exit(1);
			}

			// Pitcher pitcher = new Pitcher(port, hostname, mps,size,calculateNTPOffset());
			// Since, for the test purposes, both Pitcher and Catcher will run on the same
			// computer, there is no need for time synchronization
			Pitcher pitcher = new Pitcher(port, hostname, mps, size, 0);
			pitcher.run();
		}

	}

	/**
	 * This method calculates NTP time offset. Offset stands for difference in
	 * computer time between host computer and NTP server.
	 * 
	 * @return offset time
	 */
	private static long calculateNTPOffset() {

		long totalOffset = 0;
		for (int i = 0; i < 10; i++) {
			totalOffset += PublicServerTime.getNTPOffset();
		}
		return (totalOffset / 10);

	}

}
