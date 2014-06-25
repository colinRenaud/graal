/**
 * 
 */
package fr.lirmm.graphik.graal.solver;

import fr.lirmm.graphik.graal.core.ConjunctiveQueriesUnion;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.store.rdbms.IRdbmsStore;
import fr.lirmm.graphik.graal.transformation.ReadOnlyTransformStore;
import fr.lirmm.graphik.graal.transformation.TransformatorSolver;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class DefaultSolverFactory extends SolverFactory {

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.lirmm.graphik.alaska.solver.SolverFactory#getSolver(fr.lirmm.graphik
     * .kb.IQuery, fr.lirmm.graphik.kb.IAtomSet)
     */
    @Override
    public Solver getSolver(Query query, ReadOnlyAtomSet atomSet)
            throws SolverFactoryException {
        if (query instanceof ConjunctiveQuery) {
            if (atomSet instanceof IRdbmsStore)
                return new SqlSolver((ConjunctiveQuery) query,
                        (IRdbmsStore) atomSet);
            if (atomSet instanceof ReadOnlyTransformStore)
            	return new TransformatorSolver((ConjunctiveQuery) query, atomSet);
            else
                return new RecursiveBacktrackSolver((ConjunctiveQuery) query,
                        atomSet);
            
        } else if (query instanceof ConjunctiveQueriesUnion) {
            if (atomSet instanceof IRdbmsStore)
                return new SqlConjunctiveQueriesUnionSolver((ConjunctiveQueriesUnion) query, (IRdbmsStore) atomSet);
            if (atomSet instanceof ReadOnlyTransformStore)
            	return null;
            else
                return new ConjunctiveQueriesUnionSolver((ConjunctiveQueriesUnion) query, atomSet);
            
        } else {
            throw new SolverFactoryException("No solver for this kind of query");
        }
    }

}
