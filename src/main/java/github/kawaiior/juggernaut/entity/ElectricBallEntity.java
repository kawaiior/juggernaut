package github.kawaiior.juggernaut.entity;

import github.kawaiior.juggernaut.game.GameData;
import github.kawaiior.juggernaut.game.GameServer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ElectricBallEntity extends EntityThrowableCopy{

    private static final DataParameter<Integer> CREATE_TIME = EntityDataManager.createKey(ElectricBallEntity.class, DataSerializers.VARINT);
    private long createTime = -1;
    private boolean init = false;
    public ElectricBallEntity(EntityType<ElectricBallEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void onImpact(RayTraceResult rayTraceResult) {
        RayTraceResult.Type type = rayTraceResult.getType();
        if (type != RayTraceResult.Type.ENTITY) {
            return;
        }
        EntityRayTraceResult entityRayTraceResult = (EntityRayTraceResult) rayTraceResult;
        Entity entity = entityRayTraceResult.getEntity();
        if (!(entity instanceof ServerPlayerEntity)){
            return;
        }
        ServerPlayerEntity player = (ServerPlayerEntity) entity;
        if (player.equals(GameServer.getInstance().getJuggernautPlayer())){
            this.setMotion(0, 0, 0);
            this.velocityChanged = true;
            this.hit = true;
        }
    }

    @Override
    protected void tickAfterHit() {
        if (this.world.isRemote){
            return;
        }

        // 判断juggernaut是否在范围内
        ServerPlayerEntity juggernaut = GameServer.getInstance().getJuggernautPlayer();
        if (juggernaut != null && this.getDistance(juggernaut) <= 5){
            juggernaut.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 100, 0));
            GameData gameData = GameServer.getInstance().getPlayerGameData(juggernaut);
            // 先判断临时护甲
            float tempShield = gameData.getShieldData().temporaryShield;
            if (tempShield > 5){
                gameData.getShieldData().temporaryShield -= 5;
                gameData.getShieldData().syncData(juggernaut);
            }else {
                float shield = gameData.getShieldData().shield;
                if (shield > 0){
                    float nextShield = shield - 5 + tempShield;
                    if (nextShield < 0){
                        nextShield = 0;
                    }
                    gameData.getShieldData().shield = nextShield;
                    gameData.getShieldData().syncData(juggernaut);
                }
            }
        }
    }

    @Override
    public void tick() {
        if (this.ticksExisted % 20 == 0) {
            if (!init){
                if (this.dataManager.get(CREATE_TIME) == Integer.MAX_VALUE) {
                    this.dataManager.set(CREATE_TIME, (int) (System.currentTimeMillis() / 1000));
                }
                createTime = this.dataManager.get(CREATE_TIME) * 1000L;
                init = true;
            }
            long now = System.currentTimeMillis();
            if (now - createTime >= 1000 * 30) {
                this.remove();
            }
        }
        super.tick();
    }

    @Override
    protected void registerData() {
        this.dataManager.register(CREATE_TIME, Integer.MAX_VALUE);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        this.dataManager.set(CREATE_TIME, compound.getInt("create_time"));
    }

    @Override
    public boolean hasNoGravity(){
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    public long getMaxTimeAfterHit() {
        return 15000;  // 15s
    }
}
