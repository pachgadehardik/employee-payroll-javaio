package hardik.java_fileIO;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;

import org.junit.Test;

import com.capg.javaio.JavaWatchService;

public class WatchServiceTest {

	private static String HOME = "H:\\Capgemini\\Capg_Training";
	private static String PLAY_WITH_NIO = "TempPlay";

	@Test
	public void createTempDirectory() throws IOException {
		Path playPath = Paths.get(HOME + "/" + PLAY_WITH_NIO);

		// Creating A directory

		Files.createDirectory(playPath);
		assert (Files.exists(playPath));

		// Create Files in temPdirectory
		IntStream.range(1, 10).forEach(cntr -> {
			Path tempFile = Paths.get(HOME + "/" + PLAY_WITH_NIO + "/temp" + cntr);
			assertTrue(Files.notExists(tempFile));
			try {
				Files.createFile(tempFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			assertTrue(Files.exists(tempFile));

		});
	}
	
	@Test
	public void givenADirectoryWhenWatchedListsAllTheActivities() throws IOException {
		Path dir = Paths.get(HOME + "/" + PLAY_WITH_NIO);
		Files.list(dir).filter(Files::isRegularFile).forEach(System.out::println);
		new JavaWatchService(dir).processEvents();
	}
}
