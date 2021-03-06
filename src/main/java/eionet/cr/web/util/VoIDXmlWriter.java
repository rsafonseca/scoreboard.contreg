/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Content Registry 3
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency. Portions created by TripleDev or Zero Technologies are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 *        Juhan Voolaid
 */

package eionet.cr.web.util;

import java.io.OutputStream;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.StringUtils;

import eionet.cr.dto.UploadDTO;

// TODO: Auto-generated Javadoc
/**
 * VoID xml writer.
 *
 * @author Juhan Voolaid
 */
public class VoIDXmlWriter {

    /** The Constant ENCODING. */
    private static final String ENCODING = "UTF-8";

    /** The Constant RDF_NS. */
    private static final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    /** The Constant RDF_NS_PREFIX. */
    private static final String RDF_NS_PREFIX = "rdf";

    /** The Constant RDFS_NS. */
    private static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";

    /** The Constant RDFS_NS_PREFIX. */
    private static final String RDFS_NS_PREFIX = "rdfs";

    /** The Constant OWL_NS. */
    private static final String OWL_NS = "http://www.w3.org/2002/07/owl#";

    /** The Constant OWL_NS_PREFIX. */
    private static final String OWL_NS_PREFIX = "owl";

    /** The Constant DCT_NS. */
    private static final String DCT_NS = "http://purl.org/dc/terms/";

    /** The Constant DCT_NS_PREFIX. */
    private static final String DCT_NS_PREFIX = "dct";

    /** The Constant VOID_NS. */
    private static final String VOID_NS = "http://rdfs.org/ns/void#";

    /** The Constant VOID_NS_PREFIX. */
    private static final String VOID_NS_PREFIX = "void";

    /** The Constant ROOT_ELEMENT. */
    private static final String ROOT_ELEMENT = "RDF";

    /** The context root. */
    private String contextRoot;

    /**
     * XMLWriter to write XML to.
     */
    private XMLStreamWriter writer = null;

    /**
     *
     * Class constructor.
     *
     * @param out
     * @param contextRoot
     * @throws XMLStreamException
     */
    public VoIDXmlWriter(OutputStream out, String contextRoot) throws XMLStreamException {
        writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out, ENCODING);
        this.contextRoot = contextRoot;
    }

    /**
     * Writes sitemap xml into stream based of the uploads data.
     *
     * @param uploads
     * @throws XMLStreamException
     */
    public void writeVoIDXml(List<UploadDTO> uploads) throws XMLStreamException {
        writer.writeStartDocument(ENCODING, "1.0");

        writer.writeStartElement(RDF_NS_PREFIX, ROOT_ELEMENT, RDF_NS);
        writer.writeNamespace(RDF_NS_PREFIX, RDF_NS);
        writer.writeNamespace(RDFS_NS_PREFIX, RDFS_NS);
        writer.writeNamespace(OWL_NS_PREFIX, OWL_NS);
        writer.writeNamespace(DCT_NS_PREFIX, DCT_NS);
        writer.writeNamespace(VOID_NS_PREFIX, VOID_NS);

        for (UploadDTO upload : uploads) {
            writer.writeStartElement(VOID_NS_PREFIX, "Dataset", VOID_NS);
            writer.writeAttribute(RDF_NS_PREFIX, RDF_NS, "about", upload.getSubjectUri());
            if (StringUtils.isNotEmpty(upload.getLabel())) {
                writer.writeStartElement(DCT_NS_PREFIX, "title", DCT_NS);
                writer.writeCharacters(upload.getLabel());
                writer.writeEndElement();

                writer.writeStartElement(RDFS_NS_PREFIX, "label", RDFS_NS);
                writer.writeCharacters(upload.getLabel());
                writer.writeEndElement();
            }
            writer.writeStartElement(VOID_NS_PREFIX, "sparqlEndpoint", VOID_NS);
            writer.writeAttribute(RDF_NS_PREFIX, RDF_NS, "resource", contextRoot + "/sparql");
            writer.writeEndElement();

            writer.writeStartElement(VOID_NS_PREFIX, "dataDump", VOID_NS);
            writer.writeAttribute(RDF_NS_PREFIX, RDF_NS, "resource",
                    contextRoot + "/exportTriples.action?uri=" + upload.getSubjectUri());
            writer.writeEndElement();

            writer.writeStartElement(VOID_NS_PREFIX, "triples", VOID_NS);
            writer.writeCharacters(upload.getTriples());
            writer.writeEndElement();

            writer.writeStartElement(DCT_NS_PREFIX, "modified", DCT_NS);
            writer.writeAttribute(RDF_NS_PREFIX, RDF_NS, "datatype", "http://www.w3.org/2001/XMLSchema#dateTime");
            writer.writeCharacters(StringUtils.substringBeforeLast(upload.getDateModified(), "."));
            writer.writeEndElement();
            writer.writeEndElement();
        }
        writer.writeEndDocument();
    }
}
