/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.uiespresso.ui.fragment;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewActions;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;
import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;
import static org.catrobat.catroid.utils.PathBuilder.buildScenePath;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class RenameSoundTest {

	@Rule
	public BaseActivityInstrumentationRule<SpriteActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION,
			SpriteActivity.FRAGMENT_SOUNDS);

	private String oldSoundName = "oldSoundName";
	private String newSoundName = "newSoundName";
	private String secondSoundName = "secondSoundName";

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void renameSoundTest() {
		RecyclerViewActions.openOverflowMenu();
		onView(withText(R.string.rename)).perform(click());

		onRecyclerView().atPosition(0)
				.performCheckItem();

		onView(withId(R.id.confirm)).perform(click());

		onView(withText(R.string.rename_sound_dialog)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		onView(allOf(withText(oldSoundName), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(newSoundName));
		closeSoftKeyboard();

		onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.perform(click());

		onView(withText(newSoundName)).check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void cancelRenameSoundTest() {
		RecyclerViewActions.openOverflowMenu();
		onView(withText(R.string.rename)).perform(click());

		onRecyclerView().atPosition(0)
				.performCheckItem();

		onView(withId(R.id.confirm)).perform(click());

		onView(withText(R.string.rename_sound_dialog)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		closeSoftKeyboard();

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
				.perform(click());

		onView(withText(oldSoundName)).check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void invalidInputRenameSoundTest() {
		RecyclerViewActions.openOverflowMenu();
		onView(withText(R.string.rename)).perform(click());

		onRecyclerView().atPosition(0)
				.performCheckItem();

		onView(withId(R.id.confirm)).perform(click());

		onView(withText(R.string.rename_sound_dialog)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		String emptyInput = "";
		String spacesOnlyInput = "   ";

		onView(allOf(withText(oldSoundName), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(emptyInput));
		closeSoftKeyboard();

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(allOf(isDisplayed(), not(isEnabled()))));

		onView(allOf(withText(emptyInput), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(spacesOnlyInput));

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(allOf(isDisplayed(), not(isEnabled()))));

		onView(allOf(withText(spacesOnlyInput), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(secondSoundName));

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(allOf(isDisplayed(), not(isEnabled()))));

		onView(allOf(withText(secondSoundName), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(newSoundName));

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(allOf(isDisplayed(), isEnabled())));
	}

	private void createProject() throws IOException {
		String projectName = "renameSoundFragmentTest";
		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);

		Sprite sprite = new SingleSprite("testSprite");
		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentlyEditedScene(project.getDefaultScene());
		ProjectManager.getInstance().setCurrentSprite(sprite);
		XstreamSerializer.getInstance().saveProject(project);

		File soundFile0 = ResourceImporter.createSoundFileFromResourcesInDirectory(
				InstrumentationRegistry.getContext().getResources(),
				org.catrobat.catroid.test.R.raw.longsound,
				new File(buildScenePath(project.getName(), project.getDefaultScene().getName()), SOUND_DIRECTORY_NAME),
				"longsound.mp3");

		List<SoundInfo> soundInfoList = ProjectManager.getInstance().getCurrentSprite().getSoundList();
		SoundInfo soundInfo0 = new SoundInfo();
		soundInfo0.setFile(soundFile0);
		soundInfo0.setName(oldSoundName);
		soundInfoList.add(soundInfo0);

		File soundFile1 = StorageOperations.duplicateFile(soundFile0);
		SoundInfo soundInfo1 = new SoundInfo();
		soundInfo1.setFile(soundFile1);
		soundInfo1.setName(secondSoundName);
		soundInfoList.add(soundInfo1);
	}
}
