package com.javaone.onepet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

public class FragForPetSetting extends PreferenceFragment implements OnSharedPreferenceChangeListener {

    private EditTextPreference name;
    private ListPreference sex;
    private EditTextPreference level;
    private ListPreference character;
    //private CheckBoxPreference start;
    //private PreferenceScreen notice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        initPreferences();
    }

    private void initPreferences() {
        name = (EditTextPreference)findPreference("name_key");
        sex = (ListPreference)findPreference("sex_key");
        level = (EditTextPreference)findPreference("level_key");
        character = (ListPreference)findPreference("character_key");
        //start = (CheckBoxPreference)findPreference("start_key");
        //notice = (PreferenceScreen)findPreference("notice_key");
        //notice.setOnPreferenceClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Setup the initial values
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();

        // Set up a listener whenever a key changes
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    /*
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return true;
    }*/

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PetDBOperator dbmanager = new PetDBOperator(getActivity());

        if (key.equals("name_key")) {
            name.setSummary(sharedPreferences.getString(key, ""));
            /*
            Toast.makeText(getActivity(), sharedPreferences.getString(key, ""), Toast.LENGTH_LONG).show();
            dbmanager.update("self", "name", sharedPreferences.getString(key, ""));
            Toast.makeText(getActivity(), dbmanager.getPet().name, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            */
        }
        else if(key.equals("sex_key")) {
            sex.setSummary(sharedPreferences.getString(key, ""));
            Toast.makeText(getActivity(), sharedPreferences.getString(key, ""), Toast.LENGTH_LONG).show();
            /*if(sharedPreferences.getString(key, "").equals("男"))
                dbmanager.update("self", "sex", "1");
            else
                dbmanager.update("self", "sex", "0");
            */
        }
        else if(key.equals("level_key")) {
            level.setSummary(sharedPreferences.getString(key, ""));
            /*
            Toast.makeText(getActivity(), sharedPreferences.getString(key, ""), Toast.LENGTH_LONG).show();
            dbmanager.update("self", "sex", sharedPreferences.getString(key, ""));
            */
        }
        else if(key.equals("character_key")) {
            character.setSummary(sharedPreferences.getString(key, ""));
            /*
            Toast.makeText(getActivity(), sharedPreferences.getString(key, ""), Toast.LENGTH_LONG).show();
            dbmanager.update("self", "character", sharedPreferences.getString(key, ""));
            */
        }
    }

    /*
	private void openSetting()
	{
		try
		{
			Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
			startActivity(intent);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		//判断是哪个Preference被点击了
		if(preference.getKey().equals("notice_key"))
		{
			openSetting();
		}
		else if(preference.getKey().equals("start")) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
		else
		{
			return false;
		}
		return true;
	}*/
}
