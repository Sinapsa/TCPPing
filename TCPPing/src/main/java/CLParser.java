import org.apache.commons.cli.*;

public class CLParser {
	
	String[] args;
	Options options;
	
	public CLParser(String[] args) {
		this.args = args;
		options = new Options();
		OptionGroup optionGroup = new OptionGroup(); 
		
		optionGroup.addOption(new Option("p", false, "program acts like a pitcher"));
		optionGroup.addOption(new Option("c", false, "program acts like a cather"));
		options.addOptionGroup(optionGroup);
		options.addOption(
				Option.builder("port")
				.required()
				.hasArg(true).desc("[Pitcher] TCP socket port used for connect   [Catcher] TCP socket port used for listen")
				.build());
		
		
		options.addOption(
				Option.builder("bind")
				.hasArg(true).desc("[Catcher] TCP socket bind address where listen is started")
				.build());
		
		options.addOption(
				Option.builder("mps")
				.hasArg(true).desc("[Pitcher] sending speed expressed in messages per second. Default 1")
				.build());
		
		options.addOption(
				Option.builder("size")
				.hasArg(true).desc("[Pitcher] message lenght (Min: 50, Max: 3000, Default: 300)")
				.build());
		
	}
	

	
	public CommandLine parse() {
		CommandLineParser parser = new DefaultParser();
		CommandLine result = null;
		try {
			result = parser.parse(options, args);
		} catch (Exception e) {
			System.err.println( "Parsing failed.  Reason: " + e.getMessage() );
			System.exit(1);
		}
		
		return result;
	}
	
	

}
