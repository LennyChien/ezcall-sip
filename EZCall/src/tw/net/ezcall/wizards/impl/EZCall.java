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

package tw.net.ezcall.wizards.impl;

import android.text.InputType;

import tw.net.ezcall.api.SipConfigManager;
import tw.net.ezcall.api.SipProfile;
import tw.net.ezcall.utils.PreferencesWrapper;

public class EZCall extends SimpleImplementation {
	

	@Override
	protected String getDomain() {
		return "voip2.ttinet.com.tw";
	}
	
	@Override
	protected String getDefaultName() {
		return "EZCall";
	}

	//Customization
	@Override
	public void fillLayout(final SipProfile account) {
		super.fillLayout(account);
		
		accountUsername.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
		
	}
	
	@Override
	public SipProfile buildAccount(SipProfile account) {
	    SipProfile acc = super.buildAccount(account);
	    //acc.reg_timeout = 900;
	    return acc;
	}
	
	@Override
    public void setDefaultParams(PreferencesWrapper prefs) {
        super.setDefaultParams(prefs);
        // Stun
        //prefs.setPreferenceBooleanValue(SipConfigManager.ENABLE_STUN, true);
        //prefs.addStunServer("stun01.1worldtelecom.mobi");
        //prefs.addStunServer("stun02.1worldtelecom.mobi");
        
        // User agent -- useful?
        //prefs.setPreferenceStringValue(SipConfigManager.USER_AGENT, "1WorldVoip");

        // Codecs -- Assume they have legal rights to provide g729 to each users
        // As they activate it by default in their forked app.
        // For Narrowband
        prefs.setCodecPriority("PCMU/8000/1", SipConfigManager.CODEC_NB, "100");
        prefs.setCodecPriority("PCMA/8000/1", SipConfigManager.CODEC_NB, "150");
        prefs.setCodecPriority("speex/8000/1", SipConfigManager.CODEC_NB, "0");
        prefs.setCodecPriority("speex/16000/1", SipConfigManager.CODEC_NB, "0");
        prefs.setCodecPriority("speex/32000/1", SipConfigManager.CODEC_NB, "0");
        prefs.setCodecPriority("GSM/8000/1", SipConfigManager.CODEC_NB, "0");
        prefs.setCodecPriority("G722/16000/1", SipConfigManager.CODEC_NB, "0");
        prefs.setCodecPriority("G729/8000/1", SipConfigManager.CODEC_NB, "200");
        prefs.setCodecPriority("iLBC/8000/1", SipConfigManager.CODEC_NB, "250"); /*
                                                                                * Disable
                                                                                * by
                                                                                * default
                                                                                */
        prefs.setCodecPriority("SILK/8000/1", SipConfigManager.CODEC_NB, "0");
        prefs.setCodecPriority("SILK/12000/1", SipConfigManager.CODEC_NB, "0");
        prefs.setCodecPriority("SILK/16000/1", SipConfigManager.CODEC_NB, "0");
        prefs.setCodecPriority("SILK/24000/1", SipConfigManager.CODEC_NB, "0");
        prefs.setCodecPriority("CODEC2/8000/1", SipConfigManager.CODEC_NB, "0");
        prefs.setCodecPriority("G7221/16000/1", SipConfigManager.CODEC_NB, "0");
        prefs.setCodecPriority("G7221/32000/1", SipConfigManager.CODEC_NB, "0");
        prefs.setCodecPriority("ISAC/16000/1", SipConfigManager.CODEC_NB, "0");
        prefs.setCodecPriority("ISAC/32000/1", SipConfigManager.CODEC_NB, "0");
        prefs.setCodecPriority("AMR/8000/1", SipConfigManager.CODEC_NB, "0");

        // For Wideband
        prefs.setCodecPriority("PCMU/8000/1", SipConfigManager.CODEC_WB, "100");
        prefs.setCodecPriority("PCMA/8000/1", SipConfigManager.CODEC_WB, "150");
        prefs.setCodecPriority("speex/8000/1", SipConfigManager.CODEC_WB, "0");
        prefs.setCodecPriority("speex/16000/1", SipConfigManager.CODEC_WB, "0");
        prefs.setCodecPriority("speex/32000/1", SipConfigManager.CODEC_WB, "0");
        prefs.setCodecPriority("GSM/8000/1", SipConfigManager.CODEC_WB, "0");
        prefs.setCodecPriority("G722/16000/1", SipConfigManager.CODEC_WB, "0");
        prefs.setCodecPriority("G729/8000/1", SipConfigManager.CODEC_WB, "200");
        prefs.setCodecPriority("iLBC/8000/1", SipConfigManager.CODEC_WB, "250"); /*
                                                                                * Disable
                                                                                * by
                                                                                * default
                                                                                */
        prefs.setCodecPriority("SILK/8000/1", SipConfigManager.CODEC_WB, "0");
        prefs.setCodecPriority("SILK/12000/1", SipConfigManager.CODEC_WB, "0");
        prefs.setCodecPriority("SILK/16000/1", SipConfigManager.CODEC_WB, "0");
        prefs.setCodecPriority("SILK/24000/1", SipConfigManager.CODEC_WB, "0");
        prefs.setCodecPriority("CODEC2/8000/1", SipConfigManager.CODEC_WB, "0");
        prefs.setCodecPriority("G7221/16000/1", SipConfigManager.CODEC_WB, "0");
        prefs.setCodecPriority("G7221/32000/1", SipConfigManager.CODEC_WB, "0");
        prefs.setCodecPriority("ISAC/16000/1", SipConfigManager.CODEC_WB, "0");
        prefs.setCodecPriority("ISAC/32000/1", SipConfigManager.CODEC_WB, "0");
        prefs.setCodecPriority("AMR/8000/1", SipConfigManager.CODEC_WB, "0");
    }
	
}
