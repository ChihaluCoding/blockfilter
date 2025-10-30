package chihalu.blockfilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;

/**
 * Mod initializer that wires up creative tab registrations.
 */
public class BlockFilter implements ModInitializer {
	public static final String MOD_ID = "blockfilter";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		BlockFilterItemGroups.register();
	}
}
