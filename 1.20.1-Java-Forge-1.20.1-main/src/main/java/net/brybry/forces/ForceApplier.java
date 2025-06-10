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

public class ForceApplier {
    private static final double FORCE = 100.0;
    private static final double DISTANCE = 5.0;


    private static class GooberInfo {
        public final BlockPos pos;
        public final Long shipId;

        public GooberInfo(BlockPos pos, Long shipId) {
            this.pos = pos;
            this.shipId = shipId;
        }


    }
        public static void onEvilGooberBlockPowered(ServerLevel level, BlockPos pos ){

            try {
                System.out.println("forcing an Evil Goober Block at: " + pos);



            } catch (Exception exception) {


                System.out.print("Wizard has been fucking around with the code and the powering method for the evil goober block is fucked :(");
            }


        }




        private static void applyForces(ServerLevel level, GooberInfo goobInfo, Vector3d force) {
            if (goobInfo.shipId == null) return;

           try{


               double maxForce = 1000000.0;
               if (force.length() > maxForce) {
                   force.normalize().mul(maxForce);
               }

               var shipObjectWorld = VSGameUtilsKt.getShipObjectWorld(level);
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

               System.err.println("Something happened, I dont know fam :clueless:" );
               exception.printStackTrace();
           }
        }


    private static Vector3d getBlockWorldPosition(ServerLevel level, GooberInfo magnet) {
        try {
            if (magnet.shipId == null) {

                return new Vector3d(magnet.pos.getX() + 0.5, magnet.pos.getY() + 0.5, magnet.pos.getZ() + 0.5);
            } else {
                Ship ship = VSGameUtilsKt.getShipObjectWorld(level).getAllShips().getById(magnet.shipId);
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




}
