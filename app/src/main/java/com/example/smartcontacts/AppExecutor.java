package com.example.smartcontacts;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutor {

    private final Executor diskIO;
    private final Executor mainThread;

    private static volatile AppExecutor instance;

    public static AppExecutor getInstance() {
        if (instance == null) {
            synchronized (AppExecutor.class) {
                if (instance == null) {
                    instance = new AppExecutor();
                }
            }
        }
        return instance;
    }

    private AppExecutor() {
        // Single thread = serialized DB access, no concurrent write conflicts
        diskIO     = Executors.newSingleThreadExecutor();
        mainThread = new MainThreadExecutor();
    }

    public Executor diskIO()     { return diskIO; }

    public Executor mainThread() { return mainThread; }

    // ── Inner class ───────────────────────────────────────────────────────────

    private static class MainThreadExecutor implements Executor {
        private final Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            handler.post(command);
        }
    }
}
