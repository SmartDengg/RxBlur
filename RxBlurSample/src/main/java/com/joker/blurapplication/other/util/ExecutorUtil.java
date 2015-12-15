package com.joker.blurapplication.other.util;

import android.support.annotation.NonNull;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by Joker on 2015/11/30.
 */
public class ExecutorUtil {

  public static Executor getSynchronousExecutor() {
    return new SynchronousExecutor();
  }

  public static Executor getIoExecutor() {
    return new IoExecutor();
  }

  public static Executor getSingleThreadExecutor() {
    return new SingleThreadExecutor();
  }

  private static class SynchronousExecutor implements Executor {
    @Override public void execute(@NonNull final Runnable runnable) {
      runnable.run();
    }
  }

  private static class IoExecutor implements Executor {
    @Override public void execute(@NonNull final Runnable runnable) {
      Schedulers.io().createWorker().schedule(new Action0() {
        @Override public void call() {
          runnable.run();
        }
      });
    }
  }

  private static class SingleThreadExecutor implements Executor {
    @Override public void execute(@NonNull final Runnable runnable) {
      Executors.newSingleThreadExecutor().execute(runnable);
    }
  }
}
