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
package at.tugraz.ist.catroid.test.content.brick;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.PointToBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;

public class PointToBrickTest extends AndroidTestCase {

	public void testPointTo() {

		Project project = new Project(null, "testProject");

		Sprite sprite1 = new Sprite("cat1");
		Script startScript1 = new StartScript("script1", sprite1);
		PlaceAtBrick placeAt1 = new PlaceAtBrick(sprite1, 300, 400);
		SetSizeToBrick size1 = new SetSizeToBrick(sprite1, 20.0);
		startScript1.addBrick(placeAt1);
		startScript1.addBrick(size1);

		Sprite sprite2 = new Sprite("cat2");
		Script startScript2 = new StartScript("script2", sprite2);
		PlaceAtBrick placeAt2 = new PlaceAtBrick(sprite2, -400, -300);
		SetSizeToBrick size2 = new SetSizeToBrick(sprite2, 20.0);
		startScript2.addBrick(placeAt2);
		startScript2.addBrick(size2);
		sprite2.addScript(startScript2);

		PointToBrick pointToBrick = new PointToBrick(sprite1, sprite2);
		startScript1.addBrick(pointToBrick);
		sprite1.addScript(startScript1);

		sprite2.startStartScripts();
		sprite1.startStartScripts();

		project.addSprite(sprite1);
		project.addSprite(sprite2);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite1);
		ProjectManager.getInstance().setCurrentScript(startScript1);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		assertEquals("Wrong direction", -135.0, sprite1.getDirection(), 1e-3);
	}

}