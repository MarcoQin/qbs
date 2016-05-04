import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class Settings {
	private File propertiesFile;
	private Properties settings;
	private static Settings settingInstance = null;
	
	private Settings() {
		String userDir = System.getProperty("user.home");
		File propertiesDir = new File(userDir, ".qbs");
		if (!propertiesDir.exists()) propertiesDir.mkdir();
		propertiesFile = new File(propertiesDir, "qbs.properties");
		Properties defaultSettings = new Properties();
		defaultSettings.put("frameRate", "15.0");
		defaultSettings.put("url", "");
		defaultSettings.put("outputFile", "");
		defaultSettings.put("outputWidth", "1280");
		defaultSettings.put("outputHeight", "800");
		settings = new Properties(defaultSettings);
		if (propertiesFile.exists()) try
	      {
	         FileInputStream in = new FileInputStream(propertiesFile);
	         settings.load(in);
	      }
	      catch (IOException ex)
	      {
	         ex.printStackTrace();
	      }
	}
	
	public static Settings getSettingInstance() {
		if (settingInstance == null){
			settingInstance = new Settings();
		}
		return settingInstance;	
	}
	
	public String getProperty(String name) {
		String rt = settings.getProperty(name);
		return rt;
	}
	
	public void setProperty(String name, String value) {
		settings.setProperty(name, value);
	}
	
	public void saveSettings() {
		try
        {
           FileOutputStream out = new FileOutputStream(propertiesFile);
           settings.store(out, "Program Properties");
        }
        catch (IOException ex)
        {
           ex.printStackTrace();
        }
	}
}
