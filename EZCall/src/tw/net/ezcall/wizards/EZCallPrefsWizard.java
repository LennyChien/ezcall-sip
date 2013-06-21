/**
 * Copyright (C) 2010-2012 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of CSipSimple.
 *
 *  CSipSimple is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  If you own a pjsip commercial license you can also redistribute it
 *  and/or modify it under the terms of the GNU Lesser General Public License
 *  as an android library.
 *
 *  CSipSimple is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 */

package tw.net.ezcall.wizards;

import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import tw.net.ezcall.R;
import tw.net.ezcall.api.SipManager;
import tw.net.ezcall.api.SipProfile;
import tw.net.ezcall.db.DBProvider;
import tw.net.ezcall.models.Filter;
import tw.net.ezcall.ui.filters.AccountFilters;
import tw.net.ezcall.ui.prefs.GenericPrefs;
import tw.net.ezcall.utils.Log;
import tw.net.ezcall.utils.PreferencesWrapper;
import tw.net.ezcall.wizards.WizardUtils.WizardInfo;

import java.util.List;

public class EZCallPrefsWizard extends GenericPrefs {

	private static final String THIS_FILE = "EZCall Prefs wizard";

	protected SipProfile account = null;
	private Button regButton;
	private String wizardId = "";
	private EZCallWizardIface wizard = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Get back the concerned account and if any set the current (if not a
		// new account is created)
		Intent intent = getIntent();
		long accountId = intent.getLongExtra(SipProfile.FIELD_ID,
				SipProfile.INVALID_ID);

		// TODO : ensure this is not null...
		setWizardId(intent.getStringExtra(SipProfile.FIELD_WIZARD));

		account = SipProfile.getProfileFromDbId(this, accountId,
				DBProvider.ACCOUNT_FULL_PROJECTION);

		super.onCreate(savedInstanceState);

		// Bind buttons to their actions
		/*
		 * Button bt = (Button) findViewById(R.id.cancel_bt);
		 * bt.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { setResult(RESULT_CANCELED,
		 * getIntent()); finish(); } });
		 */

		regButton = (Button) findViewById(R.id.refill_btn);
		regButton.setText("註冊");
		regButton.setEnabled(false);
		regButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveAndFinish();
			}
		});
		wizard.fillLayout(account);
	}

	private boolean isResumed = false;

	@Override
	protected void onResume() {
		super.onResume();
		isResumed = true;
		updateDescriptions();
		updateValidation();
		wizard.onStart();
	}

	@Override
	protected void onPause() {
		super.onPause();
		isResumed = false;
		wizard.onStop();
	}

	private boolean setWizardId(String wId) {
		if (wizardId == null) {
			return setWizardId(WizardUtils.EXPERT_WIZARD_TAG);
		}

		WizardInfo wizardInfo = WizardUtils.getWizardClass(wId);
		if (wizardInfo == null) {
			if (!wizardId.equals(WizardUtils.EXPERT_WIZARD_TAG)) {
				return setWizardId(WizardUtils.EXPERT_WIZARD_TAG);
			}
			return false;
		}

		try {
			wizard = (EZCallWizardIface) wizardInfo.classObject.newInstance();
		} catch (IllegalAccessException e) {
			Log.e(THIS_FILE, "Can't access wizard class", e);
			if (!wizardId.equals(WizardUtils.EXPERT_WIZARD_TAG)) {
				return setWizardId(WizardUtils.EXPERT_WIZARD_TAG);
			}
			return false;
		} catch (InstantiationException e) {
			Log.e(THIS_FILE, "Can't access wizard class", e);
			if (!wizardId.equals(WizardUtils.EXPERT_WIZARD_TAG)) {
				return setWizardId(WizardUtils.EXPERT_WIZARD_TAG);
			}
			return false;
		}
		wizardId = wId;
		wizard.setParent(this);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setIcon(
					WizardUtils.getWizardIconRes(wizardId));
		}
		return true;
	}

	@Override
	protected void beforeBuildPrefs() {
		// Use our custom wizard view
		setContentView(R.layout.wizard_prefs_ezcall);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (isResumed) {
			updateDescriptions();
			updateValidation();
		}
	}

	/**
	 * Update validation state of the current activity. It will check if wizard
	 * can be saved and if so will enable button
	 */
	public void updateValidation() {
		regButton.setEnabled(wizard.canSave());
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// menu.findItem(SAVE_MENU).setVisible(wizard.canSave());

		return super.onPrepareOptionsMenu(menu);
	}

	private static final int CHOOSE_WIZARD = 0;
	private static final int MODIFY_FILTERS = CHOOSE_WIZARD + 1;

	private static final int FINAL_ACTIVITY_CODE = MODIFY_FILTERS;

	private int currentActivityCode = FINAL_ACTIVITY_CODE;

	public int getFreeSubActivityCode() {
		currentActivityCode++;
		return currentActivityCode;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CHOOSE_WIZARD && resultCode == RESULT_OK
				&& data != null && data.getExtras() != null) {
			String wizardId = data.getStringExtra(WizardUtils.ID);
			if (wizardId != null) {
				saveAccount(wizardId);
				setResult(RESULT_OK, getIntent());
				finish();
			}
		}

		if (requestCode > FINAL_ACTIVITY_CODE) {
			wizard.onActivityResult(requestCode, resultCode, data);
		}
	}

	/**
	 * Save account and end the activity
	 */
	public void saveAndFinish() {
		saveAccount();
		Intent intent = getIntent();
		setResult(RESULT_OK, intent);
		finish();
	}

	/*
	 * Save the account with current wizard id
	 */
	private void saveAccount() {
		saveAccount(wizardId);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		getSharedPreferences(WIZARD_PREF_NAME, MODE_PRIVATE).edit().clear()
				.commit();
	}

	/**
	 * Save the account with given wizard id
	 * 
	 * @param wizardId
	 *            the wizard to use for account entry
	 */
	private void saveAccount(String wizardId) {
		boolean needRestart = false;

		PreferencesWrapper prefs = new PreferencesWrapper(
				getApplicationContext());
		account = wizard.buildAccount(account);
		account.wizard = wizardId;
		if (account.id == SipProfile.INVALID_ID) {
			// This account does not exists yet
			prefs.startEditing();
			wizard.setDefaultParams(prefs);
			prefs.endEditing();
			Uri uri = getContentResolver().insert(SipProfile.ACCOUNT_URI,
					account.getDbContentValues());

			// After insert, add filters for this wizard
			account.id = ContentUris.parseId(uri);
			List<Filter> filters = wizard.getDefaultFilters(account);
			if (filters != null) {
				for (Filter filter : filters) {
					// Ensure the correct id if not done by the wizard
					filter.account = (int) account.id;
					getContentResolver().insert(SipManager.FILTER_URI,
							filter.getDbContentValues());
				}
			}
			// Check if we have to restart
			needRestart = wizard.needRestart();

		} else {
			// TODO : should not be done there but if not we should add an
			// option to re-apply default params
			prefs.startEditing();
			wizard.setDefaultParams(prefs);
			prefs.endEditing();
			getContentResolver().update(
					ContentUris.withAppendedId(SipProfile.ACCOUNT_ID_URI_BASE,
							account.id), account.getDbContentValues(), null,
					null);
		}

		// Mainly if global preferences were changed, we have to restart sip
		// stack
		if (needRestart) {
			Intent intent = new Intent(SipManager.ACTION_SIP_REQUEST_RESTART);
			sendBroadcast(intent);
		}
	}

	@Override
	protected int getXmlPreferences() {
		return wizard.getBasePreferenceResource();
	}

	@Override
	protected void updateDescriptions() {
		wizard.updateDescriptions();
	}

	@Override
	protected String getDefaultFieldSummary(String fieldName) {
		return wizard.getDefaultFieldSummary(fieldName);
	}

	private static final String WIZARD_PREF_NAME = "Wizard";

	@Override
	public SharedPreferences getSharedPreferences(String name, int mode) {
		return super.getSharedPreferences(WIZARD_PREF_NAME, mode);
	}

	
}
