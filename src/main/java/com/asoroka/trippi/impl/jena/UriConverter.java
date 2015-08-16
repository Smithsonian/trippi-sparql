package com.asoroka.trippi.impl.jena;

import static java.net.URI.create;
import static org.apache.jena.graph.NodeFactory.createURI;
import static org.trippi.impl.RDFFactories.createResource;

import org.apache.jena.graph.Node_URI;
import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.URIReference;

public class UriConverter extends NodeConverter<URIReference, Node_URI> {

    @Override
    protected Node_URI doForward(final URIReference uri) {
        return (Node_URI) createURI(uri.toString());
    }

    @Override
    protected URIReference doBackward(final Node_URI uri) {
        try {
            return createResource(create(uri.getURI()));
        } catch (final GraphElementFactoryException e) {
            throw new GraphElementFactoryRuntimeException(e);
        }
    }
}
