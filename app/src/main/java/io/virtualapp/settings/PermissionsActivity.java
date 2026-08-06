package io.virtualapp.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.VPackageManager;
import com.lody.virtual.remote.InstalledAppInfo;
import com.lody.virtual.server.permission.PermissionGroup;

import io.virtualapp.R;
import io.virtualapp.abs.ui.VActivity;
import io.virtualapp.abs.ui.VUiKit;
import io.virtualapp.glide.GlideUtils;
import io.virtualapp.settings.AppManageActivity.AppManageInfo;

import static io.virtualapp.settings.PermissionManageActivity.EXTRA_APP_NAME;
import static io.virtualapp.settings.PermissionManageActivity.EXTRA_APP_UID;
import static io.virtualapp.settings.PermissionManageActivity.EXTRA_PERMISSIONS_TYPE;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alberto Lazari
 * @date 17/7/24
 */
public class PermissionsActivity extends VActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getSupportFragmentManager()
            .beginTransaction()
            .replace(android.R.id.content, new PermissionsFragment())
            .commit();
    }

    public static class PermissionsFragment extends PreferenceFragmentCompat {

        private static final int DEFAULT_APP_ICON = android.R.drawable.sym_def_app_icon;

        private Context ctx;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.permissions_preferences, rootKey);

            ctx = getActivity();

            final String switchKey = getString(com.lody.virtual
                    .R.string.alert_host_permission_denied_preference);
            final boolean alertHostPermissionDenied = PreferenceManager
                .getDefaultSharedPreferences(ctx)
                .getBoolean(switchKey, true);
            // Get preference using placeholder key
            final SwitchPreference alertSwitch = (SwitchPreference) findPreference("alert_switch");
            // Set the actual key
            alertSwitch.setKey(switchKey);
            // Update status with the actual preference
            alertSwitch.setChecked(alertHostPermissionDenied);

            final PreferenceCategory appsPreference = (PreferenceCategory) findPreference(
                    "applications_permissions");

            VUiKit.defer()
                .when(this::loadApps)
                .done(apps -> {
                    apps.forEach(app ->
                            appsPreference.addPreference(createAppPreference(app)));

                    // Create an invisible preference to show the divider under last element
                    final var phantomPreference = new Preference(ctx) {
                        @Override
                        public void onBindViewHolder(PreferenceViewHolder holder) {
                            super.onBindViewHolder(holder);
                            holder.setDividerAllowedAbove(true);
                            final var itemView = holder.itemView;
                            final var params = itemView.getLayoutParams();
                            params.height = 0;
                            itemView.setLayoutParams(params);
                        }
                    };
                    appsPreference.addPreference(phantomPreference);
                });
        }

        private Preference createAppPreference(final AppManageInfo app) {
            final CharSequence appName = app.getName();

            final AppPreference appPreference = new AppPreference(ctx, app);
            final int uid = VPackageManager.get().getPackageUid(app.pkgName, app.userId);

            appPreference.setKey("app_permission_preference_" + appName);
            appPreference.setTitle(appName);
            appPreference.setIcon(DEFAULT_APP_ICON);
            appPreference.setLayoutResource(R.layout.item_app_permission);
            appPreference.setOnPreferenceClickListener(preference -> {
                final Intent intent = new Intent(ctx, PermissionManageActivity.class);
                intent.putExtra(EXTRA_APP_NAME, appName);
                intent.putExtra(EXTRA_APP_UID, uid);
                intent.putExtra(EXTRA_PERMISSIONS_TYPE, PermissionGroup.class);
                startActivity(intent);
                return true;
            });

            return appPreference;
        }

        private List<AppManageInfo> loadApps() {
            final List<AppManageInfo> apps = new ArrayList<>();
            final List<InstalledAppInfo> installedApps = VirtualCore.get().getInstalledApps(0);
            final PackageManager packageManager = ctx.getPackageManager();
            for (final InstalledAppInfo installedApp : installedApps) {
                final int[] installedUsers = installedApp.getInstalledUsers();
                for (final int installedUser : installedUsers) {
                    final AppManageInfo info = new AppManageInfo();
                    info.userId = installedUser;
                    final ApplicationInfo appInfo = installedApp.getApplicationInfo(installedUser);
                    info.name = appInfo.loadLabel(packageManager);
                    info.pkgName = installedApp.packageName;
                    info.path = appInfo.sourceDir;
                    apps.add(info);
                }
            }
            return apps;
        }

        private class AppPreference extends Preference {

            private final Context ctx;
            private final AppManageInfo app;

            private PreferenceViewHolder holder;
            private ImageView iconView;

            public AppPreference(final Context ctx, final AppManageInfo app) {
                super(ctx);
                this.ctx = ctx;
                this.app = app;
            }

            @Override
            public void onBindViewHolder(final PreferenceViewHolder holder) {
                super.onBindViewHolder(holder);
                this.holder = holder;
                iconView = (ImageView) holder.itemView.findViewById(android.R.id.icon);

                holder.setDividerAllowedAbove(true);
                holder.setDividerAllowedBelow(true);

                final int defaultIcon = DEFAULT_APP_ICON;
                if (VirtualCore.get().isOutsideInstalled(app.pkgName)) {
                    GlideUtils.loadInstalledPackageIcon(ctx, app.pkgName, iconView, defaultIcon);
                } else {
                    GlideUtils.loadPackageIconFromApkFile(ctx, app.path, iconView, defaultIcon);
                }
            }
        }

    }
}
