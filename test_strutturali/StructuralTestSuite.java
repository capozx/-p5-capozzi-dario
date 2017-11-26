

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AnalyzerTest.class, DatabaseTest.class, FileManagerTest.class,
		ModelTest.class, OutputTest.class, ParserTest.class, 
		ResultContainerTest.class, ResultComparatorTest.class })
public class StructuralTestSuite {
}
