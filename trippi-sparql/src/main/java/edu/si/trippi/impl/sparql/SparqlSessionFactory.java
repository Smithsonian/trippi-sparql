/*
 * Copyright 2015-2016 Smithsonian Institution.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.You may obtain a copy of
 * the License at: http://www.apache.org/licenses/
 *
 * This software and accompanying documentation is supplied without
 * warranty of any kind. The copyright holder and the Smithsonian Institution:
 * (1) expressly disclaim any warranties, express or implied, including but not
 * limited to any implied warranties of merchantability, fitness for a
 * particular purpose, title or non-infringement; (2) do not assume any legal
 * liability or responsibility for the accuracy, completeness, or usefulness of
 * the software; (3) do not represent that use of the software would not
 * infringe privately owned rights; (4) do not warrant that the software
 * is error-free or will be maintained, supported, updated or enhanced;
 * (5) will not be liable for any indirect, incidental, consequential special
 * or punitive damages of any kind or nature, including but not limited to lost
 * profits or loss of data, on any basis arising from contract, tort or
 * otherwise, even if any of the parties has been warned of the possibility of
 * such loss or damage.
 *
 * This distribution includes several third-party libraries, each with their own
 * license terms. For a complete copy of all copyright and license terms, including
 * those of third-party libraries, please see the product release notes.
 *
 */

package edu.si.trippi.impl.sparql;

import static org.apache.jena.query.QueryExecutionFactory.sparqlService;
import static org.apache.jena.update.UpdateExecutionFactory.createRemote;

import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.update.UpdateRequest;
import org.trippi.impl.base.TriplestoreSessionFactory;

import edu.si.trippi.impl.sparql.SparqlSession.ReadOnlySparqlSession;

/**
 * A {@link TriplestoreSessionFactory} using SPARQL Update.
 *
 * @author A. Soroka
 */
public class SparqlSessionFactory implements TriplestoreSessionFactory {

    static final String[] LANGUAGES = new String[] { "SPARQL" };

    private final Node graphName;

    private final boolean readOnly;

    private final Function<Query, QueryExecution> queryExecutor;
    private final Function<Query, QueryExecution> constructExecutor;
    private final Consumer<UpdateRequest> updateExecutor;

    /**
     * Full constructor.
     *
     * @param updateEndpoint the SPARQL Update endpoint against which to act
     * @param queryEndpoint the SPARQL Query endpoint against which to act for SELECT/ASK queries
     * @param constructEndpoint the SPARQL Query endpoint against which to act for CONSTRUCT/DESCRIBE queries
     * @param graphName the graphname to use when adding or deleting triples
     * @param readOnly whether this factory creates read-only sessions
     */
    public SparqlSessionFactory(final String updateEndpoint, final String queryEndpoint, final String constructEndpoint,
                    final Node graphName, final boolean readOnly) {
        this.updateExecutor = u -> createRemote(u, updateEndpoint).execute();
        this.queryExecutor = q -> sparqlService(queryEndpoint, q);
        this.constructExecutor = q -> sparqlService(constructEndpoint, q);

        this.graphName = graphName;
        this.readOnly = readOnly;
    }

    @Override
    public SparqlSession newSession() {
        return readOnly ? new ReadOnlySparqlSession(updateExecutor, queryExecutor, constructExecutor, graphName)
                        : new SparqlSession(updateExecutor, queryExecutor, constructExecutor, graphName);
    }

    @Override
    public String[] listTripleLanguages() {
        return LANGUAGES;
    }

    @Override
    public String[] listTupleLanguages() {
        return LANGUAGES;
    }

    @Override
    public void close() {
        // NO OP
    }
}
