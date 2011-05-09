/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.test.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.FileChecksumContainer;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.ChangeXByBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeYByBrick;
import at.tugraz.ist.catroid.content.bricks.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.bricks.GoNStepsBackBrick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.IfStartedBrick;
import at.tugraz.ist.catroid.content.bricks.IfTouchedBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
import at.tugraz.ist.catroid.content.bricks.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetXBrick;
import at.tugraz.ist.catroid.content.bricks.SetYBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.util.Utils;

public class MediaPathTest extends InstrumentationTestCase {

	private static final int IMAGE_FILE_ID = at.tugraz.ist.catroid.test.R.raw.icon;
	private static final int SOUND_FILE_ID = at.tugraz.ist.catroid.test.R.raw.testsound;
	private static final int BIGBLUE_ID = at.tugraz.ist.catroid.test.R.raw.bigblue;
	private Project project;
	private File testImage;
	private File bigBlue;
	private File testSound;
	private File testImageCopy;
	private File testImageCopy2;
	private File testSoundCopy;

	private File bigBlue2;
	private File bigBlue3;

	private String imageName = "testImage.png";
	private String soundName = "testSound.mp3";
	private String projectName = "testProject7";
	private String bigBlueName = "bigblue.png";

	@Override
	protected void setUp() throws Exception {

		Utils.clearProject(projectName);

		project = new Project(getInstrumentation().getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		Project mockProject = new Project(getInstrumentation().getTargetContext(), "mockProject");
		StorageHandler.getInstance().saveProject(mockProject);

		testImage = Utils.saveFileToProject(mockProject.getName(), imageName, IMAGE_FILE_ID, getInstrumentation()
				.getContext(),
				Utils.TYPE_IMAGE_FILE);

		bigBlue = Utils.saveFileToProject(mockProject.getName(), bigBlueName, BIGBLUE_ID, getInstrumentation()
				.getContext(),
				Utils.TYPE_IMAGE_FILE);

		testSound = Utils.saveFileToProject(mockProject.getName(), soundName, SOUND_FILE_ID, getInstrumentation()
				.getContext(),
				Utils.TYPE_SOUND_FILE);

		//copy files with the Storagehandler copy function
		testImageCopy = StorageHandler.getInstance().copyImage(projectName, testImage.getAbsolutePath());
		testImageCopy2 = StorageHandler.getInstance().copyImage(projectName, testImage.getAbsolutePath());
		testSoundCopy = StorageHandler.getInstance().copySoundFile(testSound.getAbsolutePath());

	}

	@Override
	protected void tearDown() throws Exception {

		Utils.clearProject(projectName);
	}

	public void testPathsInSpfFile() throws IOException {
		createProjectWithAllBricksAndMediaFiles();
		String spf = StorageHandler.getInstance().getProjectfileAsString(projectName);

		assertFalse("project contains DEFAULT_ROOT", spf.contains(Consts.DEFAULT_ROOT));
		assertFalse("project contains IMAGE_DIRECTORY", spf.contains(Consts.IMAGE_DIRECTORY));
		assertFalse("project contains SOUND_DIRECTORY", spf.contains(Consts.SOUND_DIRECTORY));
		assertFalse("project contains sdcard/", spf.contains("sdcard/"));
	}

	public void testFilenameChecksum() throws IOException {

		createProjectWithAllBricksAndMediaFiles();

		String spf = StorageHandler.getInstance().getProjectfileAsString(projectName);

		String checksumImage = StorageHandler.getInstance().getMD5Checksum(testImageCopy);
		String checksumSound = StorageHandler.getInstance().getMD5Checksum(testSoundCopy);

		String expectedImagename = checksumImage + "_" + imageName;
		String expectedSoundname = checksumSound + "_" + soundName;

		assertTrue("expected image name not in spf", spf.contains(expectedImagename));
		assertTrue("expected sound name not in spf", spf.contains(expectedSoundname));

		String expectedImagenameTags = ">" + checksumImage + "_" + imageName + "<";
		String expectedSoundnameTags = ">" + checksumSound + "_" + soundName + "<";

		assertTrue("unexpected imagename", spf.contains(expectedImagenameTags));
		assertTrue("unexpected soundname", spf.contains(expectedSoundnameTags));

		String unexpectedImagenameTags = ">" + imageName + "<";
		String unexpectedSoundnameTags = ">" + soundName + "<";
		assertFalse("the imagename has no checksum", spf.contains(unexpectedImagenameTags));
		assertFalse("the soundname has no checksum", spf.contains(unexpectedSoundnameTags));

		StorageHandler storage = StorageHandler.getInstance();
		assertEquals("the copy does not equal the original image", storage.getMD5Checksum(testImage),
				storage.getMD5Checksum(testImageCopy));
		assertEquals("the copy does not equal the original image", storage.getMD5Checksum(testImage),
				storage.getMD5Checksum(testImageCopy2));
		assertEquals("the copy does not equal the original image", storage.getMD5Checksum(testSound),
				storage.getMD5Checksum(testSoundCopy));

		File directory = new File(Consts.DEFAULT_ROOT + "/" + projectName + Consts.IMAGE_DIRECTORY);
		File[] filesImage = directory.listFiles();

		//nomedia file is also in images folder
		assertEquals("Wrong amount of files in folder", 2, filesImage.length);
	}

	public void testCopyLargeImage() throws IOException, InterruptedException {
		StorageHandler storage = StorageHandler.getInstance();
		bigBlue2 = storage.copyImage(projectName, bigBlue.getAbsolutePath());
		bigBlue3 = storage.copyImage(projectName, bigBlue.getAbsolutePath());
		createProjectWithAllBricksAndMediaFiles();

		File directory = new File(Consts.DEFAULT_ROOT + "/" + projectName + Consts.IMAGE_DIRECTORY);
		File[] filesImage = directory.listFiles();

		//nomedia file is also in images folder
		assertEquals("Wrong amount of files in folder", 3, filesImage.length);
		assertNotSame("The image was not downsized", storage.getMD5Checksum(bigBlue), storage.getMD5Checksum(bigBlue2));
		assertEquals("The copies are not the same", bigBlue2.hashCode(), bigBlue3.hashCode());
	}

	private void createProjectWithAllBricksAndMediaFiles() throws IOException {
		Sprite sprite = new Sprite("testSprite");
		Script script = new Script("testScript", sprite);
		Script touchedScript = new Script("touchedScript", sprite);
		sprite.getScriptList().add(script);
		sprite.getScriptList().add(touchedScript);
		project.getSpriteList().add(sprite);

		ArrayList<Brick> brickList1 = new ArrayList<Brick>();
		ArrayList<Brick> brickList2 = new ArrayList<Brick>();
		brickList1.add(new ChangeXByBrick(sprite, 4));
		brickList1.add(new ChangeYByBrick(sprite, 5));
		brickList1.add(new ComeToFrontBrick(sprite));
		brickList1.add(new GoNStepsBackBrick(sprite, 5));
		brickList1.add(new HideBrick(sprite));
		brickList1.add(new IfStartedBrick(sprite, script));

		SetCostumeBrick costumeBrick = new SetCostumeBrick(sprite);

		costumeBrick.setCostume(testImageCopy.getName());

		PlaySoundBrick soundBrick = new PlaySoundBrick(sprite);
		soundBrick.setPathToSoundfile(testSoundCopy.getName());

		StorageHandler handler = StorageHandler.getInstance();
		FileChecksumContainer container = ProjectManager.getInstance().fileChecksumContainer;

		container.addChecksum(handler.getMD5Checksum(testImageCopy), testImageCopy.getAbsolutePath());
		container.addChecksum(handler.getMD5Checksum(testImageCopy2), testImageCopy2.getAbsolutePath());
		container.addChecksum(handler.getMD5Checksum(testSoundCopy), testSoundCopy.getAbsolutePath());

		if ((bigBlue2 != null) && (bigBlue3 != null)) {
			container.addChecksum(handler.getMD5Checksum(bigBlue2), bigBlue2.getAbsolutePath());
			container.addChecksum(handler.getMD5Checksum(bigBlue3), bigBlue3.getAbsolutePath());
		}

		brickList2.add(new IfTouchedBrick(sprite, touchedScript));
		brickList2.add(new PlaceAtBrick(sprite, 50, 50));
		brickList2.add(soundBrick);
		brickList2.add(new ScaleCostumeBrick(sprite, 50));
		brickList2.add(costumeBrick);
		brickList2.add(new SetXBrick(sprite, 50));
		brickList2.add(new SetYBrick(sprite, 50));
		brickList2.add(new ShowBrick(sprite));
		brickList2.add(new WaitBrick(sprite, 1000));

		for (Brick brick : brickList1) {
			script.addBrick(brick);
		}
		for (Brick brick : brickList2) {
			touchedScript.addBrick(brick);
		}

		StorageHandler.getInstance().saveProject(project);
	}

}