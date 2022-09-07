package us.mytheria.bloblib.utilities;

import fr.skytasul.guardianbeam.Laser;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nullable;

public class BeamUtil {

    /**
     * Creates a new GuardianLaser instance
     *
     * @param start           Location where laser will start
     * @param end             Location where laser will end
     * @param duration        Duration of laser in seconds (<i>-1 if infinite</i>)
     * @param visibleDistance Distance where laser will be visible (<i>-1 if infinite</i>)
     * @return null if ReflectiveOperationException was found. Otherwise, a new GuardianLaser.
     */
    @Nullable
    public static Laser.GuardianLaser guardianLaser(Location start, Location end, int duration, int visibleDistance) {
        try {
            return new Laser.GuardianLaser(start, end, duration, visibleDistance);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a new GuardianLaser instance
     *
     * @param start           Location where laser will start
     * @param target          Entity who the laser will follow
     * @param duration        Duration of laser in seconds (<i>-1 if infinite</i>)
     * @param visibleDistance Distance where laser will be visible (<i>-1 if infinite</i>)
     * @return null if ReflectiveOperationException was found. Otherwise, a new GuardianLaser.
     */
    public static Laser.GuardianLaser followerGuardianLaser(Location start, LivingEntity target, int duration, int visibleDistance) {
        try {
            return new Laser.GuardianLaser(start, target, duration, visibleDistance);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a new CrystalLaser instance
     *
     * @param start           Location where laser will start
     * @param end             Location where laser will end
     * @param duration        Duration of laser in seconds (<i>-1 if infinite</i>)
     * @param visibleDistance Distance where laser will be visible (<i>-1 if infinite</i>)
     * @return null if ReflectiveOperationException was found. Otherwise, a new CrystalLaser.
     */
    public static Laser.CrystalLaser crystalLaser(Location start, Location end, int duration, int visibleDistance) {
        try {
            return new Laser.CrystalLaser(start, end, duration, visibleDistance);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
