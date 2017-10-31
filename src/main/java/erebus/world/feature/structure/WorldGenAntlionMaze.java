package erebus.world.feature.structure;

import java.util.Random;

import erebus.core.handler.configs.ConfigHandler;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGenAntlionMaze implements IWorldGenerator {

	@Override
	 public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		if (world.provider.getDimension() == ConfigHandler.INSTANCE.erebusDimensionID)
			generate(world, random, chunkX * 16, chunkZ * 16);
	}

	private void generate(World world, Random random, int x, int z) {
		AntlionMazeDungeon maze = new AntlionMazeDungeon();
		int chunkX = x;
		int chunkZ = z;
		int chunkY = 18;
		if (random.nextInt(ConfigHandler.INSTANCE.antlionMazeFrequency) == 0)
			maze.generateSurface(world, random, chunkX, chunkY, chunkZ);
	}
}
