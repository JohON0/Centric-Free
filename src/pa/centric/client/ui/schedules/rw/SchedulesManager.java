/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package pa.centric.client.ui.schedules.rw;

import pa.centric.client.ui.schedules.rw.Schedule;
import pa.centric.client.ui.schedules.rw.impl.AirDropSchedule;
import pa.centric.client.ui.schedules.rw.impl.CompetitionSchedule;
import pa.centric.client.ui.schedules.rw.impl.MascotSchedule;
import pa.centric.client.ui.schedules.rw.impl.ScroogeSchedule;
import pa.centric.client.ui.schedules.rw.impl.SecretMerchantSchedule;
import pa.centric.util.IMinecraft;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SchedulesManager
implements IMinecraft {
    private final List<Schedule> schedules = new ArrayList<Schedule>();

    public SchedulesManager() {
        this.schedules.addAll(Arrays.asList(new AirDropSchedule(), new ScroogeSchedule(), new SecretMerchantSchedule(), new MascotSchedule(), new CompetitionSchedule()));
    }

    public List<Schedule> getSchedules() {
        return this.schedules;
    }
}

