package com.jegg.spacesim.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.jegg.spacesim.core.Game;

public class DesktopLauncher {
	public static void main(String[] args) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Space Sim");
		config.setWindowedMode(800,600);
		config.setWindowSizeLimits(800, 600, 3840, 2160);

		new Lwjgl3Application(new Game(), config);
	}
}
