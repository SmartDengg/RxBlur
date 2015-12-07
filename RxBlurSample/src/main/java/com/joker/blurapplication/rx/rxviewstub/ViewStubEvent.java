package com.joker.blurapplication.rx.rxviewstub;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewStub;
import com.jakewharton.rxbinding.view.ViewEvent;

/**
 * Created by Joker on 2015/11/30.
 */
public class ViewStubEvent extends ViewEvent<ViewStub> {

  private View inflated;

  @CheckResult @NonNull
  public static ViewStubEvent create(@NonNull ViewStub viewStub, @NonNull View inflated) {
    return new ViewStubEvent(viewStub, inflated);
  }

  private ViewStubEvent(@NonNull ViewStub viewStub, @NonNull View inflated) {
    super(viewStub);
    this.inflated = inflated;
  }

  @NonNull public View getInflated() {
    return inflated;
  }

  @Override public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof ViewStubEvent)) return false;
    ViewStubEvent other = (ViewStubEvent) o;
    return other.view() == ViewStubEvent.this.view() && other.inflated.equals(ViewStubEvent.this.inflated);
  }

  @Override public int hashCode() {
    int result = 17;
    result = result * 37 + view().hashCode();
    result = result * 37 + inflated.hashCode();
    return result;
  }

  @Override public String toString() {
    return "ViewStubEvent{" +
        "view" + view() +
        "inflated=" + inflated +
        '}';
  }
}
