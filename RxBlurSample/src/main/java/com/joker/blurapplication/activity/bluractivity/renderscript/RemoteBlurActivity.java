package com.joker.blurapplication.activity.bluractivity.renderscript;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.joker.blurapplication.R;
import com.joker.blurapplication.activity.BaseActivity;
import com.joker.blurapplication.activity.BlurInterface;
import com.joker.blurapplication.rx.RxBlurEffective;
import com.joker.blurapplication.rx.SchedulersCompat;
import com.joker.blurapplication.rx.SimpleSubscriber;
import com.joker.blurapplication.rx.rxandroid.schedulers.AndroidSchedulers;
import com.trello.rxlifecycle.ActivityEvent;
import java.io.BufferedInputStream;
import java.io.IOException;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.mime.TypedInput;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Joker on 2015/12/6.
 */
public class RemoteBlurActivity extends BaseActivity implements BlurInterface {

  private static final String TAG = PicassoBlurActivity.class.getSimpleName();

  /*http://media.xtwind.com/images/2015/12/06/4aa43364c2144aa9a7521cc7b33acc08.png*/
  private static final String BASE_URL = "http://media.xtwind.com";

  private RestApi remoteService;

  public static void navigateToRemoteBlur(AppCompatActivity startingActivity) {
    Intent intent = new Intent(startingActivity, RemoteBlurActivity.class);
    startingActivity.startActivity(intent);
  }

  @Override public void loadBlurBitmap() {

    subscription.add(remoteService
                         .acquireBitmap()
                         .map(new Func1<Response, TransitionDrawable>() {
                           @Override public TransitionDrawable call(Response response) {

                             RemoteBlurActivity.this.startTime = System.currentTimeMillis();

                             TypedInput typedInput = response.getBody();
                             Log.e(TAG, "mimeType: " + typedInput.mimeType());

                             BufferedInputStream bufferedInputStream = null;
                             Bitmap sourceBitmap = null;

                             try {
                               bufferedInputStream = new BufferedInputStream(typedInput.in());
                               sourceBitmap = BitmapFactory.decodeStream(bufferedInputStream);
                             } catch (IOException e) {
                               Observable.error(new IOException("typedInput occur an io exception"));
                             } finally {
                               try {
                                 if (bufferedInputStream != null) {
                                   bufferedInputStream.close();
                                 }
                               } catch (IOException e) {
                                 Observable.error(new IOException("stream close exception"));
                               }
                             }

                             Bitmap blurBitmap = RxBlurEffective
                                 .bestBlur(RemoteBlurActivity.this, sourceBitmap, BLUR_RADIUS, 0.0f)
                                 .compose(RemoteBlurActivity.this.<Bitmap>bindUntilEvent(ActivityEvent.DESTROY))
                                 .toBlocking()
                                 .first();

                             Drawable[] layers = new Drawable[2];
                             layers[0] = new BitmapDrawable(RemoteBlurActivity.this.getResources(), sourceBitmap);
                             layers[1] = new BitmapDrawable(RemoteBlurActivity.this.getResources(), blurBitmap);

                             return new TransitionDrawable(layers);
                           }
                         })
                         .observeOn(AndroidSchedulers.mainThread())
                         .concatMap(new Func1<TransitionDrawable, Observable<Integer>>() {
                           @Override public Observable<Integer> call(TransitionDrawable transitionDrawable) {

                             RemoteBlurActivity.this.removeLoading();
                             int count = (int) (System.currentTimeMillis() - startTime);
                             transitionDrawable.startTransition(count);
                             blurIv.setImageDrawable(transitionDrawable);

                             return Observable
                                 .range(0, count, Schedulers.computation())
                                 .compose(RemoteBlurActivity.this.<Integer>bindUntilEvent(ActivityEvent.DESTROY));
                           }
                         })
                         .onBackpressureBuffer()
                         .compose(SchedulersCompat.<Integer>observeOnMainThread())
                         .subscribe(new SimpleSubscriber<Integer>() {
                           @Override public void onStart() {
                             this.request(1);
                           }

                           @Override public void onNext(Integer integer) {
                             durationTv.setText("" + integer + "ms");
                             this.request(1);
                           }

                           @Override public void onError(Throwable e) {
                             super.onError(e);

                             durationTv.setText(e.getMessage());
                           }
                         }));
  }

  @Override public int getLayoutId() {
    return R.layout.activity_simple_layout;
  }

  @Override public void setupActionBar() {
    getSupportActionBar().setTitle("Remote Blur");
    durationTv.setText("retrieving image from remote,wait......");

    RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASE_URL).setClient(new OkClient()).build();

    remoteService = restAdapter.create(RestApi.class);
  }

  interface RestApi {

    @GET("/images/2015/12/06/4aa43364c2144aa9a7521cc7b33acc08.png") Observable<Response> acquireBitmap();
  }
}
