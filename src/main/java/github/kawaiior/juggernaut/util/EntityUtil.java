package github.kawaiior.juggernaut.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EntityUtil {

    /**
     * What, if anything, is the player currently looking at?
     */
    @Nullable
    public static Entity getPlayerLookTarget(World world, LivingEntity living, double range) {
        Entity pointedEntity = null;
        Vector3d srcVec = new Vector3d(living.getPosX(), living.getPosY() + living.getEyeHeight(), living.getPosZ());
        Vector3d lookVec = living.getLook(1.0F);
        Vector3d destVec = srcVec.add(lookVec.x * range, lookVec.y * range, lookVec.z * range);
        float var9 = 1.0F;
        List<Entity> possibleList = world.getEntitiesWithinAABBExcludingEntity(living, living.getBoundingBox().expand(lookVec.x * range, lookVec.y * range, lookVec.z * range).grow(var9, var9, var9));
        double hitDist = 0;

        for (Entity possibleEntity : possibleList) {

            if (possibleEntity.canBeCollidedWith()) {
                float borderSize = possibleEntity.getCollisionBorderSize();
                AxisAlignedBB collisionBB = possibleEntity.getBoundingBox().grow(borderSize, borderSize, borderSize);
                Optional<Vector3d> interceptPos = collisionBB.rayTrace(srcVec, destVec);

                if (collisionBB.contains(srcVec)) {
                    if (0.0D < hitDist || hitDist == 0.0D) {
                        pointedEntity = possibleEntity;
                        hitDist = 0.0D;
                    }
                } else if (interceptPos.isPresent()) {
                    double possibleDist = srcVec.distanceTo(interceptPos.get());

                    if (possibleDist < hitDist || hitDist == 0.0D) {
                        pointedEntity = possibleEntity;
                        hitDist = possibleDist;
                    }
                }
            }
        }
        return pointedEntity;
    }

    public static BlockPos getPlayerLookBlockPos(LivingEntity living, double maxRange) {
        World world = living.getEntityWorld();
        Vector3d start = living.getEyePosition(1.0F);
        Vector3d lookVec = living.getLookVec();
        Vector3d end = start.add(lookVec.scale(maxRange));

        BlockRayTraceResult rayTraceResult = world.rayTraceBlocks(new RayTraceContext(start, end, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, living));

        if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK) {
            return rayTraceResult.getPos();
        }

        return new BlockPos(end);
    }

    /**
     * 给玩家添加药水效果
     * 如果已存在相同的药水效果，那么增加药水效果的时间
     */
    public static void addEffect(LivingEntity living, Effect effect, int seconds, int level){
        EffectInstance instance = living.getActivePotionEffect(effect);
        if (instance==null){
            living.addPotionEffect(new EffectInstance(effect, seconds*20, level));
            return;
        }
        int duration = instance.getDuration();
        int amplifier = Math.max(instance.getAmplifier(), level);
        living.addPotionEffect(new EffectInstance(effect, duration+seconds*20, amplifier));
    }

    public static void addEffect(LivingEntity living, Effect effect, int seconds){
        addEffect(living, effect, seconds, 0);
    }

    /**
     * 给target实体添加动量 相对于某个living entity
     */
    public static void playerMotionEntity(LivingEntity target, LivingEntity attacker,
                                          double xForce, double yForce, double zForce,
                                          double xIncrease, double yIncrease, double zIncrease){
        double dx = target.getPosX() - attacker.getPosX();
        double dy = target.getPosY() - attacker.getPosY();
        double dz = target.getPosZ() - attacker.getPosZ();

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        dx /= distance;
        dy /= distance;
        dz /= distance;

        target.setMotion((dx+xIncrease) * xForce, (dy+yIncrease) * yForce, (dz+zIncrease) * zForce);
    }

    public static void playerMotionEntity(LivingEntity target, LivingEntity attacker,
                                          double xForce, double yForce, double zForce){
        playerMotionEntity(target, attacker, xForce, yForce, zForce, 0, 0, 0);
    }

    /**
     * 判断生物是否穿戴了指定的盔甲
     */
    public static boolean armorEquipped(LivingEntity living,
                                        ArmorItem head, ArmorItem chest, ArmorItem legs, ArmorItem feet){
        return living.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() == head
                && living.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() == chest
                && living.getItemStackFromSlot(EquipmentSlotType.LEGS).getItem() == legs
                && living.getItemStackFromSlot(EquipmentSlotType.FEET).getItem() == feet;
    }

    public static boolean armorEquipped(LivingEntity living,
                                        RegistryObject<ArmorItem> head, RegistryObject<ArmorItem> chest,
                                        RegistryObject<ArmorItem> legs, RegistryObject<ArmorItem> feet){
        return armorEquipped(living, head.get(), chest.get(), legs.get(), feet.get());
    }

    public static void coolDownItems(CooldownTracker cooldownTracker, Item[] items, int ticks){
        for (Item item : items) {
            cooldownTracker.setCooldown(item, ticks);
        }
    }

    // 获取玩家背包的空槽数量
    public static int getPlayerInventoryEmptySlotCount(PlayerEntity player){
        int count = 0;
        for (ItemStack stack: player.inventory.mainInventory){
            if (stack.isEmpty()){
                count++;
            }
        }
        if (player.getHeldItemOffhand().isEmpty()){
            count++;
        }
        return count;
    }

    // 消耗指定物品一个
    public static boolean consumeInventoryItem(final PlayerEntity player, final Item item) {
        return consumeInventoryItem(player.inventory.armorInventory, item) || consumeInventoryItem(player.inventory.mainInventory, item) || consumeInventoryItem(player.inventory.offHandInventory, item);
    }

    public static boolean consumeInventoryItem(final NonNullList<ItemStack> stacks, final Item item) {
        for (ItemStack stack : stacks) {
            if (stack.getItem() == item) {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    }

    public static boolean consumeInventoryItem(final NonNullList<ItemStack> stacks, final Item item, int count) {
        for (ItemStack stack : stacks) {
            if (stack.getItem() == item) {
                if (stack.getCount() >= count) {
                    stack.shrink(count);
                    return true;
                }
            }
        }
        return false;
    }

    // 粒子效果
    public static <T extends IParticleData> void spawnParticlesAroundEntity(ServerWorld world, LivingEntity entity, T type, int particleCount) {
        for (int i = 0; i < particleCount; ++i) {
            double offsetX = world.rand.nextGaussian() * 0.02;
            double offsetY = world.rand.nextGaussian() * 0.02;
            double offsetZ = world.rand.nextGaussian() * 0.02;

            world.spawnParticle(type,
                    entity.getPosX() + world.rand.nextDouble() * entity.getWidth() * 2.0 - entity.getWidth(),
                    entity.getPosY() + 0.5 + world.rand.nextDouble() * entity.getHeight(),
                    entity.getPosZ() + world.rand.nextDouble() * entity.getWidth() * 2.0 - entity.getWidth(),
                    10, offsetX, offsetY, offsetZ, 2D);
        }
    }

    public static <T extends IParticleData> void spawnParticlesAroundEntity(ServerWorld world, LivingEntity entity, T type) {
        spawnParticlesAroundEntity(world, entity, type, 5);
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public static String getPlayerNameByUUID(UUID uuid){
        ClientWorld world = Minecraft.getInstance().world;
        if (world == null){
            return null;
        }
        for (PlayerEntity player: world.getPlayers()){
            if (player.getUniqueID().equals(uuid)){
                return player.getScoreboardName();
            }
        }
        return null;
    }


}
