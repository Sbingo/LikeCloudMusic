package sbingo.likecloudmusic.ui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import sbingo.likecloudmusic.R;
import sbingo.likecloudmusic.common.MyApplication;
import sbingo.likecloudmusic.di.component.ActivityComponent;
import sbingo.likecloudmusic.di.component.DaggerActivityComponent;
import sbingo.likecloudmusic.di.module.ActivityModule;
import sbingo.likecloudmusic.utils.RxUtils;

/**
 * Author: Sbingo
 * Date:   2016/12/11
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected abstract int getLayoutId();

    protected abstract void initInjector();

    protected abstract void initViews();

    protected abstract void customToolbar();

    protected abstract boolean hasToolbar();

    protected CompositeSubscription mSubscriptions;

    protected ActionBar actionBar;

    protected ActivityComponent mActivityComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTranslucent();
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initToolbar();
        customToolbar();
        mSubscriptions = new CompositeSubscription();
        initActivityComponent();
        initInjector();
        initViews();
    }

    private void initToolbar() {
        if (hasToolbar()) {
            setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
            actionBar = getSupportActionBar();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected void setStatusBarTranslucent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.colorPrimary);
        }
    }

    private void initActivityComponent() {
        mActivityComponent = DaggerActivityComponent.builder()
                .applicationComponent(((MyApplication) getApplication()).getmApplicationComponent())
                .activityModule(new ActivityModule(this))
                .build();
    }

    protected void openActivity(Class a) {
        Intent intent = new Intent(this, a);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.clear();
    }
}
