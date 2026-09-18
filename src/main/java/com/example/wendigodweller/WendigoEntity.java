package com.example.wendigodweller;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.UUID;

public class WendigoEntity extends Monster implements GeoEntity {
    @Nullable
    private UUID voiceTarget;
    private int voiceTargetTicks;
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE =
            RawAnimation.begin().thenLoop("animation.wendigo.idle");
    private static final RawAnimation WALK =
            RawAnimation.begin().thenLoop("animation.wendigo.walk");
    private static final RawAnimation ATTACK =
            RawAnimation.begin().thenPlay("animation.wendigo.attack");
    private static final RawAnimation BREAK =
            RawAnimation.begin().thenPlay("animation.wendigo.break");
    private static final RawAnimation SCREAM =
            RawAnimation.begin().thenPlay("animation.wendigo.scream");

    public WendigoEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 25;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 64.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.65D);
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        triggerAttackAnimation();
        return super.doHurtTarget(target);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new WendigoBreakBarrierGoal(this));
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.25D, true));
        goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 24.0F));
        goalSelector.addGoal(3, new RandomStrollGoal(this, 0.9D));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        goalSelector.addGoal(5, new FloatGoal(this));
    }

    public void hearPlayer(Player player) {
        voiceTarget = player.getUUID();
        voiceTargetTicks = 100; // refreshed while the player talks
        setTarget(player);
        triggerScreamAnimation();
    }

    @Nullable
    public UUID getVoiceTarget() {
        return voiceTarget;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5, state -> {
            state.getController().setAnimation(state.isMoving() ? WALK : IDLE);
            return PlayState.CONTINUE;
        }));
        controllers.add(new AnimationController<>(this, "actions", 0, state -> PlayState.STOP)
                .triggerableAnim("attack", ATTACK)
                .triggerableAnim("break", BREAK)
                .triggerableAnim("scream", SCREAM));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    public void triggerAttackAnimation() { triggerAnim("actions", "attack"); }
    public void triggerBreakAnimation() { triggerAnim("actions", "break"); }
    public void triggerScreamAnimation() { triggerAnim("actions", "scream"); }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            return;
        }

        if (voiceTargetTicks > 0) {
            voiceTargetTicks--;
        } else {
            voiceTarget = null;
            noPhysics = false;
        }

        if (voiceTarget != null) {
            Player player = level().getPlayerByUUID(voiceTarget);
            if (player == null || !player.isAlive() || player.distanceToSqr(this) > 128.0D * 128.0D) {
                voiceTarget = null;
                voiceTargetTicks = 0;
                noPhysics = false;
                return;
            }

            // "Through walls" mode: disable collision and steer directly toward the speaker.
            noPhysics = true;
            getNavigation().stop();
            setTarget(player);

            Vec3 delta = player.position().subtract(position());
            double distance = delta.length();

            if (distance > 2.2D) {
                Vec3 direction = delta.normalize();
                double speed = 0.34D;
                setDeltaMovement(direction.scale(speed));
                hasImpulse = true;
                setYRot((float)(Math.toDegrees(Math.atan2(-direction.x, direction.z))));
                yBodyRot = getYRot();
            } else {
                setDeltaMovement(Vec3.ZERO);
                noPhysics = false;
            }
        }
    }
}
