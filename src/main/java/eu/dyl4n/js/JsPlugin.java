package eu.dyl4n.js;

import com.google.common.base.Throwables;
import eu.dyl4n.js.command.CommandJs;
import eu.dyl4n.js.script.ScriptLoader;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import javax.script.ScriptEngine;
import java.util.Calendar;

public class JsPlugin extends JavaPlugin
{

	@Getter private static final ScriptEngine engine = new NashornScriptEngineFactory().getScriptEngine(new String[] {
			"-scripting"
	}, JsPlugin.class.getClassLoader());

	private static JsPlugin instance;

	@Getter private final ScriptLoader loader = new ScriptLoader();

	public static JsPlugin get()
	{
		return JsPlugin.instance;
	}

	public static ScriptEngine getEngine()
	{
		return engine;
	}

	@SuppressWarnings("unused") public static void time()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());

		int mYear = calendar.get(Calendar.YEAR);
		int mMonth = calendar.get(Calendar.MONTH);
		int mDay = calendar.get(Calendar.DAY_OF_MONTH);
	}

	@Override public void onEnable()
	{
		JsPlugin.instance = this;
		this.getDataFolder().mkdir();
		this.getDataFolder().mkdirs();
		this.loader.run();

		getCommand("js").setExecutor(new CommandJs());
	}

	@Override public void onDisable()
	{
		try
		{
			this.loader.close();
		}
		catch (Exception exception)
		{
			throw Throwables.propagate(exception);
		}
	}
}
