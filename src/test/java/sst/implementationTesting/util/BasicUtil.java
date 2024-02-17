package sst.implementationTesting.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class BasicUtil {
    @Test
    private void test_duplicated() {
        List<Integer> data = List.of(1, 2, 2, 3, 4, 5, 5);
        List<Integer> duplicatedResult = sst.util.BasicUtil.duplicated(data);
        Assert.assertEquals(
                duplicatedResult,
                List.of(2, 6)
        );
    }
}
