package catan.services;

import catan.exception.PrivateCodeException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PrivateCodeUtilTest {
    PrivateCodeUtil privateCodeUtil;
    RandomValeGeneratorMock rvg;

    @Before
    public void setUp() {
        rvg = new RandomValeGeneratorMock();

        privateCodeUtil = new PrivateCodeUtil();
        privateCodeUtil.setRvg(rvg);
    }

    @Test
    public void resultHasSevenDigitsWhenAllGeneratesValuesAreZeroTest() {
        //Given
        rvg.setNextGeneratedValue(0.0);
        rvg.setNextGeneratedValue(0.0);
        rvg.setNextGeneratedValue(0.0);

        //When
        int privateCode = privateCodeUtil.generateRandomPrivateCode();

        //Then
        assertEquals(privateCode, 1010000);
    }

    @Test
    public void resultIsCorrectWhenFirstGeneratedValueIsMaximumTest() {
        //Given
        rvg.setNextGeneratedValue(0.99999999999);
        rvg.setNextGeneratedValue(0);
        rvg.setNextGeneratedValue(0);

        //When
        int privateCode = privateCodeUtil.generateRandomPrivateCode();

        //Then
        assertEquals(privateCode, 26010000);
    }

    @Test
    public void resultIsCorrectWhenSecondGeneratedValueIsMaximumTest() {
        //Given
        rvg.setNextGeneratedValue(0);
        rvg.setNextGeneratedValue(0.99999999999);
        rvg.setNextGeneratedValue(0);

        //When
        int privateCode = privateCodeUtil.generateRandomPrivateCode();

        //Then
        assertEquals(privateCode, 1260000);
    }

    @Test
    public void resultIsCorrectWhenThirdGeneratedValueIsMaximumTest() {
        //Given
        rvg.setNextGeneratedValue(0);
        rvg.setNextGeneratedValue(0);
        rvg.setNextGeneratedValue(0.99999999999);

        //When
        int privateCode = privateCodeUtil.generateRandomPrivateCode();

        //Then
        assertEquals(privateCode, 1019999);
    }

    @Test
    public void resultIsCorrectWhenThirdAndSecondGeneratedValueIsMaximumTest() {
        //Given
        rvg.setNextGeneratedValue(0);
        rvg.setNextGeneratedValue(0.99999999999);
        rvg.setNextGeneratedValue(0.99999999999);

        //When
        int privateCode = privateCodeUtil.generateRandomPrivateCode();

        //Then
        assertEquals(privateCode, 1269999);
    }

    @Test
    public void resultIsCorrectWhenThirdAndFirstGeneratedValueIsMaximumTest() {
        //Given
        rvg.setNextGeneratedValue(0.99999999999);
        rvg.setNextGeneratedValue(0);
        rvg.setNextGeneratedValue(0.99999999999);

        //When
        int privateCode = privateCodeUtil.generateRandomPrivateCode();

        //Then
        assertEquals(privateCode, 26019999);
    }

    @Test
    public void resultIsCorrectWhenFirstAndSecondGeneratedValuesIsMaximumTest() {
        //Given
        rvg.setNextGeneratedValue(0.99999999999);
        rvg.setNextGeneratedValue(0.99999999999);
        rvg.setNextGeneratedValue(0);

        //When
        int privateCode = privateCodeUtil.generateRandomPrivateCode();

        //Then
        assertEquals(privateCode, 26260000);
    }

    @Test
    public void resultLessThan26270000WhenGeneratedValuesAreMaximumTest() {
        //Given
        rvg.setNextGeneratedValue(0.99999999999);
        rvg.setNextGeneratedValue(0.99999999999);
        rvg.setNextGeneratedValue(0.99999999999);

        //When
        int privateCode = privateCodeUtil.generateRandomPrivateCode();

        //Then
        assertEquals(privateCode, 26269999);
    }

    @Test
    public void resultGraterThan1010000AndLessThan2627000WhenGeneratedValuesAreRandomTest() {
        //Given

        //When
        int privateCode = privateCodeUtil.generateRandomPrivateCode();

        //Then
        assertThat(privateCode, greaterThan(1010000));
        assertThat(privateCode, lessThan(26270000));
    }

    @Test
    public void displayValueIsAA0000WhenPrivateCodeIs1010000Test() {
        //Given
        int privateCode = 1010000;

        //When
        String privateCodeDisplayValue = privateCodeUtil.getDisplayValueFromPrivateCode(privateCode);

        //Then
        assertEquals("AA0000", privateCodeDisplayValue);
    }

    @Test
    public void displayValueIsZA0000WhenPrivateCodeIs25010000Test() {
        //Given
        int privateCode = 26010000;

        //When
        String privateCodeDisplayValue = privateCodeUtil.getDisplayValueFromPrivateCode(privateCode);

        //Then
        assertEquals("ZA0000", privateCodeDisplayValue);
    }

    @Test
    public void displayValueIsAZ0000WhenPrivateCodeIs1260000Test() {
        //Given
        int privateCode = 1260000;

        //When
        String privateCodeDisplayValue = privateCodeUtil.getDisplayValueFromPrivateCode(privateCode);

        //Then
        assertEquals("AZ0000", privateCodeDisplayValue);
    }

    @Test
    public void displayValueIsAA9999WhenPrivateCodeIs1019999Test() {
        //Given
        int privateCode = 1019999;

        //When
        String privateCodeDisplayValue = privateCodeUtil.getDisplayValueFromPrivateCode(privateCode);

        //Then
        assertEquals("AA9999", privateCodeDisplayValue);
    }

    @Test
    public void displayValueIsZA9999WhenPrivateCodeIs26019999Test() {
        //Given
        int privateCode = 26019999;

        //When
        String privateCodeDisplayValue = privateCodeUtil.getDisplayValueFromPrivateCode(privateCode);

        //Then
        assertEquals("ZA9999", privateCodeDisplayValue);
    }


    @Test
    public void displayValueIsZZ9999WhenPrivateCodeIs1269999Test() {
        //Given
        int privateCode = 1269999;

        //When
        String privateCodeDisplayValue = privateCodeUtil.getDisplayValueFromPrivateCode(privateCode);

        //Then
        assertEquals("AZ9999", privateCodeDisplayValue);
        assertThat((int) privateCodeDisplayValue.charAt(0), greaterThanOrEqualTo(65));
        assertThat((int) privateCodeDisplayValue.charAt(0), lessThanOrEqualTo(65));
        assertThat((int) privateCodeDisplayValue.charAt(1), greaterThanOrEqualTo(90));
        assertThat((int) privateCodeDisplayValue.charAt(1), lessThanOrEqualTo(90));
    }

    @Test
    public void displayValueIsZZ9999WhenPrivateCodeIs26269999Test() {
        //Given
        int privateCode = 26269999;

        //When
        String privateCodeDisplayValue = privateCodeUtil.getDisplayValueFromPrivateCode(privateCode);

        //Then
        assertEquals("ZZ9999", privateCodeDisplayValue);
    }

    @Test
    public void displayValueIsCorrectWhenPrivateCodeIsRandomTest() {
        //Given
        int privateCode = privateCodeUtil.generateRandomPrivateCode();

        //When
        String privateCodeDisplayValue = privateCodeUtil.getDisplayValueFromPrivateCode(privateCode);

        //Then
        assertThat(Integer.parseInt(privateCodeDisplayValue.substring(PrivateCodeUtil.LETTER_LENGTH)), greaterThanOrEqualTo(0));
        assertThat(Integer.parseInt(privateCodeDisplayValue.substring(PrivateCodeUtil.LETTER_LENGTH)), lessThanOrEqualTo(9999));
        assertThat((int) privateCodeDisplayValue.charAt(0), greaterThanOrEqualTo(65));
        assertThat((int) privateCodeDisplayValue.charAt(0), lessThanOrEqualTo(90));
        assertThat((int) privateCodeDisplayValue.charAt(1), greaterThanOrEqualTo(65));
        assertThat((int) privateCodeDisplayValue.charAt(1), lessThanOrEqualTo(90));
    }

    @Test
    public void generatedAndTransformedDisplayValueTransformsBackToPrivateCOdeCorrectly() throws PrivateCodeException {
        //Given
        int privateCode = privateCodeUtil.generateRandomPrivateCode();
        String privateCodeDisplayValue = privateCodeUtil.getDisplayValueFromPrivateCode(privateCode);

        //When
        int transformedPrivateCode = privateCodeUtil.getPrivateCodeFromDisplayValue(privateCodeDisplayValue);

        //Then
        assertEquals(privateCode, transformedPrivateCode);
    }

    @Test
    public void displayValueTransformedToPrivateCodeTransformsBackToDisplayValueCorrectly() throws PrivateCodeException {
        //Given
        int privateCode = privateCodeUtil.generateRandomPrivateCode();
        String privateCodeDisplayValue = privateCodeUtil.getDisplayValueFromPrivateCode(privateCode);
        int transformedPrivateCode = privateCodeUtil.getPrivateCodeFromDisplayValue(privateCodeDisplayValue);

        //When
        String transformedPrivateCodeDisplayValue = privateCodeUtil.getDisplayValueFromPrivateCode(transformedPrivateCode);

        //Then
        assertEquals(privateCodeDisplayValue, transformedPrivateCodeDisplayValue);
    }


    @Test
    public void privateCodeIs1010000WhenDisplayValueIsAA0000Test() throws PrivateCodeException {
        //Given
        String displayValue = "AA0000";

        //When
        int privateCode = privateCodeUtil.getPrivateCodeFromDisplayValue(displayValue);

        //Then
        assertEquals(1010000, privateCode);
    }

    @Test
    public void privateCodeIs26010000WhenDisplayValueIsZA0000Test() throws PrivateCodeException {
        //Given
        String displayValue = "ZA0000";

        //When
        int privateCode = privateCodeUtil.getPrivateCodeFromDisplayValue(displayValue);

        //Then
        assertEquals(26010000, privateCode);
    }

    @Test
    public void privateCodeIs1260000WhenDisplayValueIsAZ0000Test() throws PrivateCodeException {
        //Given
        String displayValue = "AZ0000";

        //When
        int privateCode = privateCodeUtil.getPrivateCodeFromDisplayValue(displayValue);

        //Then
        assertEquals(1260000, privateCode);
    }

    @Test
    public void privateCodeIs1019999WhenDisplayValueIsAA9999Test() throws PrivateCodeException {
        //Given
        String displayValue = "AA9999";

        //When
        int privateCode = privateCodeUtil.getPrivateCodeFromDisplayValue(displayValue);

        //Then
        assertEquals(1019999, privateCode);
    }

    @Test
    public void privateCodeIs26019999WhenDisplayValueIsZA9999Test() throws PrivateCodeException {
        //Given
        String displayValue = "ZA9999";

        //When
        int privateCode = privateCodeUtil.getPrivateCodeFromDisplayValue(displayValue);

        //Then
        assertEquals(26019999, privateCode);
    }

    @Test
    public void privateCodeIs1269999WhenDisplayValueIsAZ9999Test() throws PrivateCodeException {
        //Given
        String displayValue = "AZ9999";

        //When
        int privateCode = privateCodeUtil.getPrivateCodeFromDisplayValue(displayValue);

        //Then
        assertEquals(1269999, privateCode);
    }

    @Test
    public void privateCodeIs26269999WhenDisplayValueIsZZ9999Test() throws PrivateCodeException {
        //Given
        String displayValue = "ZZ9999";

        //When
        int privateCode = privateCodeUtil.getPrivateCodeFromDisplayValue(displayValue);

        //Then
        assertEquals(26269999, privateCode);
    }

    @Test
    public void lowerCasePrivateCodeIsConvertsCorrectlyTest() throws PrivateCodeException {
        //Given
        int privateCode = privateCodeUtil.generateRandomPrivateCode();
        String privateCodeDisplayValue = privateCodeUtil.getDisplayValueFromPrivateCode(privateCode);

        //When
        int transformedPrivateCode = privateCodeUtil.getPrivateCodeFromDisplayValue(privateCodeDisplayValue.toLowerCase());

        //Then
        assertEquals(privateCode, transformedPrivateCode);
    }

    @Test
    public void privateCodeTransformationFailsWhenValueTooShortTest() throws PrivateCodeException {
        //Given
        String displayValue = "12345";

        try {
            //When
            int privateCode = privateCodeUtil.getPrivateCodeFromDisplayValue(displayValue);
            fail("Should throw PrivateCodeException exception");
        } catch (PrivateCodeException e) {
            //Then
            assertEquals(PrivateCodeUtil.WRONG_LENGTH_ERROR, e.getErrorCode());
        }
    }

    @Test
    public void privateCodeTransformationFailsWhenValueTooLongTest() throws PrivateCodeException {
        //Given
        String displayValue = "1234567";

        try {
            //When
            int privateCode = privateCodeUtil.getPrivateCodeFromDisplayValue(displayValue);
            fail("Should throw PrivateCodeException exception");
        } catch (PrivateCodeException e) {
            //Then
            assertEquals(PrivateCodeUtil.WRONG_LENGTH_ERROR, e.getErrorCode());
        }
    }

    @Test
    public void privateCodeTransformationFailsWhenFirstSymbolsIsLowerThanLetterTest() throws PrivateCodeException {
        //Given
        String displayValue = "@A1234";

        try {
            //When
            int privateCode = privateCodeUtil.getPrivateCodeFromDisplayValue(displayValue);
            fail("Should throw PrivateCodeException exception");
        } catch (PrivateCodeException e) {
            //Then
            assertEquals(PrivateCodeUtil.FIRST_SYMBOLS_ARE_NOT_LETTERS_ERROR, e.getErrorCode());
        }
    }

    @Test
    public void privateCodeTransformationFailsWhenFirstSymbolsIsHigherThanLetterTest() throws PrivateCodeException {
        //Given
        String displayValue = "[A1234";

        try {
            //When
            int privateCode = privateCodeUtil.getPrivateCodeFromDisplayValue(displayValue);
            fail("Should throw PrivateCodeException exception");
        } catch (PrivateCodeException e) {
            //Then
            assertEquals(PrivateCodeUtil.FIRST_SYMBOLS_ARE_NOT_LETTERS_ERROR, e.getErrorCode());
        }
    }

    @Test
    public void privateCodeTransformationFailsWhenSecondSymbolsIsLowerThanLetterTest() throws PrivateCodeException {
        //Given
        String displayValue = "A@1234";

        try {
            //When
            int privateCode = privateCodeUtil.getPrivateCodeFromDisplayValue(displayValue);
            fail("Should throw PrivateCodeException exception");
        } catch (PrivateCodeException e) {
            //Then
            assertEquals(PrivateCodeUtil.FIRST_SYMBOLS_ARE_NOT_LETTERS_ERROR, e.getErrorCode());
        }
    }

    @Test
    public void privateCodeTransformationFailsWhenSecondSymbolsIsHigherThanLetterTest() throws PrivateCodeException {
        //Given
        String displayValue = "A[1234";

        try {
            //When
            int privateCode = privateCodeUtil.getPrivateCodeFromDisplayValue(displayValue);
            fail("Should throw PrivateCodeException exception");
        } catch (PrivateCodeException e) {
            //Then
            assertEquals(PrivateCodeUtil.FIRST_SYMBOLS_ARE_NOT_LETTERS_ERROR, e.getErrorCode());
        }
    }

    @Test
    public void privateCodeTransformationFailsWhenWrongSymbolsInsteadOfDigitsTest() throws PrivateCodeException {
        //Given
        String displayValue = "abcdef";

        try {
            //When
            int privateCode = privateCodeUtil.getPrivateCodeFromDisplayValue(displayValue);
            fail("Should throw PrivateCodeException exception");
        } catch (PrivateCodeException e) {
            //Then
            assertEquals(PrivateCodeUtil.SYMBOLS_ARE_NOT_DIGITS_ERROR, e.getErrorCode());
        }
    }

}