package org.irmacard.androidmanagement;

/**
 * Interface that should be supported by any activity including the SettingsFragment.
 * 
 * @author Wouter Lueks <lueks@cs.ru.nl>
 *
 */
public interface SettingsFragmentActivityI {
	public void onChangeCardPIN();
	public void onChangeCredPIN();
}
