package github.kawaiior.juggernaut.card;

import github.kawaiior.juggernaut.entity.EntityBabylonWeapon;
import github.kawaiior.juggernaut.entity.render.RenderBabylonWeapon;
import github.kawaiior.juggernaut.init.EntityTypeRegistry;
import github.kawaiior.juggernaut.util.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class CardGilgamesh extends GameCard{

    private static final Random random = new Random();
    private static int weaponsSpawned = 0;
    private static final double SPEED = 5D;

    @Override
    public void playerUseSkill(@Nonnull ServerPlayerEntity player, @Nullable ServerPlayerEntity target) {
        Vector3d playerLook;
        BlockRayTraceResult rtr = raytraceFromEntity(player, 64, true);
        if (rtr.getType() != RayTraceResult.Type.BLOCK) {
            playerLook = player.getLookVec().scale(64).add(player.getPositionVec());
        } else {
            playerLook = Vector3d.copyCentered(rtr.getPos());
        }

        EntityBabylonWeapon entity = new EntityBabylonWeapon(EntityTypeRegistry.BABYLON_WEAPON_ENTITY.get(), player.world);

        entity.setOwner(player);
        entity.setPosition(player.getPosX(), player.getPosYEye(), player.getPosZ());
        entity.rotationYaw = player.rotationYaw;
        entity.setVariety(random.nextInt(RenderBabylonWeapon.WEAPON_TYPES));
        entity.setDelay(10);
        entity.setRotation(MathHelper.wrapDegrees(-player.rotationYaw + 180));

        int spawned = getWeaponsSpawned();
        Vector3 look = new Vector3(player.getLookVec()).multiply(1, 0, 1);
        double playerRot = Math.toRadians(player.rotationYaw + 90);
        if (look.x == 0 && look.z == 0) {
            look = new Vector3(Math.cos(playerRot), 0, Math.sin(playerRot));
        }
        look = look.normalize().multiply(-2);
        int div = spawned / 5;
        int mod = spawned % 5;
        Vector3 pl = look.add(Vector3.fromEntityCenter(player)).add(0, 1.6, div * 0.1);
        Vector3 axis = look.normalize().crossProduct(new Vector3(-1, 0, -1)).normalize();
        double rot = mod * Math.PI / 4 - Math.PI / 2;
        Vector3 axis1 = axis.multiply(div * 3.5 + 3.5).rotate(rot, look);
        if (axis1.y < 0) {
            axis1 = axis1.multiply(1, -1, 1);
        }

        Vector3 end = pl.add(axis1);
        entity.setPosition(end.x, end.y, end.z);

        Vector3 thisVec = Vector3.fromEntityCenter(entity);
        Vector3d mot = playerLook.subtract(thisVec.x, thisVec.y, thisVec.z).normalize().scale(2);
        entity.setNextMotion(mot);

        player.world.addEntity(entity);

        weaponsSpawned++;
        if (weaponsSpawned > 5){
            weaponsSpawned = 0;
        }
    }

    @Override
    public void playerUseUltimateSkill(@Nonnull ServerPlayerEntity player, @Nullable ServerPlayerEntity target) {
        Vector3d playerLook;
        BlockRayTraceResult rtr = raytraceFromEntity(player, 64, true);
        if (rtr.getType() != RayTraceResult.Type.BLOCK) {
            playerLook = player.getLookVec().scale(64).add(player.getPositionVec());
        } else {
            playerLook = Vector3d.copyCentered(rtr.getPos());
        }

        Vector3 look = new Vector3(player.getLookVec()).multiply(1, 0, 1);
        double playerRot = Math.toRadians(player.rotationYaw + 90);
        if (look.x == 0 && look.z == 0) {
            look = new Vector3(Math.cos(playerRot), 0, Math.sin(playerRot));
        }
        look = look.normalize().multiply(-2);

        for (int i = 0; i < 20; i++) {
            EntityBabylonWeapon entity = new EntityBabylonWeapon(EntityTypeRegistry.BABYLON_WEAPON_ENTITY.get(), player.world);

            entity.setOwner(player);
            entity.setPosition(player.getPosX(), player.getPosYEye(), player.getPosZ());
            entity.rotationYaw = player.rotationYaw;
            entity.setVariety(random.nextInt(RenderBabylonWeapon.WEAPON_TYPES));
            entity.setDelay(10);
            entity.setRotation(MathHelper.wrapDegrees(-player.rotationYaw + 180));

            int div = i / 5;
            int mod = i % 5;
            Vector3 pl = look.add(Vector3.fromEntityCenter(player)).add(0, 1.6, div * 0.1);
            Vector3 axis = look.normalize().crossProduct(new Vector3(-1, 0, -1)).normalize();
            double rot = mod * Math.PI / 4 - Math.PI / 2;
            Vector3 axis1 = axis.multiply(div * 3.5 + 3.5).rotate(rot, look);
            if (axis1.y < 0) {
                axis1 = axis1.multiply(1, -1, 1);
            }

            Vector3 end = pl.add(axis1);
            entity.setPosition(end.x, end.y, end.z);

            Vector3 thisVec = Vector3.fromEntityCenter(entity);
            Vector3d mot = playerLook.subtract(thisVec.x, thisVec.y, thisVec.z).normalize().scale(2);
            entity.setNextMotion(mot);

            player.world.addEntity(entity);
        }

    }

    @Override
    public void cardTick(@Nonnull PlayerEntity player) {

    }

    public static int getWeaponsSpawned() {
        return weaponsSpawned;
    }

    public static BlockRayTraceResult raytraceFromEntity(Entity e, double distance, boolean fluids) {
        return (BlockRayTraceResult) e.pick(distance, 1, fluids);
    }
}
