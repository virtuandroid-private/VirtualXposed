package io.virtualapp.settings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.android.launcher3.LauncherFiles;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.VActivityManager;

import io.virtualapp.R;
import io.virtualapp.VCommends;
import io.virtualapp.abs.ui.VActivity;
import io.virtualapp.home.ListAppActivity;
import io.virtualapp.utils.Misc;

/**
 * Settings activity for Launcher. Currently implements the following setting: Allow rotation
 */
public class SettingsActivity extends VActivity {

    private static final String ADD_APP_PREFERENCE = "settings_add_app";
    private static final String MODULE_MANAGE_PREFERENCE = "settings_module_manage";
    private static final String RECOMMEND_PLUGIN = "settings_plugin_recommend";
    private static final String ADVANCED_SETTINGS_PREFERENCE = "settings_advanced";
    private static final String PERMISSION_MANAGE_PREFERENCE = "settings_permission_manage";
    private static final String APP_MANAGE_PREFERENCE = "settings_app_manage";
    private static final String TASK_MANAGE_PREFERENCE = "settings_task_manage";
    private static final String FAQ_SETTINGS_PREFERENCE = "settings_faq";
    private static final String DONATE_PREFERENCE = "settings_donate";
    private static final String ABOUT_PREFERENCE = "settings_about";
    private static final String REBOOT_PREFERENCE = "settings_reboot";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Display the fragment as the main content.
            getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
        }
    }

    /**
     * This fragment shows the launcher preferences.
     */
    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceManager().setSharedPreferencesName(LauncherFiles.SHARED_PREFERENCES_KEY);
            addPreferencesFromResource(R.xml.settings_preferences);

            findPreference(ADD_APP_PREFERENCE).setOnPreferenceClickListener(preference -> {
                ListAppActivity.gotoListApp(getActivity());
                return false;
            });

            final Preference moduleManage = findPreference(MODULE_MANAGE_PREFERENCE);
            moduleManage.setOnPreferenceClickListener(preference -> {
                try {
                    Intent t = new Intent();
                    t.setComponent(new ComponentName("de.robv.android.xposed.installer",
                                "de.robv.android.xposed.installer.WelcomeActivity"));
                    t.putExtra("fragment", 1);
                    int ret = VActivityManager.get().startActivity(t, 0);
                    if (ret < 0) {
                        Toast.makeText(getActivity(), R.string.xposed_installer_not_found,
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Throwable ignored) {
                    ignored.printStackTrace();
                }
                return false;
            });

            final Preference recommend = findPreference(RECOMMEND_PLUGIN);
            recommend.setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(getActivity(), RecommendPluginActivity.class));
                return false;
            });

            findPreference(ADVANCED_SETTINGS_PREFERENCE)
                .setOnPreferenceClickListener(preference -> {
                    startActivity(new Intent(getActivity(), AdvancedSettingsActivity.class));
                    return true;
                });

            findPreference(PERMISSION_MANAGE_PREFERENCE)
                .setOnPreferenceClickListener(preference -> {
                    startActivity(new Intent(getActivity(), PermissionsActivity.class));
                    return true;
                });

            findPreference(APP_MANAGE_PREFERENCE).setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(getActivity(), AppManageActivity.class));
                return false;
            });

            findPreference(TASK_MANAGE_PREFERENCE).setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(getActivity(), TaskManageActivity.class));
                return false;
            });

            findPreference(DONATE_PREFERENCE).setOnPreferenceClickListener(preference -> {
                Misc.showDonate(getActivity());
                return false;
            });

            findPreference(FAQ_SETTINGS_PREFERENCE).setOnPreferenceClickListener(preference -> {
                Uri uri = Uri.parse("https://github.com/android-hacker/VAExposed/wiki/FAQ");
                Intent t = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(t);
                return false;
            });

            findPreference(ABOUT_PREFERENCE).setOnPreferenceClickListener(preference -> {
                startActivity(new Intent(getActivity(), AboutActivity.class));
                return false;
            });

            findPreference(REBOOT_PREFERENCE).setOnPreferenceClickListener(preference -> {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.settings_reboot_title)
                        .setMessage(getResources().getString(R.string.settings_reboot_content))
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            VirtualCore.get().killAllApps();
                            Toast.makeText(getActivity(), R.string.reboot_tips_1,
                                    Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .create();
                try {
                    alertDialog.show();
                } catch (Throwable ignored) {
                }
                return false;
            });

            boolean xposedEnabled = VirtualCore.get().isXposedEnabled();
            if (!xposedEnabled) {
                getPreferenceScreen().removePreference(moduleManage);
                getPreferenceScreen().removePreference(recommend);
            }
        }

        private static void dismiss(ProgressDialog dialog) {
            try {
                dialog.dismiss();
            } catch (Throwable ignored) {
            }
        }

        @Override
        public void startActivity(Intent intent) {
            try {
                super.startActivity(intent);
            } catch (Throwable ignored) {
                Toast.makeText(getActivity(), "startActivity failed.", Toast.LENGTH_SHORT).show();
                ignored.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VCommends.REQUEST_SELECT_APP) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }
}
