
package net.minecraft.tileentity;


import com.dungeonderps.resourcefulbees.config.BeeInfo;
import com.dungeonderps.resourcefulbees.config.Config;
import com.dungeonderps.resourcefulbees.registry.RegistryHandler;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CustomBeeEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Stack;


public class IronBeehiveBlockEntity extends BeehiveTileEntity {

  public Stack<String> honeycombs = new Stack<>();
  public boolean isSmoked = false;
  public int ticksSmoked = 0;

  @Nonnull
  @Override
  public TileEntityType<?> getType() {
    return RegistryHandler.IRON_BEEHIVE_ENTITY.get();
  }

  @Override
  public boolean releaseBee(BlockState state, CompoundNBT nbt, @Nullable List<Entity> entities, BeehiveTileEntity.State beehiveState) {
    BlockPos blockpos = this.getPos();
    if (shouldStayInHive(state,beehiveState)) {
      return false;
    } else {
      nbt.remove("Passengers");
      nbt.remove("Leash");
      nbt.removeUniqueId("UUID");
      Direction direction = state.get(BeehiveBlock.FACING);
      BlockPos blockpos1 = blockpos.offset(direction);
      if (!this.world.getBlockState(blockpos1).getCollisionShape(this.world, blockpos1).isEmpty()) {
        return false;
      } else {
        Entity entity = EntityType.func_220335_a(nbt, this.world, entity1 -> entity1);
        if (entity != null) {
          float f = entity.getWidth();
          double d0 = 0.55D + f / 2.0F;
          double d1 = blockpos.getX() + 0.5D + d0 * direction.getXOffset();
          double d2 = blockpos.getY() + 0.5D -  (entity.getHeight() / 2.0F);
          double d3 = blockpos.getZ() + 0.5D + d0 * direction.getZOffset();
          entity.setLocationAndAngles(d1, d2, d3, entity.rotationYaw, entity.rotationPitch);
          if (entity instanceof CustomBeeEntity) {
            CustomBeeEntity beeEntity = (CustomBeeEntity) entity;
            if (this.hasFlowerPos() && !beeEntity.hasFlower() && this.world.rand.nextFloat() < 0.9F) {
              beeEntity.setFlowerPos(this.flowerPos);
            }

            if (beehiveState == State.HONEY_DELIVERED) {
              beeEntity.onHoneyDelivered();
              int i = getHoneyLevel(state);
              if (i < 5) {
                float percentValue = (float)(this.honeycombs.size() / Config.HIVE_MAX_COMBS.get());
                int newState = (int)(percentValue  - (percentValue % 20)) * 100 / 20;
                this.honeycombs.push(beeEntity.getBeeType());
                this.world.setBlockState(this.getPos(), state.with(BeehiveBlock.HONEY_LEVEL, newState));
              }
            }
            beeEntity.resetTicksWithoutNectar();
            if (entities != null) {
              entities.add(beeEntity);
            }
          }
          BlockPos hivePos = this.getPos();
          this.world.playSound(null, hivePos.getX(), hivePos.getY(),  hivePos.getZ(), SoundEvents.BLOCK_BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
          return this.world.addEntity(entity);
        } else {
          return false;
        }
      }
    }
  }

  public void tryEnterHive(Entity bee, boolean hasNectar, int ticksInHive) {
    if (this.bees.size() < 3) {
      bee.removePassengers();
      CompoundNBT nbt = new CompoundNBT();
      bee.writeUnlessPassenger(nbt);
      this.bees.add(new BeehiveTileEntity.Bee(nbt, ticksInHive, hasNectar ? 100 : 600));// TODO *** <---- Change 100 back to 2400!!!!
      if (this.world != null) {
        if (bee instanceof CustomBeeEntity) {
          CustomBeeEntity bee1 = (CustomBeeEntity)bee;
          if (bee1.hasFlower() && (!this.hasFlowerPos() || this.world.rand.nextBoolean())) {
            this.flowerPos = bee1.getFlowerPos();
          }
          if (BeeInfo.BEE_INFO.get(bee1.getBeeType()).isEnderBee()){
            this.world.addParticle(ParticleTypes.PORTAL, bee.getPosXRandom(0.5D),
                    bee.getPosYRandom() - 0.25D, bee.getPosZRandom(0.5D),
                    (world.rand.nextDouble() - 0.5D) * 2.0D, -world.rand.nextDouble(),
                    (world.rand.nextDouble() - 0.5D) * 2.0D);
          }
        }
        BlockPos lvt_5_2_ = this.getPos();
        this.world.playSound(null, lvt_5_2_.getX(), lvt_5_2_.getY(), lvt_5_2_.getZ(), SoundEvents.BLOCK_BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0F, 1.0F);
      }

      bee.remove();
    }
  }

  @Override
  public boolean isSmoked() {
	  return isSmoked || CampfireBlock.isLitCampfireInRange(this.world, this.getPos(), 5);
  }
  
  @Override
  public void tick() {
    if (!this.world.isRemote && isSmoked && ticksSmoked < 600) {
      ticksSmoked++;
    }
    if (ticksSmoked == 600) {
      isSmoked = false;
      ticksSmoked = 0;
    }
    super.tick();
  }
  
  public boolean shouldStayInHive(BlockState state, State beehiveState){
    return (this.world.isNightTime() || this.world.isRaining()) && beehiveState != BeehiveTileEntity.State.EMERGENCY;
  }

  @Override
  public boolean isFullOfBees() {
    return bees.size() > 3;
  }

  public String getResourceHoneyComb(){
    String honeycomb = honeycombs.pop();
    return honeycomb;
  }

  public boolean hasCombs(){
    return honeycombs.size() > 0;
  }

  public boolean isAllowedBee(CustomBeeEntity bee){
    Block hive = getBlockState().getBlock();
    return hive == RegistryHandler.IRON_BEEHIVE.get();
  }

  @Override
  public void read(CompoundNBT nbt) {
    super.read(nbt);
    if (nbt.contains("Honeycombs")){
      CompoundNBT combs = (CompoundNBT) nbt.get("Honeycombs");
      int i = 0;
      while (combs.contains(String.valueOf(i))){
        honeycombs.push(combs.getString(String.valueOf(i)));
        i++;
      }
    }
  }

  @Nonnull
  @Override
  public CompoundNBT write(CompoundNBT nbt) {
    super.write(nbt);
    if (!honeycombs.isEmpty()){
      CompoundNBT combs = new CompoundNBT();
      for (int i = 0; i < honeycombs.size();i++){
        combs.putString(String.valueOf(i), honeycombs.elementAt(i));
      }
      nbt.put("Honeycombs",combs);
    }

    return nbt;
  }

  public static class Bee2 extends Bee {
    public Bee2(CompoundNBT data, int ticksIn, int minTicks) {
      super(data, ticksIn, minTicks);
    }
  }

}
