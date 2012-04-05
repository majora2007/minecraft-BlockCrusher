package Dingmatt.BlockCrusher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

public class BlockCrusherConfig {
	public static FileConfiguration config;
	public static HashMap<String, FileConfiguration> locale = new HashMap<String, FileConfiguration>();
	public static File DATA_FOLDER;

	public static File createDefaultFiles(File file) {
		if (!file.exists()) {
			new File(file.getParent()).mkdirs();
			InputStream in = BlockCrusher.class.getResourceAsStream(file.getName());
			if (in != null) {
				FileOutputStream out = null;
				try {
					out = new FileOutputStream(file);
					byte[] buffer = new byte[4096 * 2];
					int length = 0;
					while ((length = in.read(buffer)) > 0) {
						out.write(buffer, 0, length);
					}
					BlockCrusher.logAdd(file.getName() + " created.");
				} catch(IOException e) {
					e.printStackTrace();
					BlockCrusher.logAdd("Error creating default file " + file.getName());
				} finally {
					try {
						if (in != null) {
							in.close();
						}
					} catch(IOException e) {
						e.printStackTrace();
					}
					try {
						if (out != null) {
							out.close();
						}
					} catch(IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		BlockCrusher.logAdd(file.getName() + " loaded.");
		return file;
	}

	public static boolean load() {
		File file = createDefaultFiles(new File(DATA_FOLDER, "config.yml"));
		if (file.exists()) {
			config = BlockCrusher.config;
			try {
				config.load(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//config.configuration(file);
			//config.load();
			return true;
		} else {
			BlockCrusher.logAdd("Error loading configuration.");
			return false;
		}
	}

}
