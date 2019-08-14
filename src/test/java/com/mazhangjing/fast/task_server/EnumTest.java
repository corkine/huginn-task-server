package com.mazhangjing.fast.task_server;

import com.mazhangjing.fast.task_server.entity.Status;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EnumTest {

    @Test public void testEnum() {
        String a = "FINISHED";
        Status status = Enum.valueOf(Status.class, a);
        assert (status.equals(Status.FINISHED));
    }

    @Test public void testEnum2() {
        String a = "finished".toUpperCase();
        Status status = Enum.valueOf(Status.class, a);
        assert (status.equals(Status.FINISHED));
    }
}
