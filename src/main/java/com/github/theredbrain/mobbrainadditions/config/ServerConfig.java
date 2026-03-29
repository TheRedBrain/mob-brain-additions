package com.github.theredbrain.mobbrainadditions.config;

import com.github.theredbrain.mobbrainadditions.MobBrainAdditions;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;

public class ServerConfig extends Config {

	public ServerConfig() {
		super(MobBrainAdditions.identifier("server"));
	}

	public ValidatedBoolean enable_path_finding_node_block_debug_mode = new ValidatedBoolean(false);

}
