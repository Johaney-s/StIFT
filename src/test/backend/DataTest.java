
package backend;

import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Test cases for Data class
 */
public class DataTest {

    /**
     * Test of addStar method, of class Data.
     */
    @Test
    public void testAddStar() {
        System.out.println("addStar");
        Data simpleData = new Data();
        Star star5 = new Star(1.6, 5.0, 3.0, 4.0, 5.0, 6.0);
        simpleData.addStar(star5);
        assert(simpleData.getGroupedData().get(5.0).size() == 1);
        assertEquals(simpleData.getGroupedData().get(5.0).get(0), star5);
    }

    /**
     * Test of intersection method, of class Data.
     */
    @Test
    public void testIntersection() {
        System.out.println("intersection");
        Data simpleData = new Data();
        Star star1 = new Star(1.0, 0.7, 3.0, 4.0, 1.0, 6.0);
        Star star3 = new Star(2.0, 0.8, 3.0, 4.0, 1.0, 6.0);
        simpleData.addStar(star1);
        simpleData.addStar(star3);
        double[] expectedResult = {1.5, 0.75};
        Assert.assertArrayEquals(expectedResult, simpleData.intersection(star1, star3, 1.5, 0.0), 0.00001);
        double[] expectedResult2 = {1.0, 0.7};
        Assert.assertArrayEquals(expectedResult2, simpleData.intersection(star1, star3, 1.0, 20.0), 0.00001);
    }
    
    /**
    * Test of findNearestStars method, of class Data.
    */
    @Test
    public void testFindNearestStars() {
        System.out.println("findNearestStars");
        Data simpleData = new Data();
        Star star1 = new Star(1.0, 0.7, 3.0, 4.0, 1.0, 6.0);
        Star star2 = new Star(1.5, 4.0, 3.0, 4.0, 3.0, 6.0);
        Star star3 = new Star(2.0, 0.8, 3.0, 4.0, 1.0, 6.0);
        Star star4 = new Star(2.5, 4.2, 3.0, 4.0, 3.0, 6.0);
        simpleData.addStar(star1);
        simpleData.addStar(star2);
        simpleData.addStar(star3);
        simpleData.addStar(star4);
        Star[] expectedResult = {star2, star4, star3, star1};
        Assert.assertArrayEquals(expectedResult, simpleData.findNearestStars(1.7, 2.0));
        Star star5 = new Star(3.0, 0.9, 3.0, 4.0, 1.0, 6.0);
        Star star6 = new Star(3.5, 4.24, 3.0, 4.0, 3.0, 6.0);
        simpleData.addStar(star5);
        simpleData.addStar(star6);
        Assert.assertArrayEquals(expectedResult, simpleData.findNearestStars(1.7, 2.0));
        Star[] expectedResult2 = {star4, star6, star5, star3};
        Assert.assertArrayEquals(expectedResult2, simpleData.findNearestStars(2.99, 4.205));
    }
}
