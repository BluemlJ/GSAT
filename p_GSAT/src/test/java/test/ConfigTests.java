package test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import exceptions.ConfigNotFoundException;
import exceptions.ConfigReadException;
import io.Config;

public class ConfigTests {

	@Test
	public void testConfigRead() throws IOException, ConfigReadException, ConfigNotFoundException {
		Config.setPath(getClass().getResource("/lh_config.ini").getFile());
		Config.readConfig();
		assertEquals(Config.researcher, "lovis heindrich");
	}

	@Test
	public void testCorruptConfig() throws IOException, ConfigNotFoundException {
		Config.setPath(getClass().getResource("/corrupt_config.ini").getFile());
		try {
			Config.readConfig();
		} catch (ConfigReadException e) {
			assertEquals(e.getMessage(), "Error while reading researcher from config");
		}
	}
	
	@Test
	public void wrongConfigPath() throws IOException, ConfigReadException{
		Config.setPath("/corrupt_path/corrupt_config.ini");
		try {
			Config.readConfig();
		} catch (ConfigNotFoundException e) {
			assertEquals(e.getMessage(), "Config at path: /corrupt_path/corrupt_config.ini could not be found");
		}
	}
}
