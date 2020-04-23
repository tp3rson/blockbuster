package mchorse.blockbuster_pack;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.ClientProxy;
import mchorse.blockbuster.api.Model;
import mchorse.blockbuster.api.ModelPack;
import mchorse.blockbuster.network.Dispatcher;
import mchorse.blockbuster.network.common.structure.PacketStructureListRequest;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.blockbuster_pack.morphs.ImageMorph;
import mchorse.blockbuster_pack.morphs.ParticleMorph;
import mchorse.blockbuster_pack.morphs.RecordMorph;
import mchorse.blockbuster_pack.morphs.SequencerMorph;
import mchorse.mclib.utils.resources.RLUtils;
import mchorse.metamorph.api.creative.categories.MorphCategory;
import mchorse.metamorph.api.creative.sections.MorphSection;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockbusterSection extends MorphSection
{
	public MorphCategory extra;
	public MorphCategory structures;
	public Map<String, MorphCategory> models = new HashMap<String, MorphCategory>();

	public BlockbusterSection(String title)
	{
		super(title);

		this.extra = new MorphCategory(this, "blockbuster_extra");
		this.structures = new MorphCategory(this, "blockbuster_structures");

		/* Adding some default morphs which don't need to get reloaded */
		ImageMorph image = new ImageMorph();

		image.texture = RLUtils.create("blockbuster", "textures/gui/icon.png");

		this.extra.add(image);
		this.extra.add(new ParticleMorph());
		this.extra.add(new SequencerMorph());
		this.extra.add(new RecordMorph());
	}

	public void add(String key, Model model)
	{
		String path = this.getCategoryId(key);
		CustomMorph morph = new CustomMorph();

		morph.name = "blockbuster." + key;
		morph.model = model;

		MorphCategory category = this.models.get(path);

		if (category == null)
		{
			category = new BlockbusterCategory(this, "blockbuster_models", path);
			this.models.put(path, category);
			this.categories.add(category);
		}

		category.add(morph);
	}

	public void remove(String key)
	{
		String path = this.getCategoryId(key);
		String name = "blockbuster." + key;
		MorphCategory category = this.models.get(path);
		List<AbstractMorph> morphs = new ArrayList<AbstractMorph>();

		for (AbstractMorph m : category.getMorphs())
		{
			if (m.name.equals(name))
			{
				morphs.add(m);
			}
		}

		for (AbstractMorph morph : morphs)
		{
			category.remove(morph);
		}
	}

	private String getCategoryId(String key)
	{
		if (key.contains("/"))
		{
			key = FilenameUtils.getPath(key);

			return key.substring(0, key.length() - 1);
		}

		return "";
	}

	@Override
	public void update(World world)
	{
		this.reloadModels();

		this.categories.clear();
		this.add(this.extra);
		this.add(this.structures);

		/* Add models categories */
		for (MorphCategory category : this.models.values())
		{
			this.add(category);
		}
	}

	private void reloadModels()
	{
		/* Reload models and skin */
		ModelPack pack = Blockbuster.proxy.models.pack;

		if (pack == null)
		{
			pack = Blockbuster.proxy.getPack();

			if (Minecraft.getMinecraft().isSingleplayer())
			{
				pack.addFolder(DimensionManager.getCurrentSaveRootDirectory() + "/blockbuster/models");
			}
		}

		ClientProxy.actorPack.pack.reload();
		Blockbuster.proxy.loadModels(pack, false);

		Blockbuster.proxy.particles.reload();
		Dispatcher.sendToServer(new PacketStructureListRequest());
	}

	public static class BlockbusterCategory extends MorphCategory
	{
		public String subtitle = "";

		public BlockbusterCategory(MorphSection parent, String title)
		{
			super(parent, title);
		}

		public BlockbusterCategory(MorphSection parent, String title, String subtitle)
		{
			super(parent, title);

			this.subtitle = subtitle;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public String getTitle()
		{
			if (!this.subtitle.isEmpty())
			{
				return super.getTitle() + " (" + this.subtitle + ")";
			}

			return super.getTitle();
		}
	}
}