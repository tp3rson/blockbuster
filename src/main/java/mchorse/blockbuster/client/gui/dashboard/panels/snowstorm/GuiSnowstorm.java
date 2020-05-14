package mchorse.blockbuster.client.gui.dashboard.panels.snowstorm;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.client.gui.dashboard.GuiBlockbusterPanel;
import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections.GuiSnowstormGeneralSection;
import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections.GuiSnowstormInitializationSection;
import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections.GuiSnowstormSection;
import mchorse.blockbuster.client.gui.dashboard.panels.snowstorm.sections.GuiSnowstormSpaceSection;
import mchorse.blockbuster.client.particles.BedrockScheme;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringSearchListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDrawable;
import mchorse.mclib.client.gui.mclib.GuiDashboard;
import mchorse.mclib.client.gui.utils.Icons;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class GuiSnowstorm extends GuiBlockbusterPanel
{
	public GuiSnowstormRenderer renderer;
	public GuiScrollElement editor;
	public GuiStringSearchListElement particles;
	public GuiIconElement save;

	public List<GuiSnowstormSection> sections = new ArrayList<GuiSnowstormSection>();

	private String filename;
	private BedrockScheme scheme;

	public GuiSnowstorm(Minecraft mc, GuiDashboard dashboard)
	{
		super(mc, dashboard);

		this.renderer = new GuiSnowstormRenderer(mc);
		this.renderer.flex().relative(this).wh(1F, 1F);

		this.particles = new GuiStringSearchListElement(mc, (list) -> this.setScheme(list.get(0)));
		this.particles.list.background();
		this.particles.flex().relative(this).x(1F).wh(140, 200).anchorX(1F);

		this.editor = new GuiScrollElement(mc);
		this.editor.flex().relative(this).w(200).h(1F).column(20).vertical().stretch().scroll().padding(10);

		this.save = new GuiIconElement(mc, Icons.SAVE, (b) -> Blockbuster.proxy.particles.save(this.filename, this.scheme));
		this.save.flex().relative(this.particles).x(-20).wh(20, 20);

		this.addSection(new GuiSnowstormGeneralSection(mc));
		this.addSection(new GuiSnowstormSpaceSection(mc));
		this.addSection(new GuiSnowstormInitializationSection(mc));

		/* TODO: Add link to snowstorm web editor */

		this.add(this.renderer, new GuiDrawable(this::drawOverlay), this.editor, this.particles, this.save);
	}

	private void addSection(GuiSnowstormSection section)
	{
		this.sections.add(section);
		this.editor.add(section);
	}

	private void setScheme(String scheme)
	{
		this.filename = scheme;
		this.scheme = Blockbuster.proxy.particles.presets.get(scheme);
		this.renderer.setScheme(this.scheme);

		for (GuiSnowstormSection section : this.sections)
		{
			section.setScheme(this.scheme);
		}
	}

	@Override
	public void appear()
	{
		super.appear();

		String current = this.particles.list.getCurrentFirst();

		this.particles.filter("", true);

		this.particles.list.clear();
		this.particles.list.add(Blockbuster.proxy.particles.presets.keySet());
		this.particles.list.sort();

		if (this.scheme == null)
		{
			this.setScheme("default_snow");
			this.particles.list.setCurrent("default_snow");
		}
		else
		{
			this.particles.list.setCurrent(current);
		}
	}

	@Override
	public void close()
	{
		/* TODO: Clean up particles */
	}

	private void drawOverlay(GuiContext context)
	{
		this.editor.area.draw(0x88000000);
	}
}