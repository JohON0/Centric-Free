/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package pa.centric.client.ui.schedules.rw.impl;

import pa.centric.client.ui.schedules.rw.Schedule;
import pa.centric.client.ui.schedules.rw.TimeType;

public class MascotSchedule
extends Schedule {
    @Override
    public String getName() {
        return "\u0422\u0430\u043b\u0438\u0441\u043c\u0430\u043d";
    }

    @Override
    public TimeType[] getTimes() {
        return new TimeType[]{TimeType.NINETEEN_HALF};
    }
}

