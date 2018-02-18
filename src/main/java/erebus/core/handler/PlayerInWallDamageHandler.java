package erebus.core.handler;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerInWallDamageHandler {
	@SubscribeEvent
	public void onEntityMounted(LivingAttackEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer) {
		/*	if (event.entityLiving.ridingEntity != null && event.entityLiving.ridingEntity instanceof EntityArmchairMount)
				if(event.entityLiving.ridingEntity.worldObj.isAirBlock((int) (event.entityLiving.ridingEntity.posX - 0.5D), (int)(event.entityLiving.ridingEntity.posY + 1D), (int)(event.entityLiving.ridingEntity.posZ - 0.5D)))
					if(event.entityLiving.ridingEntity.worldObj.isAirBlock((int) (event.entityLiving.ridingEntity.posX - 0.5D), (int)(event.entityLiving.ridingEntity.posY + 2D), (int)(event.entityLiving.ridingEntity.posZ - 0.5D)))
						if (event.source == DamageSource.inWall)
							event.setCanceled(true);
		*/
		
		if (event.getEntityLiving().getRidingEntity() != null && event.getEntityLiving().getRidingEntity() instanceof EntityLivingBase)
			if (event.getSource() == DamageSource.IN_WALL)
				event.setCanceled(true);
		}
	}
}