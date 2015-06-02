package fr.lirmm.graphik.graal.core;

import org.junit.Assert;
import org.junit.Test;

public class PredicateTest {

    @Test
    public void constructorTest() {
        String label = "label";
        int arity = 5;
        Predicate predicate = new Predicate(label, arity);

		Assert.assertTrue(predicate.getIdentifier().equals(label));
        Assert.assertTrue(predicate.getArity() == arity);
    }

    @Test
    public void equalsTest() {
        String label = "label";
        int arity = 5;
        Predicate predicate = new Predicate(label, arity);

        Assert.assertTrue("Predicate not equals itself",
                predicate.equals(predicate));

        Predicate other = new Predicate(label, arity);
        Assert.assertTrue("Predicate not equals an other predicate",
                predicate.equals(other));
    }
}