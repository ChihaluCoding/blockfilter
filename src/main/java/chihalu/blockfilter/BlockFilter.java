package chihalu.blockfilter;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockFilter implements ModInitializer {
	public static final String MOD_ID = "blockfilter";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		BlockFilterItemGroups.bootstrap();
		LOGGER.info("BlockFilter ready: creative tabs registered");
	}
}
