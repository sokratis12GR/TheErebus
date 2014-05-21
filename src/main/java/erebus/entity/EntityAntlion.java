package erebus.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import erebus.ModItems;
import erebus.entity.ai.EntityErebusAIAttackOnCollide;
import erebus.item.ItemErebusMaterial.DATA;

public class EntityAntlion extends EntityMob {

	public EntityAntlion(World world) {
		super(world);
		setSize(2.0F, 0.9F);
		isImmuneToFire = true;
		experienceValue = 17;
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityErebusAIAttackOnCollide(this, EntityLivingBase.class, 0.7D, false));
		tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		tasks.addTask(3, new EntityAIWander(this, 0.7D));
		targetTasks.addTask(0, new EntityAIHurtByTarget(this, false));
		targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
		targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityFireAnt.class, 0, true));
		targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityFireAntSoldier.class, 0, true));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.7D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(35.0D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(1.0D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(16.0D);
		getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setAttribute(0.5D);
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	public int getTotalArmorValue() {
		return 8;
	}

	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.ARTHROPOD;
	}

	/*
	 * @Override protected String getLivingSound() { return
	 * "erebus:AntlionSound"; }
	 * 
	 * @Override protected String getHurtSound() { return "erebus:Antlionhurt";
	 * }
	 */
	
	@Override
	protected String getDeathSound() {
		return "erebus:squish";
	}

	@Override
	protected void playStepSound(int x, int y, int z, int blockID) {
		playSound("mob.spider.step", 0.15F, 1.0F);
	}

	@Override
	protected int getDropItemId() {
		return Block.sand.blockID;
	}

	@Override
	protected void dropFewItems(boolean recentlyHit, int looting) {
		int chance = rand.nextInt(4) + rand.nextInt(1 + looting);
		int amount;
		for (amount= 0; amount < chance; ++amount)
			entityDropItem(new ItemStack(ModItems.erebusMaterials, 1, DATA.plateExo.ordinal()), 0.0F);
	}

	@Override
	public boolean getCanSpawnHere() {
		return isOnSand() && super.getCanSpawnHere();
	}

	public boolean isOnSand() {
		return worldObj.getBlockId(MathHelper.floor_double(posX), MathHelper.floor_double(boundingBox.minY) - 1, MathHelper.floor_double(posZ)) == Block.sand.blockID;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (findPlayerToAttack() != null)
			entityToAttack = findPlayerToAttack();
		else
			entityToAttack = null;

		if (!worldObj.isRemote && getEntityToAttack() == null && isOnSand())
			yOffset = -1;
		else
			yOffset = 0;
	}

	@Override
	protected Entity findPlayerToAttack() {
		EntityPlayer player = worldObj.getClosestVulnerablePlayerToEntity(this, 16.0D);
		return player;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source.equals(DamageSource.inWall) || source.equals(DamageSource.drown))
			return false;
		return super.attackEntityFrom(source, damage);
	}
}