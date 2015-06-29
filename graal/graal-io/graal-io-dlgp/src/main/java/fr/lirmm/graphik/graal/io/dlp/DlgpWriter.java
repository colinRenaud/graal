/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.io.dlp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.NegativeConstraint;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.core.term.Literal;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.core.term.Term.Type;
import fr.lirmm.graphik.graal.io.AbstractGraalWriter;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.PrefixManager;
import fr.lirmm.graphik.util.URI;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class DlgpWriter extends AbstractGraalWriter {
	
	private PrefixManager pm;
	private Predicate top = Predicate.TOP;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Write into the standard output. Warning, if you close this object, you
	 * will close the standard output.
	 */
	public DlgpWriter() {
		this(new OutputStreamWriter(System.out));
	}
	
	public DlgpWriter(OutputStream out) {
		this(new OutputStreamWriter(out));
	}
	
	public DlgpWriter(Writer out) {
		super(out);
		this.pm = new PrefixManager();
	}
	
	public DlgpWriter(File file) throws IOException {
		this(new FileWriter(file));
	}
	
	/**
	 * Write into a file specified by the path file.
	 * 
	 * @param path
	 *            the file path
	 * @throws IOException
	 */
	public DlgpWriter(String path) throws IOException {
		 this(new FileWriter(path));
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	public void writeDirective(Directive d) throws IOException {
		switch (d.getType()) {
		case UNA:
			this.write("@una");
			break;
		case BASE:
			this.write("@base ");
			this.write(d.getText());
			break;
		case TOP:
			this.write("@top ");
			this.write(d.getText());
			this.top = new Predicate(d.getText(), 1);
			break;
		case COMMENT:
			this.write("%% ");
			this.write(d.getText());
			break;
		}
		this.write("\n");
	}

	@Override
	public void writeComment(String comment) throws IOException {
		this.write("% ");
		this.writeln(comment);
	}

	@Override
	public void write(AtomSet atomset) throws IOException {
		this.writeAtomSet(atomset, true);
		this.writeln(".");
	}

	public void write(RuleSet ruleset) throws IOException {
		for (Rule r : ruleset) {
			this.write(r);
		}
	}
	
	@Override
	public void write(Atom atom) throws IOException {
		this.writeAtom(atom);
		this.write(".\n");
	}

	@Override
	public void write(Rule rule) throws IOException {
		this.writeLabel(rule.getLabel());

		this.writeAtomSet(rule.getHead(), false);
		this.write(" :- ");
		this.writeAtomSet(rule.getBody(), false);
		this.write(".\n");
	}
	
	public void write(NegativeConstraint constraint) throws IOException {
		this.writeLabel(constraint.getLabel());
		
		this.write(" ! :- ");
		this.writeAtomSet(constraint.getBody(), false);
		this.write(".\n");
	}

	public void write(Query query) throws IOException {
		if (query instanceof ConjunctiveQuery) {
			this.write((ConjunctiveQuery)query);
		}
		else if (query instanceof Iterable) {
			for (Object q : (Iterable<?>)query) {
				if (q instanceof ConjunctiveQuery) {
					this.write((ConjunctiveQuery)q);
				}
			}
		}
	}
	
	@Override
	public void write(ConjunctiveQuery query) throws IOException {
		if(!query.getLabel().isEmpty()) {
			this.writeLabel(query.getLabel());
		}
		this.write('?');
		Collection<Term> avars = query.getAnswerVariables();
		if(!avars.isEmpty()) {
			boolean isFirst = true;
			this.write('(');
			for(Term t: avars) {
				if(isFirst) {
					isFirst = false;
				} else {
					this.write(',');
				} 
				
				this.writeTerm(t);
			}
			this.write(')');
		}
		this.write(" :- ");
		this.writeAtomSet(query.getAtomSet(), false);
		this.write(".\n");
	}
	
	@Override
	public void write(Prefix prefix) throws IOException {
		this.pm.putPrefix(prefix);
		this.write("@prefix ");
		this.write(prefix.getPrefixName());
		this.write(": <");
		this.write(prefix.getPrefix());
		this.write(">\n");
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	protected void writeLabel(String label) throws IOException {
		if(!label.isEmpty()) {
			this.write("[");
			this.write(label);
			this.write("] ");
		}
	}
	
	protected void writeAtomSet(Iterable<Atom> atomSet, boolean addCarriageReturn) throws IOException {
		boolean isFirst = true;
		for(Atom a : atomSet) {
			if(isFirst) {
				isFirst = false;
			} else {
				this.write(", ");
				if(addCarriageReturn)
					this.write('\n');
			}
			
			this.writeAtom(a);
		}
	}
	
	@Override
	protected void writeStandardAtom(Atom atom) throws IOException {
		this.writePredicate(atom.getPredicate());
		this.write('(');
		
		boolean isFirst = true;
		for(Term t : atom.getTerms()) {
			if(isFirst) {
				isFirst = false;
			} else {
				this.write(", ");
			}
			
			this.writeTerm(t);
			
			
		}
		this.write(')');
	}

	@Override
	protected void writeEquality(Term term, Term term2) throws IOException {
		this.writeTerm(term);
		this.write(" = ");
		this.writeTerm(term2);
	}

	@Override
	protected void writeBottom() throws IOException {
		this.write("!");
	}

	protected void writeTerm(Term t) throws IOException {
		if(Type.VARIABLE.equals(t.getType())) {
			this.writeUpperIdentifier(t.getIdentifier());
		} else if(Type.CONSTANT.equals(t.getType())) {
			this.writeLowerIdentifier(t.getIdentifier());
		} else { // LITERAL
			Literal l = (Literal) t;
			this.write('"');
			this.write(l.getValue().toString());
			this.write("\"^^<");
			this.write(l.getDatatype().toString());
			this.write('>');
		}
	}

	protected void writePredicate(Predicate p) throws IOException {
		if (p.equals(Predicate.TOP)) {
			p = top;
		}
		this.writeLowerIdentifier(p.getIdentifier());
	}

	protected void writeURI(URI uri) throws IOException {
		Prefix prefix = this.pm.getPrefixByValue(uri.getPrefix());
		if(prefix == null) {
			this.write('<');
			this.write(uri.toString());
			this.write('>');
		} else {
			this.write(prefix.getPrefixName() + ":"
					+ uri.getLocalname());
		}
	}

	/**
	 * @param string
	 * @throws IOException
	 */
	private void writeLowerIdentifier(Object identifier) throws IOException {

		if (identifier instanceof URI) {
			this.writeURI((URI) identifier);
		} else {
			String s = identifier.toString();
			char first = s.charAt(0);
			if (onlySimpleChar(s) && first >= 'a' && first <= 'z') {
				this.write(s);
			} else {
				this.write('<');
				this.write(s);
				this.write('>');
			}
		}
	}

	/**
	 * @param identifier
	 * @throws IOException
	 */
	private void writeUpperIdentifier(Object identifier) throws IOException {
		String s = identifier.toString();
		char first = s.charAt(0);
		if (first < 'A' || first > 'Z') {
			s = "VAR_" + s;
		}
		s = s.replaceAll("[^a-zA-Z0-9_]", "_");
		this.write(s);
	}

	// //////////////////////////////////////////////////////////////////////////
	// STATIC METHODS
	// //////////////////////////////////////////////////////////////////////////

	public static String writeToString(Object o) {
		StringWriter s = new StringWriter();
		DlgpWriter w = new DlgpWriter(s);
		try {
			w.write(o);
			w.close();
		} catch (IOException e) {

		}
		return s.toString();
	}

	/**
	 * Check if the string contains only simple char (a-z A-Z 0-9 -)
	 * 
	 * @param s
	 * @return
	 */
	private static boolean onlySimpleChar(String s) {
		char c;
		for (int i = 0; i < s.length(); ++i) {
			c = s.charAt(i);
			if (!(c >= 'a' && c <= 'z') && !(c >= 'A' && c <= 'Z')
					&& !(c >= '0' && c <= '9') && !(c == '_')) {
				return false;
			}
		}
		return true;
	}
	
};