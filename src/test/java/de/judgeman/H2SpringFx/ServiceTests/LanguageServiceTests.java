package de.judgeman.H2SpringFx.ServiceTests;

import de.judgeman.H2SpringFx.Services.LanguageService;
import de.judgeman.H2SpringFx.Services.SettingService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Locale;

@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
class LanguageServiceTests {

	@Autowired
	private LanguageService languageService;

	@Autowired
	private SettingService settingService;

	@Test
	public void SaveAndLoadLanguage() {
		Locale localeToSave = Locale.CANADA;
		languageService.saveNewLanguageSetting(localeToSave);
		Locale locale = languageService.getLastUsedOrDefaultLanguage();

		Assert.assertEquals(locale.toLanguageTag(), localeToSave.toLanguageTag());
		Assert.assertNotEquals(locale.toLanguageTag(), languageService.getDefaultLanguage().toLanguageTag());
	}

	@Test
	public void LoadDefaultLanguage() {
		// delete the saved language
		// this operation is important because getLastUsedOrDefaultLanguage load the last saved language
		// from the database if one test persists a new language setting than this test will be failed without this
		boolean savedLanguageDeleted = settingService.deleteSetting(SettingService.LANGUAGE_ENTRY_KEY);
		System.out.println("saved language deleted: " + savedLanguageDeleted);

		Locale defaultLanguage = languageService.getDefaultLanguage();

		// load default language if no database entries available
		Locale loadedLocal = languageService.getLastUsedOrDefaultLanguage();

		Assert.assertEquals(loadedLocal.toLanguageTag(), defaultLanguage.toLanguageTag());
		Assert.assertEquals(languageService.getDefaultLanguage().toLanguageTag(), defaultLanguage.toLanguageTag());

		// ---------------------------------

		// load saved language and not default language if database entry available
		Locale localeToSave = Locale.CANADA;
		languageService.saveNewLanguageSetting(localeToSave);

		loadedLocal = languageService.getLastUsedOrDefaultLanguage();

		Assert.assertNotEquals(loadedLocal.toLanguageTag(), defaultLanguage.toLanguageTag());
	}
}
