package eu.dyl4n.js.script;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.io.CharStreams;
import eu.dyl4n.js.JsPlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

import javax.script.*;
import java.io.*;

@RequiredArgsConstructor public class Script implements Runnable, AutoCloseable
{

	private static final String[] DEFAULT_IMPORTS = new String[] {
			"java.io",
			"java.lang",
			"java.net",
			"java.math",
			"java.util",

			"org.bukkit",
			"org.bukkit.block",
			"org.bukkit.configuration",
			"org.bukkit.enchantments",
			"org.bukkit.entity",
			"org.bukkit.event",
			"org.bukkit.event.block",
			"org.bukkit.event.enchantment",
			"org.bukkit.event.entity",
			"org.bukkit.event.hanging",
			"org.bukkit.event.inventory",
			"org.bukkit.event.painting",
			"org.bukkit.event.player",
			"org.bukkit.event.server",
			"org.bukkit.event.vehicle",
			"org.bukkit.event.weather",
			"org.bukkit.event.world",
			"org.bukkit.inventory",
			"org.bukkit.material",
			"org.bukkit.plugin",
			"org.bukkit.potion",
			"org.bukkit.projectiles",
			"org.bukkit.scheduler",
			"org.bukkit.scoreboard",
			"org.bukkit.util"
	};

	static String HEADER = "";

	static
	{
		Script.HEADER += "\"use strict\";\r\n";
		Script.HEADER += "load(\"nashorn:mozilla_compat.js\");\r\n";

		for (String defaultImport : Script.DEFAULT_IMPORTS)
		{
			Script.HEADER += "importPackage(\"" + defaultImport + "\");\r\n";
		}

		Script.HEADER += "var __load = load;\r\n";
		Script.HEADER += "var load = function(file) {\r\n";
		Script.HEADER += "    __load(scriptdir + file);\r\n";
		Script.HEADER += "}\r\n";
	}

	@Getter private ScriptLoader loader = null;
	@Getter private File file = null;
	private ScriptContext context = null;

	public Script(ScriptLoader scriptLoader, File loadScript)
	{
		this.loader = scriptLoader;
		this.file = loadScript;
	}

	public void run()
	{
		Reader reader;
		try
		{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.file), Charsets.UTF_8));
		}
		catch (FileNotFoundException exception)
		{
			throw Throwables.propagate(exception);
		}

		try
		{
			ScriptEngine engine = JsPlugin.getEngine();

			Bindings bindings = engine.createBindings();
			bindings.put("server", Bukkit.getServer());
			bindings.put("bukkit", JsPlugin.get().getServer());
			bindings.put("loader", this.loader);
			bindings.put("scriptdir", this.loader.getDirectory().getAbsolutePath().replace(".", "") + "/");
			bindings.put("exports", this.loader.getExports());
			bindings.put("plugin", JsPlugin.get());

			this.context = new SimpleScriptContext();
			this.context.setBindings(bindings, ScriptContext.ENGINE_SCOPE);

			engine.eval(Joiner.on("").join(Script.HEADER, CharStreams.toString(reader)), this.context);
		}
		catch (ScriptException | IOException exception)
		{
			throw Throwables.propagate(exception);
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch (IOException exception)
			{
				throw Throwables.propagate(exception);
			}
		}
	}

	public void close() throws Exception
	{
	}

	public File getFile()
	{
		return file;
	}
}
 