package net.brybry.forces;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.HashMap;
import java.util.Map;

public class ForceApplier {
    private static final double FORCE = 100.0;
    private static final double DISTANCE = 5.0;
    private static final double MIN_MASS_FOR_MOVEMENT = 100.0;

    private static class GooberInfo {
        public final BlockPos pos;
        public final Long shipId;

        public GooberInfo(BlockPos pos, Long shipId) {
            this.pos = pos;
            this.shipId = shipId;
        }


    }

    public static void onEvilGooberBlockPowered(ServerLevel onEGBPoweredLevel, BlockPos pos) {

        try {
            System.out.println("starting to power an Evil Goober Block at: " + pos);
            Ship managingShip = VSGameUtilsKt.getShipManagingPos(onEGBPoweredLevel, pos);
            Long shipId = managingShip != null ? managingShip.getId() : null;

            if (managingShip != null) {
                System.out.println("EGB is on ship " + shipId + ", ensuring force inducer exists...");
                ensureForceInducerExists(onEGBPoweredLevel, shipId);

                if (!isShipFullyLoaded(managingShip)) {
                    System.out.println("Ship not fully loaded, scheduling delayed activation...");
                    scheduleDelayedMagnetActivation(onEGBPoweredLevel, pos, 20);
                    return;
                }
            }

            GooberInfo gooberInfo = new GooberInfo(pos, shipId);

            System.out.println("Evil Goober Block has been powered, yippee, now applying forces");

            Vector3d blockPosition = getBlockWorldPosition(onEGBPoweredLevel, gooberInfo);
            Vector3d forceDirection = blockPosition.normalize();
            Vector3d maintainForce = new Vector3d(forceDirection).mul(3000);

            applyForce(onEGBPoweredLevel, gooberInfo, maintainForce);


        } catch (Exception exception) {


            System.out.print("Wizard has been fucking around with the code and the powering method for the evil goober block is fucked :(");
        }


    }


    private static void applyForce(ServerLevel applyForceLevel, GooberInfo goobInfo, Vector3d force) {
        if (goobInfo.shipId == null) return;

        try {


            double maxForce = 1000000.0;
            if (force.length() > maxForce) {
                force.normalize().mul(maxForce);
            }

            var shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(applyForceLevel);
            var serverShip = shipObjectWorld.getQueryableShipData().getById(goobInfo.shipId);


            if (serverShip != null) {
                ForceInducer inducer = ForceInducer.getOrCreate(serverShip);

                if (inducer.getQueueSize() > 50) {
                    inducer.clearQueue();
                    serverShip.saveAttachment(ForceInducer.class, inducer);
                }
                inducer.addForce(force);
            } else {
                System.out.println("Could not find server ship with the  : " + goobInfo.shipId + " ID. How Silly");

            }

        } catch (Exception exception) {

            System.err.println("Something happened, I dont know fam :clueless:");
            exception.printStackTrace();
        }
    }


    private static Vector3d getBlockWorldPosition(ServerLevel getBlockWorldPositionLevel, GooberInfo magnet) {
        try {
            if (magnet.shipId == null) {

                return new Vector3d(magnet.pos.getX() + 0.5, magnet.pos.getY() + 0.5, magnet.pos.getZ() + 0.5);
            } else {
                Ship ship = VSGameUtilsKt.getShipObjectWorld(getBlockWorldPositionLevel).getAllShips().getById(magnet.shipId);
                if (ship == null) return null;

                Vector3d shipLocalPos = new Vector3d(magnet.pos.getX() + 0.5, magnet.pos.getY() + 0.5, magnet.pos.getZ() + 0.5);
                Vector3d worldPos = new Vector3d();
                ship.getTransform().getShipToWorld().transformPosition(shipLocalPos, worldPos);
                return worldPos;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private static Vector3d getDirectionVector(Direction direction) {
        return switch (direction) {
            case NORTH -> new Vector3d(0, 0, -1);
            case SOUTH -> new Vector3d(0, 0, 1);
            case EAST -> new Vector3d(1, 0, 0);
            case WEST -> new Vector3d(-1, 0, 0);
            case UP -> new Vector3d(0, 1, 0);
            case DOWN -> new Vector3d(0, -1, 0);
        };
    }

    private static double getMagnetMass(ServerLevel getMagnetMassLevel, GooberInfo magnet) {
        if (magnet.shipId == null) {
            return Double.MAX_VALUE; // World has infinite mass
        }
        try {
            Ship ship = VSGameUtilsKt.getShipObjectWorld(getMagnetMassLevel).getAllShips().getById(magnet.shipId);
            if (ship == null) return MIN_MASS_FOR_MOVEMENT;
            return 1000.0;
        } catch (Exception e) {
            return MIN_MASS_FOR_MOVEMENT;
        }
    }

    private static double calculateForceMagnitude(double distance, double mass1, double mass2) {
        if (distance <= 0) return 0;
        double baseForceMagnitude = 1000000.0;
        double force = baseForceMagnitude / (distance * distance);

        force = Math.max(force, 100000.0);
        return force;
    }


    private static void ensureForceInducerExists(ServerLevel EnsureInducerExistsLevel, Long shipId) {
        try {
            var shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(EnsureInducerExistsLevel);
            var serverShip = shipObjectWorld.getQueryableShipData().getById(shipId);

            if (serverShip != null) {
                ForceInducer inducer = serverShip.getAttachment(ForceInducer.class);
                if (inducer == null) {
                    inducer = new ForceInducer();
                    serverShip.saveAttachment(ForceInducer.class, inducer);
                    System.out.println("Created force inducer for ship " + shipId);
                }
            }
        } catch (Exception e) {
            System.err.println("Error ensuring force inducer exists: " + e.getMessage());
        }


    }
    private static final Map<String, Integer> delayedActivations = new HashMap<>();
    private static void scheduleDelayedMagnetActivation(ServerLevel scheduleDelayedMagnetActivationLevel, BlockPos pos, int delayTicks) {
        String key = pos.toString() + "_" + scheduleDelayedMagnetActivationLevel.dimension().location().toString();
        delayedActivations.put(key, delayTicks);
        System.out.println("Scheduled delayed activation for EGB at " + pos + " in " + delayTicks + " ticks");
    }
    private static boolean isShipFullyLoaded(Ship ship) {
        try {
            return ship.getTransform() != null &&
                    ship.getTransform().getShipToWorld() != null &&
                    ship.getChunkClaim() != null;
        } catch (Exception e) {
            System.err.println("Error checking ship load state: " + e.getMessage());
            return false;
        }
    }


}