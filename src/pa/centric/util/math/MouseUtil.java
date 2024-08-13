package pa.centric.util.math;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import pa.centric.util.IMinecraft;

import static pa.centric.util.IMinecraft.mc;

public class MouseUtil implements IMinecraft {
    public MouseUtil() {
    }

    public static Entity getMouseOver(Entity target, float yaw, float pitch, double distance) {
        Entity entity = mc.getRenderViewEntity();
        if (entity != null && mc.world != null) {
            RayTraceResult objectMouseOver = null;
            boolean flag = distance > 3.0;
            Vector3d startVec = entity.getEyePosition(1.0F);
            Vector3d directionVec = getVectorForRotation(pitch, yaw);
            Vector3d endVec = startVec.add(directionVec.x * distance, directionVec.y * distance, directionVec.z * distance);
            AxisAlignedBB axisalignedbb = target.getBoundingBox().grow((double)target.getCollisionBorderSize());
            EntityRayTraceResult entityraytraceresult = rayTraceEntities(entity, startVec, endVec, axisalignedbb, (p_lambda$getMouseOver$0_0_) -> {
                return !p_lambda$getMouseOver$0_0_.isSpectator() && p_lambda$getMouseOver$0_0_.canBeCollidedWith();
            }, distance);
            if (entityraytraceresult != null) {
                if (flag && startVec.distanceTo(startVec) > distance) {
                    objectMouseOver = BlockRayTraceResult.createMiss(startVec, (Direction)null, new BlockPos(startVec));
                }

                if (distance < distance || objectMouseOver == null) {
                    objectMouseOver = entityraytraceresult;
                }
            }

            if (objectMouseOver == null) {
                return null;
            }

            if (objectMouseOver instanceof EntityRayTraceResult) {
                EntityRayTraceResult obj = (EntityRayTraceResult)objectMouseOver;
                return obj.getEntity();
            }
        }

        return null;
    }

    public static EntityRayTraceResult rayTraceEntities(Entity shooter, Vector3d startVec, Vector3d endVec, AxisAlignedBB boundingBox, Predicate<Entity> filter, double distance) {
        World world = shooter.world;
        double closestDistance = distance;
        Entity entity = null;
        Vector3d closestHitVec = null;
        Iterator var12 = world.getEntitiesInAABBexcluding(shooter, boundingBox, filter).iterator();

        while(true) {
            while(var12.hasNext()) {
                Entity entity1 = (Entity)var12.next();
                AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow((double)entity1.getCollisionBorderSize());
                Optional<Vector3d> optional = axisalignedbb.rayTrace(startVec, endVec);
                if (axisalignedbb.contains(startVec)) {
                    if (closestDistance >= 0.0) {
                        entity = entity1;
                        closestHitVec = startVec;
                        closestDistance = 0.0;
                    }
                } else if (optional.isPresent()) {
                    Vector3d vector3d1 = (Vector3d)optional.get();
                    double d3 = startVec.distanceTo((Vector3d)optional.get());
                    if (d3 < closestDistance || closestDistance == 0.0) {
                        boolean flag1 = false;
                        if (!flag1 && entity1.getLowestRidingEntity() == shooter.getLowestRidingEntity()) {
                            if (closestDistance == 0.0) {
                                entity = entity1;
                                closestHitVec = vector3d1;
                            }
                        } else {
                            entity = entity1;
                            closestHitVec = vector3d1;
                            closestDistance = d3;
                        }
                    }
                }
            }

            return entity == null ? null : new EntityRayTraceResult(entity, closestHitVec);
        }
    }

    public static RayTraceResult rayTrace(double rayTraceDistance, float yaw, float pitch, Entity entity) {
        Vector3d startVec = mc.player.getEyePosition(1.0F);
        Vector3d directionVec = getVectorForRotation(pitch, yaw);
        Vector3d endVec = startVec.add(directionVec.x * rayTraceDistance, directionVec.y * rayTraceDistance, directionVec.z * rayTraceDistance);
        return mc.world.rayTraceBlocks(new RayTraceContext(startVec, endVec, BlockMode.OUTLINE, FluidMode.NONE, entity));
    }

    public static RayTraceResult rayTraceResult(double rayTraceDistance, float yaw, float pitch, Entity entity) {
        RayTraceResult object = null;
        if (entity != null && mc.world != null) {
            float partialTicks = mc.getRenderPartialTicks();
            object = rayTrace(rayTraceDistance, yaw, pitch, entity);
            Vector3d vector3d = entity.getEyePosition(partialTicks);
            boolean flag = false;
            double d1 = rayTraceDistance;
            double distance;
            if (mc.playerController.extendedReach()) {
                d1 = 6.0;
                distance = d1;
            } else {
                if (rayTraceDistance > 3.0) {
                    flag = true;
                }

                distance = rayTraceDistance;
            }

            d1 *= d1;
            if (object != null) {
                d1 = ((RayTraceResult)object).getHitVec().squareDistanceTo(vector3d);
            }

            Vector3d vector3d1 = getVectorForRotation(pitch, yaw);
            Vector3d vector3d2 = vector3d.add(vector3d1.x * distance, vector3d1.y * distance, vector3d1.z * distance);
            float f = 1.0F;
            AxisAlignedBB axisalignedbb = entity.getBoundingBox().expand(vector3d1.scale(distance)).grow(1.0, 1.0, 1.0);
            EntityRayTraceResult entityraytraceresult = ProjectileHelper.rayTraceEntities(entity, vector3d, vector3d2, axisalignedbb, (p_lambda$getMouseOver$0_0_) -> {
                return !p_lambda$getMouseOver$0_0_.isSpectator() && p_lambda$getMouseOver$0_0_.canBeCollidedWith();
            }, d1);
            if (entityraytraceresult != null) {
                Entity entity1 = entityraytraceresult.getEntity();
                Vector3d vector3d3 = entityraytraceresult.getHitVec();
                double d2 = vector3d.squareDistanceTo(vector3d3);
                if (flag && d2 > 9.0) {
                    object = BlockRayTraceResult.createMiss(vector3d3, Direction.getFacingFromVector(vector3d1.x, vector3d1.y, vector3d1.z), new BlockPos(vector3d3));
                } else if (d2 < d1 || object == null) {
                    object = entityraytraceresult;
                }
            }
        }

        return (RayTraceResult)object;
    }

    public static boolean rayTraceWithBlock(double rayTraceDistance, float yaw, float pitch, Entity entity, Entity target) {
        RayTraceResult object = null;
        if (entity != null && mc.world != null) {
            float partialTicks = mc.getRenderPartialTicks();
            object = rayTrace(rayTraceDistance, yaw, pitch, entity);
            Vector3d vector3d = entity.getEyePosition(partialTicks);
            boolean flag = false;
            double d1 = rayTraceDistance;
            double distance;
            if (mc.playerController.extendedReach()) {
                d1 = 6.0;
                distance = d1;
            } else {
                if (rayTraceDistance > 3.0) {
                    flag = true;
                }

                distance = rayTraceDistance;
            }

            d1 *= d1;
            if (object != null) {
                d1 = ((RayTraceResult)object).getHitVec().squareDistanceTo(vector3d);
            }

            Vector3d vector3d1 = getVectorForRotation(pitch, yaw);
            Vector3d vector3d2 = vector3d.add(vector3d1.x * distance, vector3d1.y * distance, vector3d1.z * distance);
            float f = 1.0F;
            AxisAlignedBB axisalignedbb = entity.getBoundingBox().expand(vector3d1.scale(distance)).grow(1.0, 1.0, 1.0);
            EntityRayTraceResult entityraytraceresult = ProjectileHelper.rayTraceEntities(entity, vector3d, vector3d2, axisalignedbb, (p_lambda$getMouseOver$0_0_) -> {
                return !p_lambda$getMouseOver$0_0_.isSpectator() && p_lambda$getMouseOver$0_0_.canBeCollidedWith();
            }, d1);
            if (entityraytraceresult != null) {
                Entity entity1 = entityraytraceresult.getEntity();
                Vector3d vector3d3 = entityraytraceresult.getHitVec();
                double d2 = vector3d.squareDistanceTo(vector3d3);
                if (flag && d2 > 9.0) {
                    object = BlockRayTraceResult.createMiss(vector3d3, Direction.getFacingFromVector(vector3d1.x, vector3d1.y, vector3d1.z), new BlockPos(vector3d3));
                } else if (d2 < d1 || object == null) {
                    object = entityraytraceresult;
                }
            }
        }

        if (object instanceof EntityRayTraceResult) {
            return ((EntityRayTraceResult)object).getEntity().getEntityId() == target.getEntityId();
        } else {
            return false;
        }
    }

    public static Vector3d getVectorForRotation(float pitch, float yaw) {
        float yawRadians = -yaw * 0.017453292F - 3.1415927F;
        float pitchRadians = -pitch * 0.017453292F;
        float cosYaw = MathHelper.cos(yawRadians);
        float sinYaw = MathHelper.sin(yawRadians);
        float cosPitch = -MathHelper.cos(pitchRadians);
        float sinPitch = MathHelper.sin(pitchRadians);
        return new Vector3d((double)(sinYaw * cosPitch), (double)sinPitch, (double)(cosYaw * cosPitch));
    }
}

