package fr.lirmm.graphik.obda.parser.misc;

import java.io.Reader;
import java.io.IOException;
import java.io.Serializable;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.util.stream.ObjectReader;

/**
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface StringFormat extends Serializable {
	
	String toString(Atom atom);
	
	Atom parseAtom(String s);
	AtomSet parse(String s);
	ObjectReader<Atom> parse(Reader reader);
	
	Atom readAtom(Reader reader) throws IOException;
	
	/** return the Atom separator in this representation */
	String getAtomSeparator();
	
}
