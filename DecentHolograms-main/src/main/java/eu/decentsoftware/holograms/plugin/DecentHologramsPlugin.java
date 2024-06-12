package eu.decentsoftware.holograms.plugin;

import eu.decentsoftware.holograms.api.DecentHolograms;
import eu.decentsoftware.holograms.api.DecentHologramsAPI;
import eu.decentsoftware.holograms.api.commands.CommandManager;
import eu.decentsoftware.holograms.api.commands.DecentCommand;
import eu.decentsoftware.holograms.plugin.commands.HologramsCommand;
import eu.decentsoftware.holograms.plugin.features.DamageDisplayFeature;
import eu.decentsoftware.holograms.plugin.features.HealingDisplayFeature;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class DecentHologramsPlugin extends JavaPlugin {

	@Override
	public void onLoad() {
		getLogger().info("DecentHologramsPlugin is loading...");
		DecentHologramsAPI.onLoad(this);
	}

	@Override
	public void onEnable() {
		getLogger().info("DecentHologramsPlugin is enabling...");
		DecentHologramsAPI.onEnable();

		if (DecentHologramsAPI.isRunning()) {
			DecentHolograms decentHolograms = DecentHologramsAPI.get();
			getLogger().info("Registering features...");
			decentHolograms.getFeatureManager().registerFeature(new DamageDisplayFeature());
			decentHolograms.getFeatureManager().registerFeature(new HealingDisplayFeature());

			CommandManager commandManager = decentHolograms.getCommandManager();
			DecentCommand mainCommand = new HologramsCommand();
			getLogger().info("Registering commands...");
			commandManager.setMainCommand(mainCommand);
			commandManager.registerCommand(mainCommand);
		} else {
			getLogger().severe("DecentHologramsAPI is not running!");
		}
	}

	@Override
	public void onDisable() {
		getLogger().info("DecentHologramsPlugin is disabling...");
		DecentHologramsAPI.onDisable();
	}

}
