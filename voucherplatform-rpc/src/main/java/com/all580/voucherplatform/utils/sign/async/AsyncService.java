package com.all580.voucherplatform.utils.sign.async;

import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Linv2 on 2017-06-15.
 */
@Service
public class AsyncService {
    private ExecutorService pushExecutor = Executors.newFixedThreadPool(5);

    public void run(Runnable runnable) {
        pushExecutor.execute(runnable);
    }
}
