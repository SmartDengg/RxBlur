package com.joker.blurapplication.other;

import com.bumptech.glide.load.Key;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

/**
 * Created by Joker on 2015/12/5.
 */
public class IntegerVersionSignature implements Key {
  private Integer currentVersion;

  public IntegerVersionSignature(Integer currentVersion) {
    this.currentVersion = currentVersion;
  }

  @Override public boolean equals(Object o) {

    if (o == IntegerVersionSignature.this) return true;
    if (!(o instanceof IntegerVersionSignature)) return false;
    IntegerVersionSignature other = (IntegerVersionSignature) o;
    return other.currentVersion == IntegerVersionSignature.this.currentVersion;
  }

  @Override public int hashCode() {
    int result = 17;
    result = result * 37 + currentVersion.hashCode();
    return result;
  }

  @Override public void updateDiskCacheKey(MessageDigest messageDigest) {
    messageDigest.update(ByteBuffer.allocate(Integer.SIZE).putInt(currentVersion).array());
  }

  @Override public String toString() {
    return "IntegerVersionSignature{" +
        "currentVersion=" + currentVersion +
        '}';
  }
}