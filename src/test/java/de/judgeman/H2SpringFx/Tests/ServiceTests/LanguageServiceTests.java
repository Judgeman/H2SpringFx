package de.judgeman.H2SpringFx.Tests.ServiceTests;

import de.judgeman.H2SpringFx.Services.LanguageService;
import de.judgeman.H2SpringFx.Services.SettingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
class LanguageServiceTests {

	@Autowired
	private LanguageService languageService;

	@Autowired
	private SettingService settingService;

	@Test
	public void SaveAndLoadLanguageTest() {
		Locale localeToSave = Locale.CANADA;
		languageService.saveNewLanguageSetting(localeToSave);
		Locale locale = languageService.getLastUsedOrDefaultLanguage();

		Assertions.assertEquals(locale.toLanguageTag(), localeToSave.toLanguageTag());
		Assertions.assertNotEquals(locale.toLanguageTag(), languageService.getDefaultLanguage().toLanguageTag());
	}

	@Test
	public void LoadDefaultLanguageTest() {
		// delete the saved language
		// this operation is important because getLastUsedOrDefaultLanguage load the last saved language
		// from the database if one test persists a new language setting than this test will be failed without this
		boolean savedLanguageDeleted = settingService.deleteSetting(SettingService.LANGUAGE_ENTRY_KEY);
		System.out.println("saved language deleted: " + savedLanguageDeleted);

		Locale defaultLanguage = languageService.getDefaultLanguage();

		// load default language if no database entries available
		Locale loadedLocal = languageService.getLastUsedOrDefaultLanguage();

		Assertions.assertEquals(loadedLocal.toLanguageTag(), defaultLanguage.toLanguageTag());
		Assertions.assertEquals(languageService.getDefaultLanguage().toLanguageTag(), defaultLanguage.toLanguageTag());

		// ---------------------------------

		// load saved language and not default language if database entry available
		Locale localeToSave = Locale.CANADA;
		languageService.saveNewLanguageSetting(localeToSave);

		loadedLocal = languageService.getLastUsedOrDefaultLanguage();

		Assertions.assertNotEquals(loadedLocal.toLanguageTag(), defaultLanguage.toLanguageTag());

		// ---------------------------------

		// load default language if the saved language is "broken"
		localeToSave = new Locale("");
		languageService.saveNewLanguageSetting(localeToSave);

		loadedLocal = languageService.getLastUsedOrDefaultLanguage();
		Assertions.assertEquals(loadedLocal.toLanguageTag(), defaultLanguage.toLanguageTag());
	}

	@Test
	public void getCurrentUsedResourceBundleTest() {
		// ---------------------------------
		// load default resource bundle if no one is set
		languageService.setCurrentUsedResourceBundle(null);

		ResourceBundle currentResourceBundle = languageService.getCurrentUsedResourceBundle();
		ResourceBundle defaultResourceBundle = languageService.getDefaultResourceBundle();

		Assertions.assertNotNull(currentResourceBundle);
		Assertions.assertEquals(currentResourceBundle, defaultResourceBundle);

		// ---------------------------------
		// set other resource bundle
		ResourceBundle otherResourceBundle = ResourceBundle.getBundle("test_resource_bundle", Locale.CANADA);
		languageService.setCurrentUsedResourceBundle(otherResourceBundle);

		currentResourceBundle = languageService.getCurrentUsedResourceBundle();
		Assertions.assertEquals(currentResourceBundle, otherResourceBundle);
	}

	@Test
	public void getLastUsedResourceBundleTest() {
		ResourceBundle defaultResourceBundle = languageService.getDefaultResourceBundle();
		languageService.saveNewLanguageSetting(defaultResourceBundle.getLocale());

		languageService.setCurrentUsedResourceBundle(null);

		ResourceBundle currentResourceBundle = languageService.getCurrentUsedResourceBundle();
		Assertions.assertEquals(currentResourceBundle, defaultResourceBundle);
	}

	@Test
	public void setNewLanguageTest() {
		Locale englishLocal = Locale.US;
		languageService.setNewLanguage(englishLocal);

		Assertions.assertEquals("English", languageService.getLocalizationText("setNewLanguageTestValue"));

		Locale germanLocal = Locale.GERMANY;
		languageService.setNewLanguage(germanLocal);

		Assertions.assertEquals("Deutsch", languageService.getLocalizationText("setNewLanguageTestValue"));
	}

	@Test
	public void getAvailableLanguagesTest() {
		ArrayList<Locale> availableLanguages = languageService.getAvailableLanguages();

		Assertions.assertTrue(availableLanguages.contains(Locale.GERMANY));
		Assertions.assertTrue(availableLanguages.contains(Locale.US));
	}

	@Test
	public void lastUsedLanguageIsNotExistingTest() {
		Locale localeToSave = Locale.ITALIAN;
		languageService.saveNewLanguageSetting(localeToSave);

		languageService.setCurrentUsedResourceBundle(null);
		ResourceBundle resourceBundle = languageService.getCurrentUsedResourceBundle();
		Assertions.assertNotEquals(localeToSave, resourceBundle.getLocale());
		Assertions.assertEquals(languageService.getDefaultResourceBundle().getLocale(), resourceBundle.getLocale());
	}
}
