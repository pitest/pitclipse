import org.junit.Assert;
import org.junit.Test;

public class ClassInDefaultPackageTest {

	@Test
	public void testOne() {
		Assert.assertEquals(1, new ClassInDefaultPackage().one());
	}
}
