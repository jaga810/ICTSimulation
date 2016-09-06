package ictsimulationpackage;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BuildingListTest {
	BuildingList bldgList = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		bldgList = new BuildingList(102);
	}

	@After
	public void tearDown() throws Exception {
		bldgList = null;
	}

	@Test
	public void test() {
	}
}
