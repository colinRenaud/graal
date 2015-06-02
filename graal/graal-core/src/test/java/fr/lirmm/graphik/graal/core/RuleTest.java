/**
 * 
 */
package fr.lirmm.graphik.graal.core;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.core.factory.RuleFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class RuleTest {

	private static Predicate predicate = new Predicate("pred", 2);

	private static Atom atom1, atom2, atom3;
	static {
		Term[] terms = new Term[2];
		terms[0] = DefaultTermFactory.instance().createVariable("X");
		terms[1] = DefaultTermFactory.instance().createConstant("a");
		atom1 = new DefaultAtom(predicate, Arrays.asList(terms));
		terms = new Term[2];
		terms[0] = DefaultTermFactory.instance().createVariable("X");
		terms[1] = DefaultTermFactory.instance().createVariable("Y");
		atom2 = new DefaultAtom(predicate, Arrays.asList(terms));
		terms = new Term[2];
		terms[0] = DefaultTermFactory.instance().createConstant("b");
		terms[1] = DefaultTermFactory.instance().createVariable("Y");
		atom3 = new DefaultAtom(predicate, Arrays.asList(terms));
	}

	@Test
	public void piecesTest1() {
		Rule rule = RuleFactory.getInstance().createRule();
		rule.getHead().add(atom1);
		rule.getHead().add(atom2);
		rule.getHead().add(atom3);
		Assert.assertEquals(1, rule.getPieces().size());
	}
	
	@Test
	public void piecesTest2() {
		Rule rule = RuleFactory.getInstance().createRule();
		rule.getBody().add(atom2);
		rule.getHead().add(atom1);
		rule.getHead().add(atom2);
		rule.getHead().add(atom3);
		Assert.assertEquals(3, rule.getPieces().size());
	}
	
	@Test
	public void piecesTest3() {
		Rule rule = RuleFactory.getInstance().createRule();
		rule.getHead().add(atom3);
		rule.getHead().add(atom3);
		rule.getHead().add(atom3);
		Assert.assertEquals(1, rule.getPieces().size());
	}
}