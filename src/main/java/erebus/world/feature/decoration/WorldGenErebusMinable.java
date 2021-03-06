package erebus.world.feature.decoration;

import java.util.Random;

import erebus.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenErebusMinable extends WorldGenerator {
	private Block minableBlock;
	private int minableBlockMeta = 0;
	private int numberOfBlocks;
	private Block blockToReplace;

	public void prepare(Block block, int meta, int numberOfBlocks) {
		minableBlock = block;
		minableBlockMeta = meta;
		this.numberOfBlocks = numberOfBlocks;
		blockToReplace = ModBlocks.UMBERSTONE;
	}

	public void prepare(Block block, int meta, int numberOfBlocks, Block blockToReplace) {
		minableBlock = block;
		minableBlockMeta = meta;
		this.numberOfBlocks = numberOfBlocks;
		this.blockToReplace = blockToReplace;
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos position) {
		float f = rand.nextFloat() * (float) Math.PI;
		double d0 = position.getX() + 8 + MathHelper.sin(f) * numberOfBlocks * 0.125F;
		double d1 = position.getX() + 8 - MathHelper.sin(f) * numberOfBlocks * 0.125F;
		double d2 = position.getZ() + 8 + MathHelper.cos(f) * numberOfBlocks * 0.125F;
		double d3 = position.getZ() + 8 - MathHelper.cos(f) * numberOfBlocks * 0.125F;
		double d4 = position.getY() + rand.nextInt(3) - 2;
		double d5 = position.getY() + rand.nextInt(3) - 2;

		int realNumberOfBlocks = numberOfBlocks;
		numberOfBlocks = (int) Math.ceil(numberOfBlocks * (1.15F + rand.nextFloat() * 0.25F));

		for (int attempt = 0, placed = 0; attempt <= numberOfBlocks && placed <= realNumberOfBlocks; ++attempt) {
			double centerX = d0 + (d1 - d0) * placed / numberOfBlocks;
			double centerY = d4 + (d5 - d4) * placed / numberOfBlocks;
			double centerZ = d2 + (d3 - d2) * placed / numberOfBlocks;
			double spreadFactor = rand.nextDouble() * numberOfBlocks * 0.0625D;
			double maxDistXZ = (MathHelper.sin(placed * (float) Math.PI / numberOfBlocks) + 1F) * spreadFactor + 1D;
			double maxDistY = (MathHelper.sin(placed * (float) Math.PI / numberOfBlocks) + 1F) * spreadFactor + 1D;
			int minX = MathHelper.floor(centerX - maxDistXZ * 0.5D);
			int minY = MathHelper.floor(centerY - maxDistY * 0.5D);
			int minZ = MathHelper.floor(centerZ - maxDistXZ * 0.5D);
			int maxX = MathHelper.floor(centerX + maxDistXZ * 0.5D);
			int maxY = MathHelper.floor(centerY + maxDistY * 0.5D);
			int maxZ = MathHelper.floor(centerZ + maxDistXZ * 0.5D);

			for (int xx = minX; xx <= maxX; ++xx) {
				double d12 = (xx + 0.5D - centerX) / (maxDistXZ * 0.5D);

				if (d12 * d12 < 1.0D)
					for (int yy = minY; yy <= maxY; ++yy) {
						double d13 = (yy + 0.5D - centerY) / (maxDistY * 0.5D);

						if (d12 * d12 + d13 * d13 < 1.0D)
							for (int zz = minZ; zz <= maxZ; ++zz) {
								double d14 = (zz + 0.5D - centerZ) / (maxDistXZ * 0.5D);

								if (d12 * d12 + d13 * d13 + d14 * d14 >= 1D)
									continue;

								IBlockState block = world.getBlockState(new BlockPos(xx, yy, zz));
								if (block != null && block.getBlock().isReplaceableOreGen(block, world, new BlockPos(xx, yy, zz), BlockMatcher.forBlock(blockToReplace))) {
									world.setBlockState(new BlockPos(xx, yy, zz), minableBlock.getStateFromMeta(minableBlockMeta), 2);
									++placed;
								}
							}
					}
			}
		}

		return true;
	}
}