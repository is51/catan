package catan.services.util.random;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

public class RandomUtilTest {
    RandomUtil randomUtil;
    RandomValueGeneratorMock rvg;

    @Before
    public void setUp() {
        rvg = new RandomValueGeneratorMock();

        randomUtil = new RandomUtil();
        randomUtil.setRvg(rvg);
    }

    @Test
    public void resultHasSevenDigitsWhenAllGeneratesValuesAreZeroTest() {
        //Given
        rvg.setNextGeneratedValue(0.0);
        rvg.setNextGeneratedValue(0.0);
        rvg.setNextGeneratedValue(0.0);

        //When
        String privateCode = randomUtil.generateRandomPrivateCode(4);

        //Then
        assertEquals(privateCode, "AA1000");
    }

    @Test
    public void resultIsCorrectWhenFirstGeneratedValueIsMaximumTest() {
        //Given
        rvg.setNextGeneratedValue(0.99999999999);
        rvg.setNextGeneratedValue(0);
        rvg.setNextGeneratedValue(0);

        //When
        String privateCode = randomUtil.generateRandomPrivateCode(4);

        //Then
        assertEquals(privateCode, "ZA1000");
    }

    @Test
    public void resultIsCorrectWhenSecondGeneratedValueIsMaximumTest() {
        //Given
        rvg.setNextGeneratedValue(0);
        rvg.setNextGeneratedValue(0.99999999999);
        rvg.setNextGeneratedValue(0);

        //When
        String privateCode = randomUtil.generateRandomPrivateCode(4);

        //Then
        assertEquals(privateCode, "AZ1000");
    }

    @Test
    public void resultIsCorrectWhenThirdGeneratedValueIsMaximumTest() {
        //Given
        rvg.setNextGeneratedValue(0);
        rvg.setNextGeneratedValue(0);
        rvg.setNextGeneratedValue(0.99999999999);

        //When
        String privateCode = randomUtil.generateRandomPrivateCode(4);

        //Then
        assertEquals(privateCode, "AA9999");
    }

    @Test
    public void resultIsCorrectWhenThirdAndSecondGeneratedValueIsMaximumTest() {
        //Given
        rvg.setNextGeneratedValue(0);
        rvg.setNextGeneratedValue(0.99999999999);
        rvg.setNextGeneratedValue(0.99999999999);

        //When
        String privateCode = randomUtil.generateRandomPrivateCode(4);

        //Then
        assertEquals(privateCode, "AZ9999");
    }

    @Test
    public void resultIsCorrectWhenThirdAndFirstGeneratedValueIsMaximumTest() {
        //Given
        rvg.setNextGeneratedValue(0.99999999999);
        rvg.setNextGeneratedValue(0);
        rvg.setNextGeneratedValue(0.99999999999);

        //When
        String privateCode = randomUtil.generateRandomPrivateCode(4);

        //Then
        assertEquals(privateCode, "ZA9999");
    }

    @Test
    public void resultIsCorrectWhenFirstAndSecondGeneratedValuesIsMaximumTest() {
        //Given
        rvg.setNextGeneratedValue(0.99999999999);
        rvg.setNextGeneratedValue(0.99999999999);
        rvg.setNextGeneratedValue(0);

        //When
        String privateCode = randomUtil.generateRandomPrivateCode(4);

        //Then
        assertEquals(privateCode, "ZZ1000");
    }

    @Test
    public void resultIsCorrectWhenAllGeneratedValuesIsMaximumTest() {
        //Given
        rvg.setNextGeneratedValue(0.99999999999);
        rvg.setNextGeneratedValue(0.99999999999);
        rvg.setNextGeneratedValue(0.99999999999);

        //When
        String privateCode = randomUtil.generateRandomPrivateCode(4);

        //Then
        assertEquals(privateCode, "ZZ9999");
    }

    @Test
    public void sizeOfResultIs7WhenPassedParameterIs5Test() {
        //Given
        int numberOfDigits = 5;
        rvg.setNextGeneratedValue(0.99999999999);
        rvg.setNextGeneratedValue(0.99999999999);
        rvg.setNextGeneratedValue(0.99999999999);

        //When
        String privateCode = randomUtil.generateRandomPrivateCode(numberOfDigits);

        //Then
        assertEquals(privateCode, "ZZ99999");
    }

    @Test
    public void sizeOfResultIs5WhenPassedParameterIs3Test() {
        //Given
        int numberOfDigits = 3;
        rvg.setNextGeneratedValue(0.99999999999);
        rvg.setNextGeneratedValue(0.99999999999);
        rvg.setNextGeneratedValue(0.99999999999);

        //When
        String privateCode = randomUtil.generateRandomPrivateCode(numberOfDigits);

        //Then
        assertEquals(privateCode, "ZZ999");
    }

    @Test
    public void resultGraterThan1010000AndLessThan2627000WhenGeneratedValuesAreRandomTest() {
        //Given

        //When
        String privateCode = randomUtil.generateRandomPrivateCode(4);

        //Then
        assertThat(privateCode.length(), equalTo(6));
    }
}