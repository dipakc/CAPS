package progsynth.utils

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Class to handle setting/removing MDC on per test case basis. This helps
 * us log each test case into it's own log file. Please see
 * {@link http://logback.qos.ch/manual/appenders.html#SiftingAppender}
 * and {@link http://logback.qos.ch/manual/mdc.html}
 */
object TestLogHelper
{
  val log = LoggerFactory.getLogger("TestLogHelper");

  val TEST_NAME = "testname";

  /**
   * Adds the test name to MDC so that sift appender can use it and log the new
   * log events to a different file
   * @param name name of the new log file
   * @throws Exception
   */
  def startTestLogging(name: String) = MDC.put(TEST_NAME, name);

  /**
   * Removes the key (log file name) from MDC
   * @return name of the log file, if one existed in MDC
   */
  def stopTestLogging() = {
    val name = MDC.get(TEST_NAME);
    MDC.remove(TEST_NAME);
    name;
  }

    def setupSiftLogger(testName: String, logger: Logger)(body: => Unit): Unit = {

    	TestLogHelper.startTestLogging(testName)
    	logger.trace("Test Case: " + testName) //This ensures that a log file is created.
    	body
    	TestLogHelper.stopTestLogging()

    	val logFilePath = "testlogs/" + testName + ".log"
    	val outputFilePath = "testlogs/" + testName + ".html"
    	ProcessServerLog.mainFn(logFilePath, outputFilePath)
    }

}