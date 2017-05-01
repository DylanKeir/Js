package eu.dyl4n.js.script;

import eu.dyl4n.js.JsPlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor public class ScriptLoader implements Runnable, AutoCloseable
{

	@Getter public static File directory = null;
	private final Set<Script> scripts = new HashSet<Script>();
	@Getter private final ScriptExports exports = new ScriptExports();
	public JsPlugin plugin = JsPlugin.get();

	public void run()
	{
		this.plugin = JsPlugin.get();
		if (!this.plugin.getDataFolder().exists())
		{
			this.plugin.getDataFolder().mkdir();
		}
		directory = this.plugin.getDataFolder();
		JsPlugin.get().getLogger().info(directory.getAbsolutePath());

		JsPlugin.get().getLogger().info("Loading scripts...");

		long startTime = System.currentTimeMillis();

		File loadScript = new File(directory, "init.js");
		if (!loadScript.exists())
		{
			JsPlugin.get().getLogger().info("init.js does not exist, scripts will not load.");
		}
		else if (!loadScript.isFile())
		{
			JsPlugin.get().getLogger().info("init.js is not a file, scripts will not load");
		}
		else
		{
			try
			{
				this.load(new Script(this, loadScript));
			}
			catch (Throwable throwable)
			{
				throwable.printStackTrace();
			}
		}

		JsPlugin.get().getLogger().info("Loaded scripts in " + (System.currentTimeMillis() - startTime) + "ms.");
	}

	public void close() throws Exception
	{
		JsPlugin.get().getLogger().info("Unloading scripts...");

		long startTime = System.currentTimeMillis();
		for (Script script : this.scripts)
		{
			try
			{
				this.unload(script);
			}
			catch (Throwable throwable)
			{
				throwable.printStackTrace();
			}
		}
		this.scripts.clear();

		JsPlugin.get().getLogger().info("Unloaded scripts in " + (System.currentTimeMillis() - startTime) + "ms.");
	}

	public void load(String[] paths)
	{
		for (String path : paths)
		{
			this.load(path);
		}
	}

	public void load(String path)
	{
		try
		{
			this.load(new Script(this, new File(directory, path)));
		}
		catch (Throwable throwable)
		{
			throwable.printStackTrace();
		}
	}

	private void load(Script script) throws Exception
	{
		JsPlugin.get().getLogger().info("Loading script " + script.getFile().getName() + ".");

		this.scripts.add(script);

		script.run();
	}

	private void unload(Script script) throws Exception
	{
		JsPlugin.get().getLogger().info("Unloading script " + script.getFile().getName() + "...");
		script.close();
	}

	public File getDirectory()
	{
		return directory;
	}

	public ScriptExports getExports()
	{
		return exports;
	}

}