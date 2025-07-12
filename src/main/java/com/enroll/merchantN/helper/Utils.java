package com.enroll.merchantN.helper;

import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author raghav
 */
@Service
public class Utils {
    private static final AtomicInteger atomicInteger = new AtomicInteger(0);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmm");
    public static synchronized String generateId(final String prefix){
        int unique = atomicInteger.incrementAndGet();
        if (unique > 99990) {
            atomicInteger.set(2);
            unique = 1;
        }
        String paddedNumber = String.format("%05d", unique);
        return prefix + dateFormat.format(new Date()) + paddedNumber;

    }
}
